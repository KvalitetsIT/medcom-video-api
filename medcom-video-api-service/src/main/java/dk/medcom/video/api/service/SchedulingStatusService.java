package dk.medcom.video.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingStatus;
import dk.medcom.video.api.repository.SchedulingStatusRepository;


@Component
public class SchedulingStatusService {
	
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

}
