package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.service.AuditService;

public class AuditServiceNullOperation implements AuditService {
    @Override
    public void auditMeeting(Meeting meeting, String action) {
    }
}
