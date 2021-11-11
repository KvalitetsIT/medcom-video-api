package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.api.*;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.*;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.organisation.OrganisationTree;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import dk.medcom.video.api.service.*;
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
public class SchedulingInfoServiceImpl implements SchedulingInfoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoServiceImpl.class);

	private final SchedulingInfoRepository schedulingInfoRepository;
	private final SchedulingTemplateRepository schedulingTemplateRepository;
	private final SchedulingTemplateService schedulingTemplateService;
	private final SchedulingStatusServiceImpl schedulingStatusService;
	private final MeetingUserService meetingUserService;
	private final OrganisationRepository organisationRepository;
	private final OrganisationStrategy organisationStrategy;
	private final UserContextService userContextService;
	private final String overflowPoolOrganisationId;
	private final OrganisationTreeServiceClient organisationTreeServiceClient;
	private final AuditService auditService;
	private final CustomUriValidator customUriValidator;

	@Value("${scheduling.info.citizen.portal}")
	private String citizenPortal;

	@Value("${pool.meeting.minimumAgeSec:60}")
	private int meetingMinimumAgeSec;

	public SchedulingInfoServiceImpl(SchedulingInfoRepository schedulingInfoRepository,
									 SchedulingTemplateRepository schedulingTemplateRepository,
									 SchedulingTemplateService schedulingTemplateService,
									 SchedulingStatusServiceImpl schedulingStatusService,
									 MeetingUserService meetingUserService,
									 OrganisationRepository organisationRepository,
									 OrganisationStrategy organisationStrategy,
									 UserContextService userContextService,
									 @Value("${overflow.pool.organisation.id}") String overflowPoolOrganisationId,
									 OrganisationTreeServiceClient organisationTreeServiceClient,
									 AuditService auditService,
									 CustomUriValidator customUriValidator) {
		this.schedulingInfoRepository = schedulingInfoRepository;
		this.schedulingTemplateRepository = schedulingTemplateRepository;
		this.schedulingTemplateService = schedulingTemplateService;
		this.schedulingStatusService = schedulingStatusService;
		this.meetingUserService = meetingUserService;
		this.organisationRepository = organisationRepository;
		this.organisationStrategy = organisationStrategy;
		this.userContextService = userContextService;
		this.organisationTreeServiceClient = organisationTreeServiceClient;
		this.auditService = auditService;
		this.customUriValidator = customUriValidator;

		if(overflowPoolOrganisationId == null)  {
			throw new RuntimeException("overflow.pool.organisation.id not set.");
		}
		this.overflowPoolOrganisationId = overflowPoolOrganisationId;
	}

	@Override
	public List<SchedulingInfo> getSchedulingInfo(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus) {
		return schedulingInfoRepository.findAllWithinAdjustedTimeIntervalAndStatus(fromStartTime, toEndTime, provisionStatus);
	}

	@Override
	public List<SchedulingInfo> getSchedulingInfoAwaitsProvision() {
		return schedulingInfoRepository.findAllWithinStartAndEndTimeLessThenAndStatus(new Date(), ProvisionStatus.AWAITS_PROVISION);
	}

	@Override
	public List<SchedulingInfo> getSchedulingInfoAwaitsDeProvision() {
		return schedulingInfoRepository.findAllWithinEndTimeLessThenAndStatus(new Date(), ProvisionStatus.PROVISIONED_OK);
	}

	@Override
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
	@Override
	public SchedulingInfo createSchedulingInfo(Meeting meeting, CreateMeetingDto createMeetingDto) throws NotAcceptableException, PermissionDeniedException, NotValidDataException {
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
			schedulingTemplate = schedulingTemplateService.getSchedulingTemplateInOrganisationTree();
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

		if(createMeetingDto.getUriWithoutDomain() != null) {
			var uri = createMeetingDto.getUriWithoutDomain();

			customUriValidator.validate(uri);

			var schedulingInfoUri = schedulingInfoRepository.findOneByUriWithoutDomain(uri);
			if(schedulingInfoUri != null) {
				LOGGER.info("uriWithoutDomain already used. Uri: {}", uri);
				throw new NotValidDataException(NotValidDataErrors.URI_ALREADY_USED);
			}

			schedulingInfo.setUriWithoutDomain(uri);
			schedulingInfo.setUriWithDomain(schedulingInfo.getUriWithoutDomain() + "@" + schedulingTemplate.getUriDomain());
		}
		else {
			var uri = generateUriWithoutDomain(schedulingTemplate);
			schedulingInfo.setUriWithoutDomain(uri);
			schedulingInfo.setUriWithDomain(schedulingTemplate.getUriPrefix() + schedulingInfo.getUriWithoutDomain() + "@" + schedulingTemplate.getUriDomain());
		}

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
		if (createMeetingDto.getVmrType() != null){
			schedulingInfo.setVmrType(createMeetingDto.getVmrType());
			LOGGER.debug("VmrType is taken from input: " + createMeetingDto.getVmrType().toString());
		}else {
			schedulingInfo.setVmrType(schedulingTemplate.getVmrType());
		}
		if (createMeetingDto.getHostView() != null){
			schedulingInfo.setHostView(createMeetingDto.getHostView());
			LOGGER.debug("HostView is taken from input: " + createMeetingDto.getHostView().toString());
		}else {
			schedulingInfo.setHostView(schedulingTemplate.getHostView());
		}
		if (createMeetingDto.getGuestView() != null){
			schedulingInfo.setGuestView(createMeetingDto.getGuestView());
			LOGGER.debug("GuestView is taken from input: " + createMeetingDto.getGuestView().toString());
		}else {
			schedulingInfo.setGuestView(schedulingTemplate.getGuestView());
		}
		if (createMeetingDto.getVmrQuality() != null){
			schedulingInfo.setVmrQuality(createMeetingDto.getVmrQuality());
			LOGGER.debug("VmrQuality is taken from input: " + createMeetingDto.getVmrQuality().toString());
		}else {
			schedulingInfo.setVmrQuality(schedulingTemplate.getVmrQuality());
		}
		if (createMeetingDto.getEnableOverlayText() != null){
			schedulingInfo.setEnableOverlayText(createMeetingDto.getEnableOverlayText());
			LOGGER.debug("EnableOverlayText is taken from input: " + createMeetingDto.getEnableOverlayText().toString());
		}else {
			schedulingInfo.setEnableOverlayText(schedulingTemplate.getEnableOverlayText());
		}
		if (createMeetingDto.getGuestsCanPresent() != null){
			schedulingInfo.setGuestsCanPresent(createMeetingDto.getGuestsCanPresent());
			LOGGER.debug("GuestsCanPresent is taken from input: " + createMeetingDto.getGuestsCanPresent().toString());
		}else {
			schedulingInfo.setGuestsCanPresent(schedulingTemplate.getGuestsCanPresent());
		}
		if (createMeetingDto.getForcePresenterIntoMain() != null){
			schedulingInfo.setForcePresenterIntoMain(createMeetingDto.getForcePresenterIntoMain());
			LOGGER.debug("ForcePresenterIntoMain is taken from input: " + createMeetingDto.getForcePresenterIntoMain().toString());
		}else {
			schedulingInfo.setForcePresenterIntoMain(schedulingTemplate.getForcePresenterIntoMain());
		}
		if (createMeetingDto.getForceEncryption() != null){
			schedulingInfo.setForceEncryption(createMeetingDto.getForceEncryption());
			LOGGER.debug("ForceEncryption is taken from input: " + createMeetingDto.getForceEncryption().toString());
		}else {
			schedulingInfo.setForceEncryption(schedulingTemplate.getForceEncryption());
		}
		if (createMeetingDto.getMuteAllGuests() != null){
			schedulingInfo.setMuteAllGuests(createMeetingDto.getMuteAllGuests());
			LOGGER.debug("MuteAllGuests is taken from input: " + createMeetingDto.getMuteAllGuests().toString());
		}else {
			schedulingInfo.setMuteAllGuests(schedulingTemplate.getMuteAllGuests());
		}

		schedulingInfo.setSchedulingTemplate(schedulingTemplate);
		schedulingInfo.setProvisionStatus(ProvisionStatus.AWAITS_PROVISION);
		
		schedulingInfo.setMeetingUser(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingInfo.setCreatedTime(calendarNow.getTime());

		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		auditService.auditSchedulingInformation(schedulingInfo, "create");

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

	@Override
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
		auditService.auditSchedulingInformation(schedulingInfo, "update");

		LOGGER.debug("Exit updateSchedulingInfo");
		return schedulingInfo;
	}
	
	//used by meetingService to update VMRStarttime and portalLink because it depends on the meetings starttime
	@Transactional(rollbackFor = Throwable.class)
	@Override
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
		auditService.auditSchedulingInformation(schedulingInfo, "update");
		
		LOGGER.debug("Entry updateSchedulingInfo");
		return schedulingInfo;
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
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

	@Override
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

		LOGGER.debug("Found schedulingTemplate: " + schedulingTemplate);

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

		schedulingInfo.setPool(true);

		schedulingInfo.setVmrType(schedulingTemplate.getVmrType());
		schedulingInfo.setHostView(schedulingTemplate.getHostView());
		schedulingInfo.setGuestView(schedulingTemplate.getGuestView());
		schedulingInfo.setVmrQuality(schedulingTemplate.getVmrQuality());
		schedulingInfo.setEnableOverlayText(schedulingTemplate.getEnableOverlayText());
		schedulingInfo.setGuestsCanPresent(schedulingTemplate.getGuestsCanPresent());
		schedulingInfo.setForcePresenterIntoMain(schedulingTemplate.getForcePresenterIntoMain());
		schedulingInfo.setForceEncryption(schedulingTemplate.getForceEncryption());
		schedulingInfo.setMuteAllGuests(schedulingTemplate.getMuteAllGuests());

		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);

		auditService.auditSchedulingInformation(schedulingInfo, "create");

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
	Long getUnusedSchedulingInfoForOrganisation(Organisation organisation, CreateMeetingDto createMeetingDto) { // TODO Refactor so this can be private
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) - meetingMinimumAgeSec);

		if (createMeetingDto == null){
			createMeetingDto = new CreateMeetingDto();
		}
		createMeetingDto.setDefaults();

		LOGGER.debug("findByMeetingIsNullAndOrganisationAndProvisionStatus - Org: '{}' Time: '{}' VMR Type: '{}' HostView: '{}' GuestView: '{}' VmrQuality: '{}' EnableOverlayText: '{}' GuestsCanPresent: '{}' ForcePresenterIntoMain: '{}' ForceEncryption: '{}' MuteAllGuests: '{}'",
				organisation.getId(),
				cal.getTime(),
				createMeetingDto.getVmrType().name(),
				createMeetingDto.getHostView().name(),
				createMeetingDto.getGuestView().name(),
				createMeetingDto.getVmrQuality().name(),
				createMeetingDto.getEnableOverlayText(),
				createMeetingDto.getGuestsCanPresent(),
				createMeetingDto.getForcePresenterIntoMain(),
				createMeetingDto.getForceEncryption(),
				createMeetingDto.getMuteAllGuests());

		List<BigInteger> schedulingInfos = schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(organisation.getId(),
				ProvisionStatus.PROVISIONED_OK.name(),
				cal.getTime(),
				createMeetingDto.getVmrType().name(),
				createMeetingDto.getHostView().name(),
				createMeetingDto.getGuestView().name(),
				createMeetingDto.getVmrQuality().name(),
				createMeetingDto.getEnableOverlayText(),
				createMeetingDto.getGuestsCanPresent(),
				createMeetingDto.getForcePresenterIntoMain(),
				createMeetingDto.getForceEncryption(),
				createMeetingDto.getMuteAllGuests());

		if(schedulingInfos == null || schedulingInfos.isEmpty()) {
			LOGGER.debug("findByMeetingIsNullAndOrganisationAndProvisionStatus Result NULL- Org: '{}' Time: '{}'",
					organisation.getId(),
					cal.getTime());
			return null;
		}

		LOGGER.debug("findByMeetingIsNullAndOrganisationAndProvisionStatus Result '{}'- Org: '{}' Time: '{}'",
				schedulingInfos.get(0).longValue(),
				organisation.getId(),
				cal.getTime());
		return schedulingInfos.get(0).longValue();
	}

	@Override
	public SchedulingInfo attachMeetingToSchedulingInfo(Meeting meeting, SchedulingInfo schedulingInfo, boolean fromOverflow) {
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

		var resultingSchedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		auditService.auditSchedulingInformation(resultingSchedulingInfo, "update");

		return resultingSchedulingInfo;
	}

	@Override
	public SchedulingInfo attachMeetingToSchedulingInfo(Meeting meeting, CreateMeetingDto createMeetingDto) {
		boolean fromOverflow = false;
		long organisationId = findPoolOrganisation(meeting.getOrganisation());
		Organisation organisation = organisationRepository.findById(organisationId).orElseThrow(RuntimeException::new);

		SchedulingInfo schedulingInfo = null;
		Long unusedId = getUnusedSchedulingInfoForOrganisation(organisation, createMeetingDto);

		if(unusedId == null && organisation.getPoolSize() != null) {
			unusedId = getSchedulingInfoFromOverflowPool(createMeetingDto);
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

	private Long getSchedulingInfoFromOverflowPool(CreateMeetingDto createMeetingDto) {
		LOGGER.info("Organisation {} is using scheduling info from overflow pool organisation {}.", userContextService.getUserContext().getUserOrganisation(), overflowPoolOrganisationId);
		var organisation = organisationRepository.findByOrganisationId(overflowPoolOrganisationId);
		LOGGER.debug("Organisation found: {}", organisation != null);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) - meetingMinimumAgeSec);

		if (createMeetingDto == null) {
			createMeetingDto = new CreateMeetingDto();
		}
		createMeetingDto.setDefaults();

		List<BigInteger> schedulingInfos = schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(organisation.getId(),  ProvisionStatus.PROVISIONED_OK.name(), cal.getTime(), createMeetingDto.getVmrType().name(), createMeetingDto.getHostView().name(), createMeetingDto.getGuestView().name(), createMeetingDto.getVmrQuality().name(), createMeetingDto.getEnableOverlayText(), createMeetingDto.getGuestsCanPresent(), createMeetingDto.getForcePresenterIntoMain(), createMeetingDto.getForceEncryption(), createMeetingDto.getMuteAllGuests());

		if(schedulingInfos == null || schedulingInfos.isEmpty()) {
			return null;
		}

		return schedulingInfos.get(0).longValue();
	}

	@Override
	public SchedulingInfo reserveSchedulingInfo(VmrType vmrType,
												ViewType hostView,
												ViewType guestView,
												VmrQuality vmrQuality,
												Boolean enableOverlayText,
												Boolean guestsCanPresent,
												Boolean forcePresenterIntoMain,
												Boolean forceEncryption,
												Boolean muteAllGuests) throws RessourceNotFoundException {
		var organisation = organisationRepository.findByOrganisationId(userContextService.getUserContext().getUserOrganisation());

		var createMeetingDto = new CreateMeetingDto();
		createMeetingDto.setVmrType(vmrType);
		createMeetingDto.setHostView(hostView);
		createMeetingDto.setGuestView(guestView);
		createMeetingDto.setVmrQuality(vmrQuality);
		createMeetingDto.setEnableOverlayText(enableOverlayText);
		createMeetingDto.setGuestsCanPresent(guestsCanPresent);
		createMeetingDto.setForcePresenterIntoMain(forcePresenterIntoMain);
		createMeetingDto.setForceEncryption(forceEncryption);
		createMeetingDto.setMuteAllGuests(muteAllGuests);

		var id = getUnusedSchedulingInfoForOrganisation(organisation, createMeetingDto);

		if(id == null) {
			LOGGER.info("Unused scheduling info not found for organisation {}.", userContextService.getUserContext().getUserOrganisation());
			throw new RessourceNotFoundException("SchedulingInfo", "SchedulingInfo");
		}

		var schedulingInfo = schedulingInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("Please try again"));
		schedulingInfo.setReservationId(UUID.randomUUID().toString());

		var resultingSchedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		auditService.auditSchedulingInformation(resultingSchedulingInfo, "update");

		return resultingSchedulingInfo;
	}

	@Override
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