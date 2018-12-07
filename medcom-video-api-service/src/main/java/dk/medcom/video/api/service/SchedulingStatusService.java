package dk.medcom.video.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingStatus;
import dk.medcom.video.api.repository.SchedulingStatusRepository;


@Component
public class SchedulingStatusService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SchedulingStatusService.class);
	
	@Autowired
	SchedulingStatusRepository schedulingStatusRepository;

	public SchedulingStatus createSchedulingStatus(SchedulingInfo schedulingInfo) {
		SchedulingStatus schedulingStatus = new SchedulingStatus();
		schedulingStatus.setMeeting(schedulingInfo.getMeeting());
		schedulingStatus.setProvisionStatus(schedulingInfo.getProvisionStatus());
		schedulingStatus.setProvisionStatusDescription(schedulingInfo.getProvisionStatusDescription());
		schedulingStatus.setTimeStamp(schedulingInfo.getProvisionTimestamp());
		schedulingStatus = schedulingStatusRepository.save(schedulingStatus);
		
		return schedulingStatus;
	}

	public void deleteSchedulingStatus(Meeting meeting) throws RessourceNotFoundException, PermissionDeniedException {
		LOGGER.debug("Entry deleteShedulingStatus. meetingid=" + meeting.getId());

		schedulingStatusRepository.deleteByMeetingId(meeting.getId());
		
		LOGGER.debug("Exit deleteeSchedulingStatus");
	}

}
