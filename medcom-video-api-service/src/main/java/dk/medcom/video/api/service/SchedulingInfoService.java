package dk.medcom.video.api.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.SchedulingInfoDto;
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
	UserContextService userService;

	public List<SchedulingInfo> getSchedulingInfo() {
	
		return schedulingInfoRepository.findAll();
	}

	public SchedulingInfo getSchedulingInfoByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		SchedulingInfo schedulingInfo = schedulingInfoRepository.findOneByUuid(uuid);
		if (schedulingInfo == null) {
			throw new RessourceNotFoundException("schedulingInfo", "uuid");
		}
		//TODO Lene: skal vi tjekke via organisation? teknik "personen" skal han ikke kunne trække en liste på tværs af organisation? men anden begrænsning? f.eks. er date internval kommet på getMeetings
//		if (!schedulingInfo.getOrganisationId().equals(userService.getUserContext().getUserOrganisation())) {
//			throw new PermissionDeniedException();
//		}
		return schedulingInfo;
	}

	public SchedulingInfo createSchedulingInfo(Meeting meeting) throws RessourceNotFoundException {
		
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplate();
		SchedulingInfo schedulingInfo = new SchedulingInfo();
		
		schedulingInfo.setUuid(meeting.getUuid());
		if (schedulingTemplate.getHostPinRequired()) {
			schedulingInfo.setHostPin(ThreadLocalRandom.current().nextLong(schedulingTemplate.getHostPinRangeLow(), schedulingTemplate.getHostPinRangeHigh()));
		}
		if (schedulingTemplate.getGuestPinRequired()) {
			schedulingInfo.setGuestPin(ThreadLocalRandom.current().nextLong(schedulingTemplate.getGuestPinRangeLow(), schedulingTemplate.getGuestPinRangeHigh()));
		}
		
		schedulingInfo.setVMRAvailableBefore(schedulingTemplate.getVMRAvailableBefore());
		schedulingInfo.setMaxParticipants(schedulingTemplate.getMaxParticipants());
		schedulingInfo.setMeeting(meeting);
		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		
		return schedulingInfo;
	}
	
	public SchedulingInfo updateSchedulingInfo(String uuid, SchedulingInfoDto schedulingInfoDto) throws RessourceNotFoundException, PermissionDeniedException  {
		
		SchedulingInfo schedulingInfo = getSchedulingInfoByUuid(uuid);
		schedulingInfo.setHostPin(schedulingInfoDto.getHostPin());
		schedulingInfo.setGuestPin(schedulingInfoDto.getHostPin());
		schedulingInfo.setVMRAvailableBefore(schedulingInfoDto.getVmrAvailableBefore());
		schedulingInfo.setMaxParticipants(schedulingInfoDto.getMaxParticipants());
		schedulingInfo = schedulingInfoRepository.save(schedulingInfo);
		return schedulingInfo;
	}
	
}
