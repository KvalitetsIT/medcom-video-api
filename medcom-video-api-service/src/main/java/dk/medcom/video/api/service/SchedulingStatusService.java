package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.SchedulingInfo;

public interface SchedulingStatusService {
    void createSchedulingStatus(SchedulingInfo schedulingInfo);

    void deleteSchedulingStatus(Meeting meeting);
}
