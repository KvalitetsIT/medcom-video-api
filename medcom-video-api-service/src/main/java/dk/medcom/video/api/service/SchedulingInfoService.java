package dk.medcom.video.api.service;


import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.CreateSchedulingInfoDto;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.UpdateSchedulingInfoDto;
import dk.medcom.video.api.repository.OrganisationRepository;
import dk.medcom.video.api.repository.SchedulingInfoRepository;
import dk.medcom.video.api.repository.SchedulingTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SchedulingInfoService {
	private static Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoService.class);

	private SchedulingInfoRepository schedulingInfoRepository;
	private SchedulingTemplateRepository schedulingTemplateRepository;
	private SchedulingTemplateService schedulingTemplateService;
	private SchedulingStatusService schedulingStatusService;
	private MeetingUserService meetingUserService;
	private OrganisationRepository organisationRepository;

	@Value("${scheduling.info.citizen.portal}")
	private String citizenPortal;		
	
	public SchedulingInfoService(SchedulingInfoRepository schedulingInfoRepository, SchedulingTemplateRepository schedulingTemplateRepository, SchedulingTemplateService schedulingTemplateService,
			SchedulingStatusService schedulingStatusService, MeetingUserService meetingUserService, OrganisationRepository organisationRepository) {
		this.schedulingInfoRepository = schedulingInfoRepository;
		this.schedulingTemplateRepository = schedulingTemplateRepository;
		this.schedulingTemplateService = schedulingTemplateService;
		this.schedulingStatusService = schedulingStatusService;
		this.meetingUserService = meetingUserService;
		this.organisationRepository = organisationRepository;
	}

	public List<SchedulingInfo> getSchedulingInfo(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus) {
		return schedulingInfoRepository.findAllWithinAdjustedTimeIntervalAndStatus(fromStartTime, toEndTime, provisionStatus);
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
		
		schedulingInfo.setMeeting(meeting);
		schedulingInfo.setOrganisation(meeting.getOrganisation());

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
			throw new NotAcceptableException("The Uri assignment failed due to invalid setup on the template used");
		}
		do {            //loop x number of times until a no-duplicate url is found
			randomUri = String.valueOf(ThreadLocalRandom.current().nextLong(schedulingTemplate.getUriNumberRangeLow(), schedulingTemplate.getUriNumberRangeHigh()));
			schedulingInfoUri = schedulingInfoRepository.findOneByUriWithoutDomain(randomUri);
		} while (schedulingInfoUri != null && whileCount++ < whileMax);
		if (whileCount > whileMax) {
			LOGGER.debug("The Uri assignment failed. It was not possible to create a unique. Consider changing the interval on the template ");
			throw new NotAcceptableException("The Uri assignment failed. It was not possible to create a unique. Consider changing the interval on the template ");
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
				throw new NotAcceptableException("The guest pincode assignment failed due to invalid setup on the template used");
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
				throw new NotAcceptableException("The host pincode assignment failed due to invalid setup on the template used");
			}
		}

		return null;
	}

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
		
		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		schedulingStatusService.createSchedulingStatus(schedulingInfo);
		
		LOGGER.debug("Exit updateSchedulingInfo");
		return schedulingInfo;
	}
	
	//used by meetingService to update VMRStarttime and portalLink because it depends on the meetings starttime
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
		
		String portalLink = citizenPortal + "/?url=" + schedulingInfo.getUriWithDomain() + "&pin=" + portalPin + "&start_dato=" + portalDate; 		//Example: https://portal-test.vconf.dk/?url=12312@rooms.vconf.dk&pin=1020&start_dato=2018-11-19T13:50:54
		LOGGER.debug("portalLink is " + portalLink);
		return portalLink;
	}

	public SchedulingInfo createSchedulingInfo(CreateSchedulingInfoDto createSchedulingInfoDto) throws PermissionDeniedException, NotValidDataException, NotAcceptableException {
		LOGGER.debug("Entry createSchedulingInfo");

		SchedulingInfo schedulingInfo = new SchedulingInfo();

		Organisation organisation = organisationRepository.findByOrganisationId(createSchedulingInfoDto.getOrganizationId());
		if(organisation == null) {
			throw new NotValidDataException(String.format("OrganisationId %s in request not found.", createSchedulingInfoDto.getOrganizationId()));
		}

		if(organisation.getPoolSize() == null) {
			throw new NotValidDataException(String.format("Scheduling information can not be created on organisation %s that is not pool enabled.", organisation.getOrganisationId()));
		}

		//if template is input and is related to the users organisation use that. Otherwise find default.
		LOGGER.debug("Searching for schedulingTemplate using id: " + createSchedulingInfoDto.getSchedulingTemplateId());
		SchedulingTemplate schedulingTemplate  = schedulingTemplateRepository.findOne(createSchedulingInfoDto.getSchedulingTemplateId());

		if (schedulingTemplate == null) {
			LOGGER.debug(String.format("Scheduling template %s not found.", createSchedulingInfoDto.getSchedulingTemplateId()));
			throw new NotValidDataException(String.format("Scheduling template %s not found.", createSchedulingInfoDto.getSchedulingTemplateId()));
		}

		if(schedulingTemplate.getOrganisation() != null && !schedulingTemplate.getOrganisation().getOrganisationId().equals(createSchedulingInfoDto.getOrganizationId())) {
			LOGGER.debug(String.format("Scheduling template %s does not belong to organisation %s.", createSchedulingInfoDto.getSchedulingTemplateId(), createSchedulingInfoDto.getOrganizationId()));
			throw new NotValidDataException(String.format("Scheduling template %s does not belong to organisation %s.", createSchedulingInfoDto.getSchedulingTemplateId(), createSchedulingInfoDto.getOrganizationId()));
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

		schedulingInfo.setOrganisation(organisation);
		schedulingInfo.setUuid(UUID.randomUUID().toString());

		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);

		LOGGER.debug("Exit createSchedulingInfo");
		return schedulingInfo;
	}

	SchedulingInfo getUnusedSchedulingInfoForOrganisation(Organisation organisation) { // TODO Refactor so this can be private
		List<SchedulingInfo> schedulingInfos = schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(organisation,  ProvisionStatus.PROVISIONED_OK);

		if(schedulingInfos == null || schedulingInfos.isEmpty()) {
			return null;
		}

		return schedulingInfos.get(0);
	}

	SchedulingInfo attachMeetingToSchedulingInfo(Meeting meeting) {
		SchedulingInfo schedulingInfo = getUnusedSchedulingInfoForOrganisation(meeting.getOrganisation());

		if(schedulingInfo == null) {
			return null;
		}

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

		return schedulingInfoRepository.save(schedulingInfo);
	}
}
