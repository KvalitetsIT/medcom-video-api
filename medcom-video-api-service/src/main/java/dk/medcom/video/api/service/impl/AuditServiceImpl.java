package dk.medcom.video.api.service.impl;

import dk.kvalitetsit.audit.client.AuditClient;
import dk.kvalitetsit.audit.client.api.v1.AuditEvent;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingLabel;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
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

    @Override
    public void auditSchedulingInformation(SchedulingInfo input, String action) {
        if(input == null) {
            logger.warn("Unable to create audit entry as input is null.");
            return;
        }

        var auditSchedulingInfo = mapSchedulingInfo(input);
        auditClient.addAuditEntry(createSchedulingInfoAuditEvent(auditSchedulingInfo, action));
    }

    private AuditEvent<dk.medcom.video.api.service.domain.audit.SchedulingInfo> createSchedulingInfoAuditEvent(dk.medcom.video.api.service.domain.audit.SchedulingInfo schedulingInfo, String action) {
        var auditEvent = new AuditEvent<dk.medcom.video.api.service.domain.audit.SchedulingInfo>();
        auditEvent.setAuditData(schedulingInfo);
        auditEvent.setAuditEventDateTime(OffsetDateTime.now());
        auditEvent.setOrganisationCode(schedulingInfo.getOrganisation());
        auditEvent.setUser(schedulingInfo.getUpdatedBy() == null ? schedulingInfo.getCreatedBy() : schedulingInfo.getUpdatedBy());
        auditEvent.setSource("video-api");
        auditEvent.setIdentifier(schedulingInfo.getUriWithDomain());
        auditEvent.setOperation(action);
        auditEvent.setResource("schedulingInfo");

        return auditEvent;
    }

    private dk.medcom.video.api.service.domain.audit.SchedulingInfo mapSchedulingInfo(SchedulingInfo input) {
        var auditSchedulingInfo = new dk.medcom.video.api.service.domain.audit.SchedulingInfo();
        auditSchedulingInfo.setId(input.getId());
        auditSchedulingInfo.setUuid(input.getUuid());
        auditSchedulingInfo.setHostPin(input.getHostPin());
        auditSchedulingInfo.setGuestPin(input.getGuestPin());
        auditSchedulingInfo.setvMRAvailableBefore(input.getVMRAvailableBefore());
        auditSchedulingInfo.setvMRStartTime(toOffsetDateTime(input.getvMRStartTime()));
        auditSchedulingInfo.setMaxParticipants(input.getMaxParticipants());
        auditSchedulingInfo.setEndMeetingOnEndTime(input.getEndMeetingOnEndTime());
        auditSchedulingInfo.setUriWithDomain(input.getUriWithDomain());
        auditSchedulingInfo.setUriWithoutDomain(input.getUriWithoutDomain());
        auditSchedulingInfo.setPoolOverflow(input.getPoolOverflow());
        auditSchedulingInfo.setPool(input.isPool());
        auditSchedulingInfo.setVmrType(toStringOrNull(input.getVmrType()));
        auditSchedulingInfo.setHostView(toStringOrNull(input.getHostView()));
        auditSchedulingInfo.setGuestView(toStringOrNull(input.getGuestView()));
        auditSchedulingInfo.setVmrQuality(toStringOrNull(input.getVmrQuality()));
        auditSchedulingInfo.setEnableOverlayText(input.getEnableOverlayText());
        auditSchedulingInfo.setGuestsCanPresent(input.getGuestsCanPresent());
        auditSchedulingInfo.setForcePresenterIntoMain(input.getForcePresenterIntoMain());
        auditSchedulingInfo.setForceEncryption(input.getForceEncryption());
        auditSchedulingInfo.setMuteAllGuests(input.getMuteAllGuests());
        auditSchedulingInfo.setSchedulingTemplate(longOrNull(input.getSchedulingTemplate()));
        auditSchedulingInfo.setProvisionStatus(toStringOrNull(input.getProvisionStatus()));
        auditSchedulingInfo.setProvisionStatusDescription(input.getProvisionStatusDescription());
        auditSchedulingInfo.setProvisionTimestamp(toOffsetDateTime(input.getProvisionTimestamp()));
        auditSchedulingInfo.setProvisionVMRId(input.getProvisionVMRId());
        auditSchedulingInfo.setOrganisation(input.getOrganisation().getOrganisationId()); // Not null in database. Always present.
        auditSchedulingInfo.setPortalLink(input.getPortalLink());
        auditSchedulingInfo.setIvrTheme(input.getIvrTheme());
        auditSchedulingInfo.setCreatedBy(input.getMeetingUser() == null ? null : input.getMeetingUser().getEmail());
        auditSchedulingInfo.setCreatedTime(toOffsetDateTime(input.getCreatedTime()));
        auditSchedulingInfo.setUpdatedBy(input.getUpdatedByUser() == null ? null : input.getUpdatedByUser().getEmail());
        auditSchedulingInfo.setUpdatedTime(toOffsetDateTime(input.getUpdatedTime()));
        auditSchedulingInfo.setReservationId(input.getReservationId());

        return auditSchedulingInfo;
    }

    private Long longOrNull(SchedulingTemplate value) {
        return value == null ? null : value.getId();
    }

    private String toStringOrNull(Object value) {
        return value == null ? null : value.toString();
    }

    private dk.medcom.video.api.service.domain.audit.Meeting mapMeeting(Meeting meeting) {
        var auditMeeting = new dk.medcom.video.api.service.domain.audit.Meeting();
        auditMeeting.setId(meeting.getId());
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
        auditMeeting.setCreatedBy(meeting.getMeetingUser() == null ? null : meeting.getMeetingUser().getEmail());
        auditMeeting.setOrganizedBy(meeting.getOrganizedByUser() == null ? null : meeting.getOrganizedByUser().getEmail());
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
