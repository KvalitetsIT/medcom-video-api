package dk.medcom.video.api.service;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.UpdateSchedulingInfoDto;
import dk.medcom.video.api.repository.SchedulingInfoRepository;
import dk.medcom.video.api.repository.SchedulingTemplateRepository;

@Component
public class SchedulingInfoService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoService.class);

	@Autowired
	SchedulingInfoRepository schedulingInfoRepository;
	
	@Autowired
	SchedulingTemplateRepository schedulingTemplateRepository;
	
	@Autowired
	SchedulingTemplateService schedulingTemplateService;
	
	@Autowired
	SchedulingStatusService schedulingStatusService;
	
	@Autowired
	UserContextService userService;
	
	@Autowired
	MeetingUserService meetingUserService;
	
	@Value("${scheduling.info.citizen.portal}")
	private String citizenPortal;		
	
	public SchedulingInfoService() {
		
	}
	public SchedulingInfoService(SchedulingInfoRepository schedulingInfoRepository, SchedulingTemplateRepository schedulingTemplateRepository, SchedulingTemplateService schedulingTemplateService, 
			SchedulingStatusService schedulingStatusService, UserContextService userService, MeetingUserService meetingUserService) {
		this.schedulingInfoRepository = schedulingInfoRepository;
//		this.schedulingTemplateRepository = 
	}
	
	
	public List<SchedulingInfo> getSchedulingInfo(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus) {
		return schedulingInfoRepository.findAllWithinAdjustedTimeIntervalAndStatus(fromStartTime, toEndTime, provisionStatus);
	}

	public SchedulingInfo getSchedulingInfoByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		LOGGER.debug("Entry getSchedulingInfoByUuid. uuid=" + uuid);
		SchedulingInfo schedulingInfo = schedulingInfoRepository.findOneByUuid(uuid);
		if (schedulingInfo == null) {
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
			LOGGER.debug("Searching for  schedulingTemplate using id: " + createMeetingDto.getSchedulingTemplateId());
			schedulingTemplate = schedulingTemplateService.getSchedulingTemplateFromOrganisation(createMeetingDto.getSchedulingTemplateId());
		}
		if (schedulingTemplate == null) {
			schedulingTemplate = schedulingTemplateService.getSchedulingTemplate();
		}
		LOGGER.debug("Found schedulingTemplate: " + schedulingTemplate.toString());
		
		schedulingInfo.setUuid(meeting.getUuid());
		if (schedulingTemplate.getHostPinRequired()) {
			if (schedulingTemplate.getHostPinRangeLow() != null && schedulingTemplate.getHostPinRangeHigh() != null &&
					schedulingTemplate.getHostPinRangeLow() < schedulingTemplate.getHostPinRangeHigh()) {
				schedulingInfo.setHostPin(ThreadLocalRandom.current().nextLong(schedulingTemplate.getHostPinRangeLow(), schedulingTemplate.getHostPinRangeHigh()));
			} else {	
				throw new NotAcceptableException("The host pincode assignment failed due to invalid setup on the template used");
			}
			
		}
		if (schedulingTemplate.getGuestPinRequired()) {
			if (schedulingTemplate.getGuestPinRangeLow() != null && schedulingTemplate.getGuestPinRangeHigh() != null &&
					schedulingTemplate.getGuestPinRangeLow() < schedulingTemplate.getGuestPinRangeHigh()) {
				schedulingInfo.setGuestPin(ThreadLocalRandom.current().nextLong(schedulingTemplate.getGuestPinRangeLow(), schedulingTemplate.getGuestPinRangeHigh()));
			} else {
				throw new NotAcceptableException("The guest pincode assignment failed due to invalid setup on the template used");
			}
		}
		
		schedulingInfo.setVMRAvailableBefore(schedulingTemplate.getVMRAvailableBefore());
		Calendar cal = Calendar.getInstance();
		cal.setTime(meeting.getStartTime());
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - schedulingInfo.getVMRAvailableBefore());
		schedulingInfo.setvMRStartTime(cal.getTime());
		
//		schedulingInfo.setMaxParticipants(schedulingTemplate.getMaxParticipants()); //TODO: clean up
//		schedulingInfo.setEndMeetingOnEndTime(schedulingTemplate.getEndMeetingOnEndTime());
		schedulingInfo.setIvrTheme(schedulingTemplate.getIvrTheme());  //example: /api/admin/configuration/v1/ivr_theme/10/
		
		String randomUri;
		int whileCount = 0;
		int whileMax = 100;

		SchedulingInfo schedulingInfoUri;
		
		if (!(schedulingTemplate.getUriNumberRangeLow() < schedulingTemplate.getUriNumberRangeHigh())) {
			throw new NotAcceptableException("The Uri assignment failed due to invalid setup on the template used");
		}
		do {  			//loop x number of times until a no-duplicate url is found
			randomUri = String.valueOf(ThreadLocalRandom.current().nextLong(schedulingTemplate.getUriNumberRangeLow(), schedulingTemplate.getUriNumberRangeHigh()));
			schedulingInfoUri = schedulingInfoRepository.findOneByUriWithoutDomain(randomUri);
			} while (schedulingInfoUri != null && whileCount++ < whileMax); 
		if (whileCount > whileMax ) {
			throw new NotAcceptableException("The Uri assignment failed. It was not possible to create a unique. Consider changing the interval on the template ");
		}
		
		schedulingInfo.setUriWithoutDomain(randomUri);		
		schedulingInfo.setUriWithDomain(schedulingInfo.getUriWithoutDomain() + "@" + schedulingTemplate.getUriDomain());
		
		LOGGER.debug("CitizenPortal (borgerPortal) parameter is: " + citizenPortal);
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String portalDate = formatter.format(meeting.getStartTime());
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
		schedulingInfo.setPortalLink(portalLink);
		
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
		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		
		LOGGER.debug("Exit createSchedulingInfo");
		return schedulingInfo;
	}
	
	public SchedulingInfo updateSchedulingInfo(String uuid, UpdateSchedulingInfoDto updateSchedulingInfoDto) throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException  {
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
	//used by meetingService to update VMRStarttime because it depends on the meetings starttime
	public SchedulingInfo updateSchedulingInfo(String uuid, Date startTime) throws RessourceNotFoundException, PermissionDeniedException{
		LOGGER.debug("Entry updateSchedulingInfo. uuid/startTime. uuid=" + uuid);
		
		SchedulingInfo schedulingInfo = getSchedulingInfoByUuid(uuid);

		Calendar cal = Calendar.getInstance();
		
		cal.setTime(startTime);
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - schedulingInfo.getVMRAvailableBefore());
		schedulingInfo.setvMRStartTime(cal.getTime());

		
		schedulingInfo.setUpdatedByUser(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingInfo.setUpdatedTime(calendarNow.getTime());
		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		
		LOGGER.debug("Entry updateSchedulingInfo");
		return schedulingInfo;
	}

	public void deleteSchedulingInfo(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		LOGGER.debug("Entry deleteSchedulingInfo. uuid=" + uuid);
		
		SchedulingInfo schedulingInfo = getSchedulingInfoByUuid(uuid);
		schedulingInfoRepository.delete(schedulingInfo);
		
		LOGGER.debug("Exit deleteeSchedulingInfo");
	}

	
}
