package dk.medcom.video.api.service;

import dk.medcom.video.api.PerformanceLogger;
import dk.medcom.video.api.api.*;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.*;
import dk.medcom.video.api.dao.MeetingLabelRepository;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.*;
import dk.medcom.video.api.organisation.model.OrganisationTree;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import dk.medcom.video.api.service.domain.MessageType;
import dk.medcom.video.api.service.domain.UpdateMeeting;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class MeetingServiceImpl implements MeetingService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MeetingServiceImpl.class);
	private final IdGenerator idGenerator;

	private final MeetingRepository meetingRepository;
	private final MeetingUserService meetingUserService;
	private final SchedulingInfoService schedulingInfoService;
	private final SchedulingStatusService schedulingStatusService;
	private final OrganisationService organisationService;
	private final UserContextService userService;
	private final MeetingLabelRepository meetingLabelRepository;
	private final OrganisationRepository organisationRepository;
	private final OrganisationTreeServiceClient organisationTreeServiceClient;
	private final AuditService auditService;
	private final SchedulingInfoEventPublisher schedulingInfoEventPublisher;

	public MeetingServiceImpl(MeetingRepository meetingRepository,
							  MeetingUserService meetingUserService,
							  SchedulingInfoService schedulingInfoService,
							  SchedulingStatusService schedulingStatusService,
							  OrganisationService organisationService,
							  UserContextService userService,
							  MeetingLabelRepository meetingLabelRepository,
							  OrganisationRepository organisationRepository,
							  OrganisationTreeServiceClient organisationTreeServiceClient,
							  AuditService auditClient,
							  SchedulingInfoEventPublisher schedulingInfoEventPublisher) {
	 	this.meetingRepository = meetingRepository;
	 	this.meetingUserService = meetingUserService;
	 	this.schedulingInfoService = schedulingInfoService;
	 	this.schedulingStatusService = schedulingStatusService;
	 	this.organisationService = organisationService;
	 	this.userService = userService;
		this.meetingLabelRepository = meetingLabelRepository;
		this.organisationRepository = organisationRepository;
		this.organisationTreeServiceClient = organisationTreeServiceClient;
		this.auditService = auditClient;
		this.schedulingInfoEventPublisher = schedulingInfoEventPublisher;

		this.idGenerator = new IdGeneratorImpl();
	}

	@Override
	public List<Meeting> getMeetings(Date fromStartTime, Date toStartTime) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByOrganizedByAndStartTimeBetween");
			return meetingRepository.findByOrganizedByAndStartTimeBetween(meetingUserService.getOrCreateCurrentMeetingUser(), fromStartTime, toStartTime);
		} else {
			LOGGER.debug("Finding meetings using findByOrganisationAndStartTimeBetween");
			return meetingRepository.findByOrganisationAndStartTimeBetween(organisationService.getUserOrganisation(), fromStartTime, toStartTime);	
		}
	}

	@Override
	public Meeting getMeetingByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		Meeting meeting = meetingRepository.findOneByUuid(uuid);
		if (meeting == null) {
			LOGGER.debug("The meeting was not found. UUID: " + uuid );
			throw new RessourceNotFoundException("meeting", "uuid");
		}
		if (!meeting.getOrganisation().equals(organisationService.getUserOrganisation())) {
			LOGGER.debug("The user does not have the same organization as the meeting: ");
			LOGGER.debug("The user does not have the same organization as the meeting. Calling user organization: " + organisationService.getUserOrganisation().getOrganisationId() + ", + meetingOrganizing user" + meeting.getOrganisation().getOrganisationId());
			throw new PermissionDeniedException();
		}
		
		if (userService.getUserContext().hasOnlyRole(UserRole.USER) && !(meeting.getOrganizedByUser() == meetingUserService.getOrCreateCurrentMeetingUser())) {
			LOGGER.debug("The user only has the role USER and cannot see meetings not organized by this user. Calling user: " + meetingUserService.getOrCreateCurrentMeetingUser().getEmail() + ", + meetingOrganizing user" + meeting.getOrganizedByUser().getEmail());
			throw new PermissionDeniedException();
		} 
		
		return meeting;
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public Meeting createMeeting(CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotAcceptableException, NotValidDataException  {
		var performanceLogger = new PerformanceLogger("create meeting convert");
		Meeting meeting = convert(createMeetingDto);
		meeting.setMeetingUser(meetingUserService.getOrCreateCurrentMeetingUser());
		
		if (createMeetingDto.getOrganizedByEmail() != null && !createMeetingDto.getOrganizedByEmail().isEmpty() && userService.getUserContext().isOrganisationalMeetingAdministrator()) {
			LOGGER.debug("Setting Organized By using input given user");
			meeting.setOrganizedByUser(meetingUserService.getOrCreateCurrentMeetingUser(createMeetingDto.getOrganizedByEmail()));
		} else {
			LOGGER.debug("Setting Organized By using meeting user (same as current user)");
			meeting.setOrganizedByUser(meeting.getMeetingUser());
		}
			
		Calendar calendarNow = new GregorianCalendar();
		meeting.setCreatedTime(calendarNow.getTime());

		if(createMeetingDto.getSchedulingInfoReservationId() != null && (createMeetingDto.getHostPin() != null || createMeetingDto.getGuestPin() != null)) {
			LOGGER.info("SchedulingInfoReservationId and host pin or guest pin can not be set at the same time.");
			throw new NotValidDataException(NotValidDataErrors.SCHEDULING_INFO_RESERVATION_PIN_COMBINATION);
		}

		if(createMeetingDto.getMeetingType() == MeetingType.POOL) {
			boolean isPoolOrganisation = isPoolOrganisation(userService.getUserContext().getUserOrganisation());
			if(!isPoolOrganisation) {
				throw new NotValidDataException(NotValidDataErrors.NON_AD_HOC_ORGANIZATION, meeting.getOrganisation().getOrganisationId());
			}
		}

		performanceLogger.logTimeSinceCreation();

		performanceLogger.reset("create meeting save");
		meeting = saveMeetingWithShortLink(meeting, 0);
		meetingLabelRepository.saveAll(meeting.getMeetingLabels());
		performanceLogger.logTimeSinceCreation();
		performanceLogger.reset("attach or create scheduling info");
		attachOrCreateSchedulingInfo(meeting, createMeetingDto);
		performanceLogger.logTimeSinceCreation();

		performanceLogger.reset("create meeting audit");
		auditService.auditMeeting(meeting, "create");
		performanceLogger.logTimeSinceCreation();
		return meeting;
	}

	private boolean isPoolOrganisation(String organisationCode) {
		OrganisationTree organisationTree = organisationTreeServiceClient.getOrganisationTree(organisationCode);

		var poolOrganisation = new OrganisationFinder().findPoolOrganisation(organisationCode, organisationTree);
		Organisation o = organisationRepository.findByOrganisationId(poolOrganisation.getCode());
		return o.getPoolSize() != null && o.getPoolSize() > 0;
	}

	private Meeting saveMeetingWithShortLink(Meeting meeting, int count) throws NotValidDataException {
		try {
			var performanceLogger = new PerformanceLogger("save meeting with short link to database");
			meeting.setShortId(idGenerator.generateId(UUID.randomUUID()));
			var savedResult = meetingRepository.save(meeting);
			performanceLogger.logTimeSinceCreation();
			return savedResult;
		}
		catch(DataIntegrityViolationException e) {
			if(e.getCause() instanceof ConstraintViolationException constraint) {
				if(constraint.getConstraintName().equals("short_id")) {
					if(count < 5) {
						return saveMeetingWithShortLink(meeting, ++count);
					}
				}

				if(constraint.getConstraintName().equals("organisation_external_id")) {
					throw new NotValidDataException(NotValidDataErrors.EXTERNAL_ID_NOT_UNIQUE);

				}
			}
			throw new NotValidDataException(NotValidDataErrors.UNABLE_TO_GENERATE_SHORT_ID);
		}
	}

	private void attachOrCreateSchedulingInfo(Meeting meeting, CreateMeetingDto createMeetingDto) throws NotAcceptableException, PermissionDeniedException, NotValidDataException {
		var performanceLogger = new PerformanceLogger("attachOrCreateSchedulingInfo");

		// Custom URI
		if(createMeetingDto.getUriWithoutDomain() != null) {
			if(organisationService.getUserOrganisation().getAllowCustomUriWithoutDomain()) {
				LOGGER.info("Using custom uriWithoutDomain for meeting in organisation {}", organisationService.getUserOrganisation().getOrganisationId());
				schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);
				return;
			}
			else {
				LOGGER.info("Organisation not configured to allow custom uri without domain. Organisation: {}, uriWithoutDomain: {}.",
							organisationService.getUserOrganisation().getOrganisationId(),
							createMeetingDto.getUriWithoutDomain()
				);
				throw new NotValidDataException(NotValidDataErrors.CUSTOM_MEETING_ADDRESS_NOT_ALLOWED);
			}
		}
		performanceLogger.logTimeSinceCreation();
		performanceLogger.reset("attachOrCreateSchedulingInfo.check pin");
		// If host pin or guest pin used we can not use pool scheduling info.
		if(createMeetingDto.getHostPin() != null || createMeetingDto.getGuestPin() != null) {
			LOGGER.info("Using custom guest pin ({}) or host pin ({}) for meeting.", createMeetingDto.getGuestPin(), createMeetingDto.getHostPin());
			schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);
			return;
		}

		performanceLogger.logTimeSinceCreation();
		performanceLogger.reset("attachOrCreateSchedulingInfo.reservation id");
		if(createMeetingDto.getSchedulingInfoReservationId() != null) {
			attachReservedSchedulingInfo(meeting, createMeetingDto);
			return;
		}
		performanceLogger.logTimeSinceCreation();
		performanceLogger.reset("attachOrCreateSchedulingInfo. future meeting");

		if(isFutureMeeting(createMeetingDto)) {
			schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);
			return;
		}
		performanceLogger.logTimeSinceCreation();
		performanceLogger.reset("attachOrCreateSchedulingInfo.scheduling info attach meeting.");

		SchedulingInfo schedulingInfo = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, createMeetingDto);
		performanceLogger.logTimeSinceCreation();

		performanceLogger.reset("attachOrCreateSchedulingInfo.scheduling info create");
		if(createMeetingDto.getMeetingType() == MeetingType.POOL) {
			if(schedulingInfo == null) {
				throw new NotValidDataException(NotValidDataErrors.SCHEDULING_INFO_NOT_FOUND_ORGANISATION, meeting.getOrganisation().getOrganisationId());
			}
		}
		else {
			if(schedulingInfo == null) {
				schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);
			}
		}

		performanceLogger.logTimeSinceCreation();
	}

	private void attachReservedSchedulingInfo(Meeting meeting, CreateMeetingDto createMeetingDto) throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
		var performanceLogger = new PerformanceLogger("attach reserved scheduling info");
		try {
			var schedulingInfo = schedulingInfoService.getSchedulingInfoByReservation(createMeetingDto.getSchedulingInfoReservationId());

			if(!schedulingInfo.getOrganisation().getOrganisationId().equals(userService.getUserContext().getUserOrganisation())) {
				LOGGER.info("ReservationId {} belongs to organisation {} and user organisation is {}.", createMeetingDto.getSchedulingInfoReservationId(), schedulingInfo.getOrganisation().getOrganisationId(), userService.getUserContext().getUserOrganisation());
				throw new NotValidDataException(NotValidDataErrors.INVALID_RESERVATION_ID, createMeetingDto.getSchedulingInfoReservationId().toString());
			}

			schedulingInfoService.attachMeetingToSchedulingInfo(meeting, schedulingInfo, false);
		}
		catch(RessourceNotFoundException e) {
			LOGGER.info("ReservationId {} not found.", createMeetingDto.getSchedulingInfoReservationId());
			throw new NotValidDataException(NotValidDataErrors.INVALID_RESERVATION_ID, createMeetingDto.getSchedulingInfoReservationId().toString());
		}
		finally {
			performanceLogger.logTimeSinceCreation();
		}
	}

	private boolean isFutureMeeting(CreateMeetingDto createMeetingDto) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, 2);

		return createMeetingDto.getStartTime().after(now.getTime());
	}

	@Override
	public Meeting convert(CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotValidDataException {
		validateDate(createMeetingDto.getStartTime());
		validateDate(createMeetingDto.getEndTime());
		
		Meeting meeting = new Meeting();
		meeting.setSubject(createMeetingDto.getSubject());
		if(createMeetingDto.getUuid() != null) {
			meeting.setUuid(createMeetingDto.getUuid().toString());
		}
		else {
			meeting.setUuid(UUID.randomUUID().toString());
		}
		meeting.setOrganisation(organisationService.getUserOrganisation());
		meeting.setStartTime(createMeetingDto.getStartTime());
		meeting.setEndTime(createMeetingDto.getEndTime());
		meeting.setDescription(createMeetingDto.getDescription());
		meeting.setProjectCode(createMeetingDto.getProjectCode());
		meeting.setGuestPinRequired(createMeetingDto.getGuestPinRequired());

		createMeetingDto.getLabels().forEach(x -> {
			MeetingLabel meetingLabel = new MeetingLabel();
			meetingLabel.setLabel(x);

			meeting.addMeetingLabel(meetingLabel);
		});

		meeting.setExternalId(createMeetingDto.getExternalId());

		if(createMeetingDto.getGuestMicrophone() != null){
			meeting.setGuestMicrophone(createMeetingDto.getGuestMicrophone());
		} else {
			meeting.setGuestMicrophone(GuestMicrophone.on);
		}

		return meeting;
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public Meeting updateMeeting(String uuid, UpdateMeetingDto updateMeetingDto) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		Meeting meeting = getMeetingByUuid(uuid);
		var schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(meeting.getUuid());

		var domainMapper = new DomainMapper();
		return updateMeeting(uuid, domainMapper.mapToUpdateMeeting(updateMeetingDto, meeting, schedulingInfo), meeting);
	}

	private Meeting updateMeeting(String uuid, UpdateMeeting updateMeetingDto, Meeting meeting) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(uuid);
		if (schedulingInfo.getProvisionStatus() != ProvisionStatus.AWAITS_PROVISION && schedulingInfo.getProvisionStatus() != ProvisionStatus.PROVISIONED_OK) {
			LOGGER.debug("Meeting does not have the correct Status for updating. Meeting status is: " + schedulingInfo.getProvisionStatus().getValue());
			throw new NotAcceptableException(NotAcceptableErrors.MUST_HAVE_STATUS_AWAITS_PROVISION_OR_PROVISIONED_OK);
		}
		
		validateDate(updateMeetingDto.getEndTime());
		Integer poolSize = organisationService.getPoolSizeForOrganisation(schedulingInfo.getOrganisation().getOrganisationId());
		if (schedulingInfo.getProvisionStatus() == ProvisionStatus.AWAITS_PROVISION ||
				(schedulingInfo.getProvisionStatus() == ProvisionStatus.PROVISIONED_OK && poolSize != null)) {  //only when this status must all but endTime be updated
			validateDate(updateMeetingDto.getStartTime());
			
			meeting.setSubject(updateMeetingDto.getSubject());
			meeting.setStartTime(updateMeetingDto.getStartTime());
			meeting.setDescription(updateMeetingDto.getDescription());
			meeting.setProjectCode(updateMeetingDto.getProjectCode());
			
			if (updateMeetingDto.getOrganizedByEmail() != null && !updateMeetingDto.getOrganizedByEmail().isEmpty() && userService.getUserContext().isOrganisationalMeetingAdministrator()) {
				LOGGER.debug("Setting Organized By using input given user");
				meeting.setOrganizedByUser(meetingUserService.getOrCreateCurrentMeetingUser(updateMeetingDto.getOrganizedByEmail()));
			} else {
				LOGGER.debug("Setting Organized By using current user");
				meeting.setOrganizedByUser(meetingUserService.getOrCreateCurrentMeetingUser());
			}
			
		} 
		meeting.setEndTime(updateMeetingDto.getEndTime());		

		Calendar calendarNow = new GregorianCalendar();
		meeting.setUpdatedTime(calendarNow.getTime());
		meeting.setUpdatedByUser(meetingUserService.getOrCreateCurrentMeetingUser());
		meeting.setGuestMicrophone(GuestMicrophone.valueOf(updateMeetingDto.getGuestMicrophone().name()));
		meeting.setGuestPinRequired(updateMeetingDto.isGuestPinRequired());

		meeting = meetingRepository.save(meeting);

		meetingLabelRepository.deleteByMeeting(meeting);

		List<MeetingLabel> meetingLabels = new ArrayList<>();
		Meeting finalMeeting = meeting;
		updateMeetingDto.getLabels().forEach(x -> {
			MeetingLabel meetingLabel = new MeetingLabel();
			meetingLabel.setLabel(x);
			meetingLabel.setMeeting(finalMeeting);

			meetingLabels.add(meetingLabel);
		});

		meetingLabelRepository.saveAll(meetingLabels);

		if (schedulingInfo.getProvisionStatus() == ProvisionStatus.AWAITS_PROVISION) {
			LOGGER.debug("Start time and pin codes is allowed to be updated, because booking has status AWAITS_PROVISION");
			schedulingInfoService.updateSchedulingInfo(uuid,
					meeting.getStartTime(),
					updateMeetingDto.getHostPin() != null ? updateMeetingDto.getHostPin().longValue() : null,
					updateMeetingDto.getGuestPin() != null ? updateMeetingDto.getGuestPin().longValue() : null);
		}
		else {
			var event = SchedulingInfoEventMapper.map(schedulingInfo, MessageType.UPDATE);
			schedulingInfoEventPublisher.publishCreate(event);
		}

		auditService.auditMeeting(meeting, "update");
		return meeting;
	}

	@Transactional
	@Override
	public void deleteMeeting(String uuid) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException {
		Meeting meeting = getMeetingByUuid(uuid);
		
		SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(uuid);
		if (schedulingInfo.getProvisionStatus() != ProvisionStatus.AWAITS_PROVISION) {
			LOGGER.debug("Meeting does not have the correct Status for deletion. Meeting status is: " + schedulingInfo.getProvisionStatus().getValue());
			throw new NotAcceptableException(NotAcceptableErrors.MUST_HAVE_STATUS_AWAITS_PROVISION);
		}

		auditService.auditMeeting(meeting, "delete");
		schedulingInfoService.deleteSchedulingInfo(uuid);
		schedulingStatusService.deleteSchedulingStatus(meeting);
		meetingLabelRepository.deleteByMeeting(meeting);
		meetingRepository.delete(meeting);

	}

	private void validateDate(Date date) throws NotValidDataException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (calendar.get(Calendar.YEAR) > 9999) {
			LOGGER.debug("Date must be less than 9999 but is not. Actual value is: " + calendar.get(Calendar.YEAR));
			throw new NotValidDataException(NotValidDataErrors.DATA_FORMAT_WRONG);
		}
	}

	@Override
	public List<Meeting> getMeetingsBySubject(String subject) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByOrganizedByAndStartTimeBetween");
			return meetingRepository.findByOrganizedByAndSubject(meetingUserService.getOrCreateCurrentMeetingUser(),subject);
		} else {
			LOGGER.debug("Finding meetings using findByOrganisationAndStartTimeBetween");
			return meetingRepository.findByOrganisationAndSubject(organisationService.getUserOrganisation(),subject);
		}
	}

	private List<Meeting> getMeetingsBySubjectLikeOrDescriptionLike(String searchString) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByOrganizedByAndSubjectLike");
			return meetingRepository.findByOrganizedByAndSubjectLikeOrDescriptionLike(meetingUserService.getOrCreateCurrentMeetingUser(), searchString, searchString);
		} else {
			LOGGER.debug("Finding meetings using findByOrganisationAndSubjectLike");
			return meetingRepository.findByOrganisationAndSubjectLikeOrDescriptionLike(organisationService.getUserOrganisation(), searchString, searchString);
		}
	}

	@Override
	public List<Meeting> getMeetingsByOrganizedBy(String organizedBy) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByOrganizedBy");

			MeetingUser meetingUser = meetingUserService.getOrCreateCurrentMeetingUser();
			if(organizedBy.equals(meetingUser.getEmail())) {
				return meetingRepository.findByOrganizedBy(meetingUser);
			}

			LOGGER.debug("Returning empty list as query email is not same as context email.");
			return Collections.emptyList();
		} else {
			LOGGER.debug("Finding meetings using findByOrganisationAndOrganizedBy");
			MeetingUser meetingUser = meetingUserService.getOrCreateCurrentMeetingUser(organizedBy);
			return meetingRepository.findByOrganisationAndOrganizedBy(organisationService.getUserOrganisation(), meetingUser);
		}
	}

	@Override
	public List<Meeting> getMeetingsByUriWithDomain(String uriWithDomain) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByUriWithDomainAndOrganizedBy");
			return meetingRepository.findByUriWithDomainAndOrganizedBy(meetingUserService.getOrCreateCurrentMeetingUser(), uriWithDomain);
		} else {
			LOGGER.debug("Finding meetings using findByUriWithDomainAndOrganisation");
			return meetingRepository.findByUriWithDomainAndOrganisation(organisationService.getUserOrganisation(), uriWithDomain);
		}
	}

	@Override
	public Meeting getMeetingsByUriWithDomainSingle(String uriWithDomain) throws PermissionDeniedException, RessourceNotFoundException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByUriWithDomainAndOrganizedBy");
			var result =  meetingRepository.findOneByUriWithDomainAndOrganizedBy(meetingUserService.getOrCreateCurrentMeetingUser(), uriWithDomain);

			if(result == null) {
				throw new RessourceNotFoundException("meeting", "uriWithDomain");
			}

			return result;
		} else {
			LOGGER.debug("Finding meetings using findByUriWithDomainAndOrganisation");
			var result = meetingRepository.findOneByUriWithDomainAndOrganisation(organisationService.getUserOrganisation(), uriWithDomain);

			if(result == null) {
				throw new RessourceNotFoundException("meeting", "uriWithDomain");
			}

			return result;
		}
	}

	@Override
	public Meeting getMeetingsByUriWithoutDomain(String uriWithoutDomain) throws PermissionDeniedException, RessourceNotFoundException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByUriWithoutDomainAndOrganizedBy");
			var result =  meetingRepository.findOneByUriWithoutDomainAndOrganizedBy(meetingUserService.getOrCreateCurrentMeetingUser(), uriWithoutDomain);

			if(result == null) {
				throw new RessourceNotFoundException("meeting", "uriWithDomain");
			}

			return result;

		} else {
			LOGGER.debug("Finding meetings using findByUriWithoutDomainAndOrganisation");
			var result =  meetingRepository.findOneByUriWithoutDomainAndOrganisation(organisationService.getUserOrganisation(), uriWithoutDomain);

			if(result == null) {
				throw new RessourceNotFoundException("meeting", "uriWithDomain");
			}

			return result;
		}
	}

	@Override
	public List<Meeting> getMeetingsByLabel(String label) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByLabelAndOrganizedBy");
			return meetingRepository.findByLabelAndOrganizedBy(meetingUserService.getOrCreateCurrentMeetingUser(), label);
		} else {
			LOGGER.debug("Finding meetings using findByLabelAndOrganisation");
			return meetingRepository.findByLabelAndOrganisation(organisationService.getUserOrganisation(), label);
		}
	}

	@Override
    public List<Meeting> searchMeetings(String search, Date fromStartTime, Date toStartTime) throws PermissionDeniedException {
		Map<Long, Meeting> distinctMeetnings = new HashMap<>();

		List<Meeting> meetings = getMeetingsByOrganizedBy(search);
		meetings.addAll(getMeetingsByLabel(search));
		meetings.addAll(getMeetingsBySubjectLikeOrDescriptionLike(String.format("%%%s%%", search)));
		meetings.addAll(getMeetingsByUriWithDomain(search));

		meetings.forEach(meeting -> {
			boolean includeMeeting = true;
			if(fromStartTime != null && toStartTime != null) {
				if (!fromStartTime.before(meeting.getStartTime()) || !toStartTime.after(meeting.getStartTime())) {
					includeMeeting = false;
				}
			}

			if(includeMeeting) {
				distinctMeetnings.putIfAbsent(meeting.getId(), meeting);
			}
		});

		return new ArrayList<>(distinctMeetnings.values());
    }

	@Override
	public Meeting getMeetingByShortId(String shortId) throws RessourceNotFoundException, PermissionDeniedException {
		Meeting meeting = meetingRepository.findOneByShortId(shortId);
		if (meeting == null) {
			LOGGER.debug("The meeting was not found. Short Id: " + shortId );
			throw new RessourceNotFoundException("meeting", "shortId");
		}

		if(userService.getUserContext().hasOnlyRole(UserRole.ADMIN)) {
			LOGGER.info("Search short-id called with ADMIN role.");
			return meeting;
		}

		if (!meeting.getOrganisation().equals(organisationService.getUserOrganisation())) {
			LOGGER.debug("The user does not have the same organization as the meeting: ");
			LOGGER.debug("The user does not have the same organization as the meeting. Calling user organization: " + organisationService.getUserOrganisation().getOrganisationId() + ", + meetingOrganizing user" + meeting.getOrganisation().getOrganisationId());
			throw new PermissionDeniedException();
		}

		if (userService.getUserContext().hasOnlyRole(UserRole.USER) && !(meeting.getOrganizedByUser() == meetingUserService.getOrCreateCurrentMeetingUser())) {
			LOGGER.debug("The user only has the role USER and cannot see meetings not organized by this user. Calling user: " + meetingUserService.getOrCreateCurrentMeetingUser().getEmail() + ", + meetingOrganizing user" + meeting.getOrganizedByUser().getEmail());
			throw new PermissionDeniedException();
		}

		return meeting;
	}

	@Transactional
	@Override
    public Meeting patchMeeting(UUID uuid, PatchMeetingDto patchMeetingDto) throws PermissionDeniedException, NotValidDataException, RessourceNotFoundException, NotAcceptableException {
		var	 meeting = getMeetingByUuid(uuid.toString());
		var schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(meeting.getUuid());

		var domainMapper = new DomainMapper();
		return updateMeeting(uuid.toString(), domainMapper.mapToUpdateMeeting(patchMeetingDto, meeting, schedulingInfo), meeting);
    }
}
