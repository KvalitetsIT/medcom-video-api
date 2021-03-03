package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.*;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.api.*;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.organisation.OrganisationTree;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SchedulingInfoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoService.class);

	private final SchedulingInfoRepository schedulingInfoRepository;
	private final SchedulingTemplateRepository schedulingTemplateRepository;
	private final SchedulingTemplateService schedulingTemplateService;
	private final SchedulingStatusService schedulingStatusService;
	private final MeetingUserService meetingUserService;
	private final OrganisationRepository organisationRepository;
	private final OrganisationStrategy organisationStrategy;
	private final UserContextService userContextService;
	private final String overflowPoolOrganisationId;
	private final OrganisationTreeServiceClient organisationTreeServiceClient;

	@Value("${scheduling.info.citizen.portal}")
	private String citizenPortal;		
	
	public SchedulingInfoService(SchedulingInfoRepository schedulingInfoRepository,
								 SchedulingTemplateRepository schedulingTemplateRepository,
								 SchedulingTemplateService schedulingTemplateService,
								 SchedulingStatusService schedulingStatusService,
								 MeetingUserService meetingUserService,
								 OrganisationRepository organisationRepository,
								 OrganisationStrategy organisationStrategy,
								 UserContextService userContextService,
								@Value("${overflow.pool.organisation.id}") String overflowPoolOrganisationId,
								 OrganisationTreeServiceClient organisationTreeServiceClient) {
		this.schedulingInfoRepository = schedulingInfoRepository;
		this.schedulingTemplateRepository = schedulingTemplateRepository;
		this.schedulingTemplateService = schedulingTemplateService;
		this.schedulingStatusService = schedulingStatusService;
		this.meetingUserService = meetingUserService;
		this.organisationRepository = organisationRepository;
		this.organisationStrategy = organisationStrategy;
		this.userContextService = userContextService;
		this.organisationTreeServiceClient = organisationTreeServiceClient;

		if(overflowPoolOrganisationId == null)  {
			throw new RuntimeException("overflow.pool.organisation.id not set.");
		}
		this.overflowPoolOrganisationId = overflowPoolOrganisationId;
	}

	public List<SchedulingInfo> getSchedulingInfo(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus) {
		return schedulingInfoRepository.findAllWithinAdjustedTimeIntervalAndStatus(fromStartTime, toEndTime, provisionStatus);
	}

	public List<SchedulingInfo> getSchedulingInfoAwaitsProvision() {
		return schedulingInfoRepository.findAllWithinStartAndEndTimeLessThenAndStatus(new Date(), ProvisionStatus.AWAITS_PROVISION);
	}

	public List<SchedulingInfo> getSchedulingInfoAwaitsDeProvision() {
		return schedulingInfoRepository.findAllWithinEndTimeLessThenAndStatus(new Date(), ProvisionStatus.PROVISIONED_OK);
	}

	public SchedulingInfo getSchedulingInfoByUuid(String uuid) throws RessourceNotFoundException {
		LOGGER.debug("Entry getSchedulingInfoByUuid. uuid=" + uuid);
		SchedulingInfo schedulingInfo = schedulingInfoRepository.findOneByUuid(uuid);
		if (schedulingInfo == null) {
			LOGGER.debug("SchedulingInfo was null");
			throw new RessourceNotFoundException("schedulingInfo", "uuid");
		}
		LOGGER.debug("Exit getSchedulingInfoByUuid");
		return schedulingInfo;
	}

	@Transactional(rollbackFor = Throwable.class)
	public SchedulingInfo createSchedulingInfo(Meeting meeting, CreateMeetingDto createMeetingDto) throws NotAcceptableException, PermissionDeniedException {
		LOGGER.debug("Entry createSchedulingInfo");
		SchedulingTemplate schedulingTemplate = null;
		SchedulingInfo schedulingInfo = new SchedulingInfo();
		
		//if template is input and is related to the users organisation use that. Otherwise find default.
		if (createMeetingDto.getSchedulingTemplateId() != null && createMeetingDto.getSchedulingTemplateId() > 0 ) {
			LOGGER.debug("Searching for schedulingTemplate using id: " + createMeetingDto.getSchedulingTemplateId());
			try {
				schedulingTemplate = schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(createMeetingDto.getSchedulingTemplateId());
			} catch (RessourceNotFoundException e) {
				LOGGER.debug("The template was not found using Organization and id");
				//Do nothing. More logic below
			} 
		}
		if (schedulingTemplate == null) {
			LOGGER.debug("Searching for schedulingTemplate");
			schedulingTemplate = schedulingTemplateService.getDefaultSchedulingTemplate();
		}
		LOGGER.debug("Found schedulingTemplate: " + schedulingTemplate.toString());
		
		schedulingInfo.setUuid(meeting.getUuid());
		schedulingInfo.setHostPin(createHostPin(schedulingTemplate));
		schedulingInfo.setGuestPin(createGuestPin(schedulingTemplate));

		schedulingInfo.setVMRAvailableBefore(schedulingTemplate.getVMRAvailableBefore());
		Calendar cal = Calendar.getInstance();
		cal.setTime(meeting.getStartTime());
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - schedulingInfo.getVMRAvailableBefore());
		schedulingInfo.setvMRStartTime(cal.getTime());
		
		schedulingInfo.setIvrTheme(schedulingTemplate.getIvrTheme());  //example: /api/admin/configuration/v1/ivr_theme/10/

		String randomUri = generateUriWithoutDomain(schedulingTemplate);
		schedulingInfo.setUriWithoutDomain(randomUri);
		schedulingInfo.setUriWithDomain(schedulingTemplate.getUriPrefix() + schedulingInfo.getUriWithoutDomain() + "@" + schedulingTemplate.getUriDomain());

		schedulingInfo.setMeeting(meeting);
		schedulingInfo.setOrganisation(meeting.getOrganisation());

		schedulingInfo.setPortalLink(createPortalLink(meeting.getStartTime(), schedulingInfo));
		
		
		//Overwrite template value with input parameters 
		if (createMeetingDto.getMaxParticipants() > 0) { 
			schedulingInfo.setMaxParticipants(createMeetingDto.getMaxParticipants());
			LOGGER.debug("MaxParticipants is taken from input: " + createMeetingDto.getMaxParticipants());
		} else {
			schedulingInfo.setMaxParticipants(schedulingTemplate.getMaxParticipants());	
		}
		if (createMeetingDto.isEndMeetingOnEndTime() != null) {
			schedulingInfo.setEndMeetingOnEndTime(createMeetingDto.isEndMeetingOnEndTime());
			LOGGER.debug("EndMeetingOnTime is taken from input: " + createMeetingDto.isEndMeetingOnEndTime().toString());
		} else {
			schedulingInfo.setEndMeetingOnEndTime(schedulingTemplate.getEndMeetingOnEndTime());	
		}
		
		schedulingInfo.setSchedulingTemplate(schedulingTemplate);
		schedulingInfo.setProvisionStatus(ProvisionStatus.AWAITS_PROVISION);
		
		schedulingInfo.setMeetingUser(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingInfo.setCreatedTime(calendarNow.getTime());

		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);

		LOGGER.debug("Exit createSchedulingInfo");
		return schedulingInfo;
	}

	private String generateUriWithoutDomain(SchedulingTemplate schedulingTemplate) throws NotAcceptableException {
		String randomUri;
		int whileCount = 0;
		int whileMax = 100;

		SchedulingInfo schedulingInfoUri;

		if (!(schedulingTemplate.getUriNumberRangeLow() < schedulingTemplate.getUriNumberRangeHigh())) {
			LOGGER.debug("The Uri assignment failed due to invalid setup on the template used.");
			throw new NotAcceptableException(NotAcceptableErrors.URI_ASSIGNMENT_FAILED_INVALID_TEMPLATE_USED);
		}
		do {            //loop x number of times until a no-duplicate url is found
			randomUri = String.valueOf(ThreadLocalRandom.current().nextLong(schedulingTemplate.getUriNumberRangeLow(), schedulingTemplate.getUriNumberRangeHigh()));
			schedulingInfoUri = schedulingInfoRepository.findOneByUriWithoutDomain(randomUri);
		} while (schedulingInfoUri != null && whileCount++ < whileMax);
		if (whileCount > whileMax) {
			LOGGER.debug("The Uri assignment failed. It was not possible to create a unique. Consider changing the interval on the template ");
			throw new NotAcceptableException(NotAcceptableErrors.URI_ASSIGNMENT_FAILED_NOT_POSSIBLE_TO_CREATE_UNIQUE);
		}

		return randomUri;
	}

	private Long createGuestPin(SchedulingTemplate schedulingTemplate) throws NotAcceptableException {
		if (schedulingTemplate.getGuestPinRequired()) {
			LOGGER.debug("GuestPin is required");
			if (schedulingTemplate.getGuestPinRangeLow() != null && schedulingTemplate.getGuestPinRangeHigh() != null &&
					schedulingTemplate.getGuestPinRangeLow() < schedulingTemplate.getGuestPinRangeHigh()) {
				return ThreadLocalRandom.current().nextLong(schedulingTemplate.getGuestPinRangeLow(), schedulingTemplate.getGuestPinRangeHigh());
			} else {
				LOGGER.debug("The guest pincode assignment failed due to invalid setup on the template used");
				throw new NotAcceptableException(NotAcceptableErrors.GUEST_PINCODE_ASSIGNMENT_FAILED);
			}
		}

		return null;
	}

	private Long createHostPin(SchedulingTemplate schedulingTemplate) throws NotAcceptableException {
		if (schedulingTemplate.getHostPinRequired()) {
			LOGGER.debug("HostPin is required");
			if (schedulingTemplate.getHostPinRangeLow() != null && schedulingTemplate.getHostPinRangeHigh() != null &&
					schedulingTemplate.getHostPinRangeLow() < schedulingTemplate.getHostPinRangeHigh()) {
				return ThreadLocalRandom.current().nextLong(schedulingTemplate.getHostPinRangeLow(), schedulingTemplate.getHostPinRangeHigh());
			} else {
				LOGGER.debug("The host pincode assignment failed due to invalid setup on the template used.");
				throw new NotAcceptableException(NotAcceptableErrors.HOST_PINCODE_ASSIGNMENT_FAILED);
			}
		}

		return null;
	}

	@Transactional(rollbackFor = Throwable.class)
	public SchedulingInfo updateSchedulingInfo(String uuid, UpdateSchedulingInfoDto updateSchedulingInfoDto) throws RessourceNotFoundException, PermissionDeniedException {
		LOGGER.debug("Entry updateSchedulingInfo. uuid/updateSchedulingInfoDto. uuid=" + uuid);
		SchedulingInfo schedulingInfo = getSchedulingInfoByUuid(uuid);
	//	LOGGER.debug("shedulingInfo found is with uuid:" + schedulingInfo.getUuid());
		schedulingInfo.setProvisionStatus(updateSchedulingInfoDto.getProvisionStatus());
		schedulingInfo.setProvisionStatusDescription(updateSchedulingInfoDto.getProvisionStatusDescription());
		schedulingInfo.setProvisionTimestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
		
		//Removed UUID validation again		
//		try{
//			if (updateSchedulingInfoDto.getProvisionVmrId() != null) {
//				UUID uuidChk = UUID.fromString(updateSchedulingInfoDto.getProvisionVmrId());
//			}
		schedulingInfo.setProvisionVMRId(updateSchedulingInfoDto.getProvisionVmrId());
//		} catch (IllegalArgumentException exception) {
//			throw new NotValidDataException("provisionVmrId must have uuid format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
//		}
		
		schedulingInfo.setUpdatedByUser(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingInfo.setUpdatedTime(calendarNow.getTime());
		if(schedulingInfo.getProvisionStatus() == ProvisionStatus.DEPROVISION_OK) {
			schedulingInfo.setUriWithoutDomain(null);
		}

		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		schedulingStatusService.createSchedulingStatus(schedulingInfo);
		
		LOGGER.debug("Exit updateSchedulingInfo");
		return schedulingInfo;
	}
	
	//used by meetingService to update VMRStarttime and portalLink because it depends on the meetings starttime
	@Transactional(rollbackFor = Throwable.class)
	public SchedulingInfo updateSchedulingInfo(String uuid, Date startTime) throws RessourceNotFoundException, PermissionDeniedException{
		LOGGER.debug("Entry updateSchedulingInfo. uuid/startTime. uuid=" + uuid);
		
		SchedulingInfo schedulingInfo = getSchedulingInfoByUuid(uuid);

		Calendar cal = Calendar.getInstance();
		
		cal.setTime(startTime);
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - schedulingInfo.getVMRAvailableBefore());
		schedulingInfo.setvMRStartTime(cal.getTime());
		
		schedulingInfo.setPortalLink(createPortalLink(startTime, schedulingInfo));
		
		schedulingInfo.setUpdatedByUser(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingInfo.setUpdatedTime(calendarNow.getTime());
		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		
		LOGGER.debug("Entry updateSchedulingInfo");
		return schedulingInfo;
	}

	@Transactional(rollbackFor = Throwable.class)
	public void deleteSchedulingInfo(String uuid) throws RessourceNotFoundException {
		LOGGER.debug("Entry deleteSchedulingInfo. uuid=" + uuid);
		
		SchedulingInfo schedulingInfo = getSchedulingInfoByUuid(uuid);
		schedulingInfoRepository.delete(schedulingInfo);
		
		LOGGER.debug("Exit deleteeSchedulingInfo");
	}

	private String createPortalLink(Date startTime, SchedulingInfo schedulingInfo) {
		LOGGER.debug("CitizenPortal (borgerPortal) parameter is: " + citizenPortal);
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String portalDate = formatter.format(startTime);
		LOGGER.debug("portalDate is: " + portalDate);
		
		String portalPin;
		if (schedulingInfo.getGuestPin() != null && schedulingInfo.getGuestPin() != null) {
			portalPin = schedulingInfo.getGuestPin().toString();
			LOGGER.debug("Portal pin used is guest");
		} else {
				if (schedulingInfo.getHostPin() != null && schedulingInfo.getHostPin() != null) {
					portalPin = schedulingInfo.getHostPin().toString();
					LOGGER.debug("Portal pin used is host");
				} else {
					portalPin = "";
					LOGGER.debug("Portal pin used is empty");
				}
		}

		String microphone = null;
		if (schedulingInfo.getMeeting() != null && schedulingInfo.getMeeting().getGuestMicrophone() != null){
			if (schedulingInfo.getMeeting().getGuestMicrophone() != GuestMicrophone.on){
				microphone = schedulingInfo.getMeeting().getGuestMicrophone().toString().toLowerCase();
			}
			LOGGER.debug("Guest microphone is: "+ schedulingInfo.getMeeting().getGuestMicrophone());
		}else {
			LOGGER.debug("Guest microphone is not set");
		}

		StringBuilder portalLink = new StringBuilder();
		//Minimum portal link
		portalLink.append(citizenPortal).append("/?url=").append(schedulingInfo.getUriWithDomain()).append("&pin=").append(portalPin).append("&start_dato=").append(portalDate);

		if (microphone != null){
			portalLink.append("&microphone=").append(microphone); 		//Example: https://portal-test.vconf.dk/?url=12312@rooms.vconf.dk&pin=1020&start_dato=2018-11-19T13:50:54&microphone=off
		}
		LOGGER.debug("portalLink is " + portalLink);
		return portalLink.toString();
	}

	@Transactional(rollbackFor = Throwable.class)
	public SchedulingInfo createSchedulingInfo(CreateSchedulingInfoDto createSchedulingInfoDto) throws PermissionDeniedException, NotValidDataException, NotAcceptableException {
		LOGGER.debug("Entry createSchedulingInfo");

		SchedulingInfo schedulingInfo = new SchedulingInfo();

		dk.medcom.video.api.organisation.Organisation organisation = organisationStrategy.findOrganisationByCode(createSchedulingInfoDto.getOrganizationId());
		if(organisation == null) {
			throw new NotValidDataException(NotValidDataErrors.ORGANISATION_ID_NOT_FOUND ,createSchedulingInfoDto.getOrganizationId());
		}

		if(organisation.getPoolSize() == null) {
			throw new NotValidDataException(NotValidDataErrors.SCHEDULING_INFO_CAN_NOT_BE_CREATED, organisation.getCode());
		}

		//if template is input and is related to the users organisation use that. Otherwise find default.
		LOGGER.debug("Searching for schedulingTemplate using id: " + createSchedulingInfoDto.getSchedulingTemplateId());
		SchedulingTemplate schedulingTemplate  = schedulingTemplateRepository.findById(createSchedulingInfoDto.getSchedulingTemplateId()).orElse(null);

		if (schedulingTemplate == null) {
			LOGGER.debug(String.format("Scheduling template %s not found.", createSchedulingInfoDto.getSchedulingTemplateId()));
			throw new NotValidDataException(NotValidDataErrors.SCHEDULING_TEMPLATE_NOT_FOUND, createSchedulingInfoDto.getSchedulingTemplateId().toString());
		}

		if(schedulingTemplate.getOrganisation() != null && !schedulingTemplate.getOrganisation().getOrganisationId().equals(createSchedulingInfoDto.getOrganizationId())) {
			LOGGER.debug(String.format("Scheduling template %s does not belong to organisation %s.", createSchedulingInfoDto.getSchedulingTemplateId(), createSchedulingInfoDto.getOrganizationId()));
			throw new NotValidDataException(NotValidDataErrors.SCHEDULING_TEMPLATE_NOT_IN_ORGANISATION, createSchedulingInfoDto.getSchedulingTemplateId().toString(), createSchedulingInfoDto.getOrganizationId());
		}

		LOGGER.debug("Found schedulingTemplate: " + schedulingTemplate.toString());

		schedulingInfo.setHostPin(createHostPin(schedulingTemplate));
		schedulingInfo.setGuestPin(createGuestPin(schedulingTemplate));
		schedulingInfo.setVMRAvailableBefore(schedulingTemplate.getVMRAvailableBefore());
		schedulingInfo.setIvrTheme(schedulingTemplate.getIvrTheme());

		String randomUri = generateUriWithoutDomain(schedulingTemplate);
		schedulingInfo.setUriWithoutDomain(randomUri);
		schedulingInfo.setUriWithDomain(schedulingTemplate.getUriPrefix() + schedulingInfo.getUriWithoutDomain() + "@" + schedulingTemplate.getUriDomain());

		schedulingInfo.setMaxParticipants(schedulingTemplate.getMaxParticipants());
		schedulingInfo.setEndMeetingOnEndTime(schedulingTemplate.getEndMeetingOnEndTime());

		schedulingInfo.setSchedulingTemplate(schedulingTemplate);
		schedulingInfo.setProvisionStatus(ProvisionStatus.AWAITS_PROVISION);
		schedulingInfo.setProvisionStatusDescription("Pooled awaiting provisioning.");

		schedulingInfo.setMeetingUser(meetingUserService.getOrCreateCurrentMeetingUser());

		schedulingInfo.setCreatedTime(new Date());

		schedulingInfo.setOrganisation(ensureOrganisationCreated(createSchedulingInfoDto.getOrganizationId()));
		schedulingInfo.setUuid(UUID.randomUUID().toString());

		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);

		LOGGER.debug("Exit createSchedulingInfo");
		return schedulingInfo;
	}

	private Organisation ensureOrganisationCreated(String organisationCode) {
		Organisation dbOrganisation = organisationRepository.findByOrganisationId(organisationCode);
		if(dbOrganisation == null) {
			dbOrganisation = new Organisation();
			dbOrganisation.setOrganisationId(organisationCode);

			dbOrganisation = organisationRepository.save(dbOrganisation);
		}

		return dbOrganisation;

	}

	@Transactional(rollbackFor = Throwable.class)
	Long getUnusedSchedulingInfoForOrganisation(Organisation organisation) { // TODO Refactor so this can be private
		List<BigInteger> schedulingInfos = schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(organisation.getId(),  ProvisionStatus.PROVISIONED_OK.name());

		if(schedulingInfos == null || schedulingInfos.isEmpty()) {
			return null;
		}

		return schedulingInfos.get(0).longValue();
	}

	SchedulingInfo attachMeetingToSchedulingInfo(Meeting meeting, SchedulingInfo schedulingInfo, boolean fromOverflow) {
		schedulingInfo.setMeetingUser(meeting.getMeetingUser());
		schedulingInfo.setUpdatedTime(new Date());
		schedulingInfo.setUpdatedByUser(meeting.getMeetingUser());
		schedulingInfo.setUuid(meeting.getUuid());
		schedulingInfo.setMeeting(meeting);

		Calendar cal = Calendar.getInstance();
		cal.setTime(meeting.getStartTime());
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - schedulingInfo.getVMRAvailableBefore());
		schedulingInfo.setvMRStartTime(cal.getTime());

		schedulingInfo.setPortalLink(createPortalLink(meeting.getStartTime(), schedulingInfo));
		if(!meeting.getOrganisation().getOrganisationId().equals(schedulingInfo.getOrganisation().getOrganisationId())) {
			schedulingInfo.setOrganisation(meeting.getOrganisation());
		}
		if(fromOverflow) {
			schedulingInfo.setPoolOverflow(true);
		}

		return schedulingInfoRepository.save(schedulingInfo);
	}

	SchedulingInfo attachMeetingToSchedulingInfo(Meeting meeting) {
		boolean fromOverflow = false;
		long organisationId = findPoolOrganisation(meeting.getOrganisation());
		Organisation organisation = organisationRepository.findById(organisationId).orElseThrow(RuntimeException::new);

		SchedulingInfo schedulingInfo = null;
		Long unusedId = getUnusedSchedulingInfoForOrganisation(organisation);

		if(unusedId == null && organisation.getPoolSize() != null) {
			unusedId = getSchedulingInfoFromOverflowPool();
			fromOverflow = true;
		}

		if (unusedId != null) {
			schedulingInfo = schedulingInfoRepository.findById(unusedId).orElse(null);
		}

		if(schedulingInfo == null) {
			return null;
		}

		return attachMeetingToSchedulingInfo(meeting, schedulingInfo, fromOverflow);
	}

	private Long findPoolOrganisation(Organisation organisation) {
		if(organisation.getPoolSize() != null && organisation.getPoolSize() > 0) {
			return organisation.getId();
		}

		OrganisationTree organisationTree = organisationTreeServiceClient.getOrganisationTree(organisation.getOrganisationId());

		var poolOrganisation = new OrganisationFinder().findPoolOrganisation(organisation.getOrganisationId(), organisationTree);
		return organisationRepository.findByOrganisationId(poolOrganisation.getCode()).getId();
	}

	private Long getSchedulingInfoFromOverflowPool() {
		LOGGER.info("Organisation {} is using scheduling info from overflow pool organisation {}.", userContextService.getUserContext().getUserOrganisation(), overflowPoolOrganisationId);
		var organisation = organisationRepository.findByOrganisationId(overflowPoolOrganisationId);
		LOGGER.debug("Organisation found: {}", organisation != null);
		List<BigInteger> schedulingInfos = schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(organisation.getId(),  ProvisionStatus.PROVISIONED_OK.name());

		if(schedulingInfos == null || schedulingInfos.isEmpty()) {
			return null;
		}

		return schedulingInfos.get(0).longValue();
	}

	public SchedulingInfo reserveSchedulingInfo() throws RessourceNotFoundException {
		var organisation = organisationRepository.findByOrganisationId(userContextService.getUserContext().getUserOrganisation());

		var id = getUnusedSchedulingInfoForOrganisation(organisation);

		if(id == null) {
			LOGGER.info("Unused scheduling info not found for organisation {}.", userContextService.getUserContext().getUserOrganisation());
			throw new RessourceNotFoundException("SchedulingInfo", "SchedulingInfo");
		}

		var schedulingInfo = schedulingInfoRepository.findById(id).get();
		schedulingInfo.setReservationId(UUID.randomUUID().toString());
		return schedulingInfoRepository.save(schedulingInfo);
	}

	public SchedulingInfo getSchedulingInfoByReservation(UUID schedulingInfoReservationId) throws RessourceNotFoundException {
		LOGGER.debug("Entry getSchedulingInfoByReservation. reservationId=" + schedulingInfoReservationId);
		SchedulingInfo schedulingInfo = schedulingInfoRepository.findOneByReservationId(schedulingInfoReservationId.toString());
		if (schedulingInfo == null) {
			LOGGER.debug("SchedulingInfo not found.");
			throw new RessourceNotFoundException("schedulingInfo", "reservationId");
		}
		LOGGER.debug("Exit getSchedulingInfoByUuid");
		return schedulingInfo;
	}
}
