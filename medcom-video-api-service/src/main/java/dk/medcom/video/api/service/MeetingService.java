package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingLabel;
import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.MeetingType;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.UpdateMeetingDto;
import dk.medcom.video.api.repository.MeetingLabelRepository;
import dk.medcom.video.api.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Component
public class MeetingService {

	private static Logger LOGGER = LoggerFactory.getLogger(MeetingService.class);
	
	private MeetingRepository meetingRepository;
	private MeetingUserService meetingUserService;
	private SchedulingInfoService schedulingInfoService;
	private SchedulingStatusService schedulingStatusService;
	private OrganisationService organisationService;
	private UserContextService userService;
	private MeetingLabelRepository meetingLabelRepository;

	MeetingService(MeetingRepository meetingRepository,
				   MeetingUserService meetingUserService,
				   SchedulingInfoService schedulingInfoService,
				   SchedulingStatusService schedulingStatusService,
				   OrganisationService organisationService,
				   UserContextService userService,
				   MeetingLabelRepository meetingLabelRepository) {
	 	this.meetingRepository = meetingRepository;
	 	this.meetingUserService = meetingUserService;
	 	this.schedulingInfoService = schedulingInfoService;
	 	this.schedulingStatusService = schedulingStatusService;
	 	this.organisationService = organisationService;
	 	this.userService = userService;
		this.meetingLabelRepository = meetingLabelRepository;
	}
	
	public List<Meeting> getMeetings(Date fromStartTime, Date toStartTime) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByOrganizedByAndStartTimeBetween");
			return meetingRepository.findByOrganizedByAndStartTimeBetween(meetingUserService.getOrCreateCurrentMeetingUser(), fromStartTime, toStartTime);
		} else {
			LOGGER.debug("Finding meetings using findByOrganisationAndStartTimeBetween");
			return meetingRepository.findByOrganisationAndStartTimeBetween(organisationService.getUserOrganisation(), fromStartTime, toStartTime);	
		}
		
	}

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
	public Meeting createMeeting(CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotAcceptableException, NotValidDataException  {
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
//		meeting.setUpdatedTime(calendarNow.getTime());
//		meeting.setUpdatedByUser(meetingUserService.getOrCreateCurrentMeetingUser());

		if(meeting.getOrganisation().getPoolSize() == null && createMeetingDto.getMeetingType() == MeetingType.POOL) {
			throw new NotValidDataException("Can not create ad hoc meeting on non ad hoc organization: " + meeting.getOrganisation().getOrganisationId());
		}

		meeting = meetingRepository.save(meeting);
		meetingLabelRepository.save(meeting.getMeetingLabels());

		attachOrCreateSchedulingInfo(meeting, createMeetingDto);

		return meeting;
	}

	private void attachOrCreateSchedulingInfo(Meeting meeting, CreateMeetingDto createMeetingDto) throws NotAcceptableException, PermissionDeniedException, NotValidDataException {
		if(isFutureMeeting(createMeetingDto)) {
			schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);
			return;
		}

		SchedulingInfo schedulingInfo = schedulingInfoService.attachMeetingToSchedulingInfo(meeting);

		if(createMeetingDto.getMeetingType() == MeetingType.POOL) {
			if(schedulingInfo == null) {
				throw new NotValidDataException("Unused scheduling information not found for organisation " + meeting.getOrganisation().getOrganisationId());
			}
		}
		else {
			if(schedulingInfo == null) {
				schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);
			}
		}
	}

	private boolean isFutureMeeting(CreateMeetingDto createMeetingDto) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, 1);

		return createMeetingDto.getStartTime().after(now.getTime());
	}

	Meeting convert(CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotValidDataException {
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

		createMeetingDto.getLabels().forEach(x -> {
			MeetingLabel meetingLabel = new MeetingLabel();
			meetingLabel.setLabel(x);

			meeting.addMeetingLabel(meetingLabel);
		});

		return meeting;
	}

	@Transactional(rollbackFor = Throwable.class)
	public Meeting updateMeeting(String uuid, UpdateMeetingDto updateMeetingDto) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		Meeting meeting = getMeetingByUuid(uuid);
				
		SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(uuid);
		if (schedulingInfo.getProvisionStatus() != ProvisionStatus.AWAITS_PROVISION && schedulingInfo.getProvisionStatus() != ProvisionStatus.PROVISIONED_OK) {
			LOGGER.debug("Meeting does not have the correct Status for updating. Meeting status is: " + schedulingInfo.getProvisionStatus().getValue());
			throw new NotAcceptableException("Meeting must have status AWAITS_PROVISION (0)  or PROVISIONED_OK (3) in order to be updated");
		}
		
		validateDate(updateMeetingDto.getEndTime());
		if (schedulingInfo.getProvisionStatus() == ProvisionStatus.AWAITS_PROVISION ||
				(schedulingInfo.getProvisionStatus() == ProvisionStatus.PROVISIONED_OK && schedulingInfo.getOrganisation().getPoolSize() != null)) {  //only when this status must all but endTime be updated
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

		meetingLabelRepository.save(meetingLabels);

		if (schedulingInfo.getProvisionStatus() == ProvisionStatus.AWAITS_PROVISION) {
			LOGGER.debug("Start time is allowed to be updated, because booking has status AWAITS_PROVISION");
			schedulingInfoService.updateSchedulingInfo(uuid, meeting.getStartTime());
		}
		return meeting;
	}
	
	public void deleteMeeting(String uuid) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException {
		Meeting meeting = getMeetingByUuid(uuid);
		
		SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(uuid);
		if (schedulingInfo.getProvisionStatus() != ProvisionStatus.AWAITS_PROVISION) {
			LOGGER.debug("Meeting does not have the correct Status for deletion. Meeting status is: " + schedulingInfo.getProvisionStatus().getValue());
			throw new NotAcceptableException("Meeting must have status AWAITS_PROVISION (0) in order to be deleted");
		}

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
			throw new NotValidDataException("Date format is wrong, year must only have 4 digits");
		}
	}

	public List<Meeting> getMeetingsBySubject(String subject) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByOrganizedByAndStartTimeBetween");
			return meetingRepository.findByOrganizedByAndSubject(meetingUserService.getOrCreateCurrentMeetingUser(),subject);
		} else {
			LOGGER.debug("Finding meetings using findByOrganisationAndStartTimeBetween");
			return meetingRepository.findByOrganisationAndSubject(organisationService.getUserOrganisation(),subject);
		}
	}

	public List<Meeting> getMeetingsBySubjectLikeOrDescriptionLike(String searchString) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByOrganizedByAndSubjectLike");
			return meetingRepository.findByOrganizedByAndSubjectLikeOrDescriptionLike(meetingUserService.getOrCreateCurrentMeetingUser(), searchString, searchString);
		} else {
			LOGGER.debug("Finding meetings using findByOrganisationAndSubjectLike");
			return meetingRepository.findByOrganisationAndSubjectLikeOrDescriptionLike(organisationService.getUserOrganisation(), searchString, searchString);
		}
	}

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

	public List<Meeting> getMeetingsByUriWithDomain(String uriWithDomain) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByUriWithDomainAndOrganizedBy");
			return meetingRepository.findByUriWithDomainAndOrganizedBy(meetingUserService.getOrCreateCurrentMeetingUser(), uriWithDomain);
		} else {
			LOGGER.debug("Finding meetings using findByUriWithDomainAndOrganisation");
			return meetingRepository.findByUriWithDomainAndOrganisation(organisationService.getUserOrganisation(), uriWithDomain);
		}
	}

	public List<Meeting> getMeetingsByLabel(String label) throws PermissionDeniedException {
		if (userService.getUserContext().hasOnlyRole(UserRole.USER)) {
			LOGGER.debug("Finding meetings using findByLabelAndOrganizedBy");
			return meetingRepository.findByLabelAndOrganizedBy(meetingUserService.getOrCreateCurrentMeetingUser(), label);
		} else {
			LOGGER.debug("Finding meetings using findByLabelAndOrganisation");
			return meetingRepository.findByLabelAndOrganisation(organisationService.getUserOrganisation(), label);
		}
	}

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
}
