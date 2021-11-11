package dk.medcom.video.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingStatus;
import dk.medcom.video.api.dao.SchedulingStatusRepository;

@Component
public class SchedulingStatusService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingStatusService.class);
	
	@Autowired
	private SchedulingStatusRepository schedulingStatusRepository;

	public void createSchedulingStatus(SchedulingInfo schedulingInfo) {
		SchedulingStatus schedulingStatus = new SchedulingStatus();
		schedulingStatus.setMeeting(schedulingInfo.getMeeting());
		schedulingStatus.setProvisionStatus(schedulingInfo.getProvisionStatus());
		schedulingStatus.setProvisionStatusDescription(schedulingInfo.getProvisionStatusDescription());
		schedulingStatus.setTimeStamp(schedulingInfo.getProvisionTimestamp());
		schedulingStatusRepository.save(schedulingStatus);
	}

	public void deleteSchedulingStatus(Meeting meeting) {
		LOGGER.debug("Entry deleteShedulingStatus. meetingid=" + meeting.getId());

		schedulingStatusRepository.deleteByMeetingId(meeting.getId());
		
		LOGGER.debug("Exit deleteeSchedulingStatus");
	}
}
