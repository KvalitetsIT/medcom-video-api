package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.entity.Meeting;

public interface AuditService {
    void auditMeeting(Meeting meeting, String action);
}
