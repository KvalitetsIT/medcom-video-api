package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.SchedulingInfo;

public interface AuditService {
    void auditMeeting(Meeting meeting, String action);

    void auditSchedulingInformation(SchedulingInfo input, String action);
}
