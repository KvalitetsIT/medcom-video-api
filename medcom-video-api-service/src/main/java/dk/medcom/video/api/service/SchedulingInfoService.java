package dk.medcom.video.api.service;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.SchedulingInfoDto;
import dk.medcom.video.api.dto.UpdateSchedulingInfoDto;
import dk.medcom.video.api.repository.SchedulingInfoRepository;
import dk.medcom.video.api.repository.SchedulingTemplateRepository;

@Component
public class SchedulingInfoService {

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
	
	public List<SchedulingInfo> getSchedulingInfo(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus) {
		return schedulingInfoRepository.findAllWithinAdjustedTimeIntervalAndStatus(fromStartTime, toEndTime, provisionStatus);
	}

	public SchedulingInfo getSchedulingInfoByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		SchedulingInfo schedulingInfo = schedulingInfoRepository.findOneByUuid(uuid);
		if (schedulingInfo == null) {
			throw new RessourceNotFoundException("schedulingInfo", "uuid");
		}
		return schedulingInfo;
	}

	public SchedulingInfo createSchedulingInfo(Meeting meeting) throws NotAcceptableException, PermissionDeniedException {
		
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplate();
		SchedulingInfo schedulingInfo = new SchedulingInfo();
		
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
		
		schedulingInfo.setMaxParticipants(schedulingTemplate.getMaxParticipants());
		schedulingInfo.setEndMeetingOnEndTime(schedulingTemplate.getEndMeetingOnEndTime());
		
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
		schedulingInfo.setSchedulingTemplate(schedulingTemplate);
		schedulingInfo.setProvisionStatus(ProvisionStatus.AWAITS_PROVISION);
		schedulingInfo.setMeeting(meeting);
		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		
		return schedulingInfo;
	}
	
	public SchedulingInfo updateSchedulingInfo(String uuid, UpdateSchedulingInfoDto updateSchedulingInfoDto) throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException  {
		
		SchedulingInfo schedulingInfo = getSchedulingInfoByUuid(uuid);
		schedulingInfo.setProvisionStatus(updateSchedulingInfoDto.getProvisionStatus());
		schedulingInfo.setProvisionStatusDescription(updateSchedulingInfoDto.getProvisionStatusDescription());
		schedulingInfo.setProvisionTimestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());  
		

		try{
			if (updateSchedulingInfoDto.getProvisionVmrId() != null) {
				UUID uuidChk = UUID.fromString(updateSchedulingInfoDto.getProvisionVmrId());
			}
			schedulingInfo.setProvisionVMRId(updateSchedulingInfoDto.getProvisionVmrId());
		} catch (IllegalArgumentException exception) {
			throw new NotValidDataException("provisionVmrId must have uuid format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
		}
		
		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		schedulingStatusService.createSchedulingStatus(schedulingInfo);
		
		return schedulingInfo;
	}
	
	public SchedulingInfo updateSchedulingInfo(String uuid, Date startTime) throws RessourceNotFoundException, PermissionDeniedException{
		
		SchedulingInfo schedulingInfo = getSchedulingInfoByUuid(uuid);

		Calendar cal = Calendar.getInstance();
		
		cal.setTime(startTime);
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - schedulingInfo.getVMRAvailableBefore());
		schedulingInfo.setvMRStartTime(cal.getTime());

		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		
		return schedulingInfo;
	}

	public void deleteSchedulingInfo(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		
		SchedulingInfo schedulingInfo = getSchedulingInfoByUuid(uuid);
		schedulingInfoRepository.delete(schedulingInfo);

	}

	
}
