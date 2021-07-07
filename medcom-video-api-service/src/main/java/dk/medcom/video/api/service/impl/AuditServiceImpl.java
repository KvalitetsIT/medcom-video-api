package dk.medcom.video.api.service.impl;

import dk.medcom.audit.client.AuditClient;
import dk.medcom.audit.client.api.v1.AuditEvent;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingLabel;
import dk.medcom.video.api.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.stream.Collectors;

public class AuditServiceImpl implements AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    private final AuditClient auditClient;

    public AuditServiceImpl(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    @Override
    public void auditMeeting(Meeting meeting, String action) {
        if(meeting == null) {
            logger.warn("Unable to create audit entry as input is null.");
            return;
        }

        var auditMeeting = mapMeeting(meeting);
        auditClient.addAuditEntry(createMeetingAuditEvent(auditMeeting, action));
    }

    private dk.medcom.video.api.service.domain.audit.Meeting mapMeeting(Meeting meeting) {
        var auditMeeting = new dk.medcom.video.api.service.domain.audit.Meeting();
        auditMeeting.setId(auditMeeting.getId());
        auditMeeting.setDescription(meeting.getDescription());
        auditMeeting.setMeetingLabels(meeting.getMeetingLabels().stream().map(MeetingLabel::getLabel).collect(Collectors.toSet()));
        auditMeeting.setEndTime(toOffsetDateTime(meeting.getEndTime()));
        auditMeeting.setExternalId(meeting.getExternalId());
        auditMeeting.setCreatedTime(toOffsetDateTime(meeting.getCreatedTime()));
        auditMeeting.setGuestMicrophone(meeting.getGuestMicrophone());
        auditMeeting.setOrganisation(meeting.getOrganisation().getOrganisationId());
        auditMeeting.setSubject(meeting.getSubject());
        auditMeeting.setProjectCode(meeting.getProjectCode());
        auditMeeting.setStartTime(toOffsetDateTime(meeting.getStartTime()));
        auditMeeting.setShortId(meeting.getShortId());
        auditMeeting.setUuid(meeting.getUuid());
        auditMeeting.setUpdatedTime(toOffsetDateTime(meeting.getUpdatedTime()));
        auditMeeting.setCreatedBy(meeting.getMeetingUser().getEmail());
        auditMeeting.setOrganizedBy(meeting.getOrganizedByUser().getEmail());
        auditMeeting.setEndTime(toOffsetDateTime(meeting.getEndTime()));
        auditMeeting.setGuestPinRequired(meeting.getGuestPinRequired());
        auditMeeting.setUpdatedBy(meeting.getUpdatedByUser() == null ? null : meeting.getUpdatedByUser().getEmail());

        return auditMeeting;
    }

    private OffsetDateTime toOffsetDateTime(Date dateTime) {
        if(dateTime == null) {
            return null;
        }

        return dateTime.toInstant().atOffset(ZoneOffset.UTC);
    }

    private AuditEvent<?> createMeetingAuditEvent(dk.medcom.video.api.service.domain.audit.Meeting meeting, String action) {
        var auditEvent = new AuditEvent<dk.medcom.video.api.service.domain.audit.Meeting>();
        auditEvent.setAuditData(meeting);
        auditEvent.setAuditEventDateTime(OffsetDateTime.now());
        auditEvent.setOrganisationCode(meeting.getOrganisation());
        auditEvent.setUser(meeting.getUpdatedBy() == null ? meeting.getCreatedBy() : meeting.getUpdatedBy());
        auditEvent.setSource("video-api");
        auditEvent.setIdentifier(meeting.getUuid());
        auditEvent.setOperation(action);
        auditEvent.setResource("meeting");

        return auditEvent;
    }
}
