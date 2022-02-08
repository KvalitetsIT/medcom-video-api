package dk.medcom.video.api.service.impl;

import dk.medcom.audit.client.AuditClient;
import dk.medcom.audit.client.api.v1.AuditEvent;
import dk.medcom.video.api.api.*;
import dk.medcom.video.api.dao.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;

public class AuditServiceImplTest {
    private AuditServiceImpl auditService;
    private AuditClient auditClient;

    @Before
    public void setup() {
        auditClient = Mockito.mock(AuditClient.class);
        auditService = new AuditServiceImpl(auditClient);

    }

    @Test
    public void testAuditServiceMeeting() {
        var input = createMeeting();

        auditService.auditMeeting(input, "action");

        var auditArgumentCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        Mockito.verify(auditClient, times(1)).addAuditEntry(auditArgumentCaptor.capture());

        assertNotNull(auditArgumentCaptor.getValue());
        AuditEvent<dk.medcom.video.api.service.domain.audit.Meeting> auditEvent = auditArgumentCaptor.getValue();

        assertNotNull(auditEvent.getAuditEventDateTime());
        assertEquals(input.getUuid(), auditEvent.getIdentifier());
        assertEquals("action", auditEvent.getOperation());
        assertEquals("meeting", auditEvent.getResource());
        assertEquals("video-api", auditEvent.getSource());
        assertEquals(input.getUpdatedByUser().getEmail(), auditEvent.getUser());
        assertEquals(input.getOrganisation().getOrganisationId(), auditEvent.getOrganisationCode());

        assertEquals(input.getId(), auditEvent.getAuditData().getId());
        assertEquals(input.getUuid(), auditEvent.getAuditData().getUuid());
        assertEquals(input.getSubject(), auditEvent.getAuditData().getSubject());
        assertEquals(input.getOrganisation().getOrganisationId(), auditEvent.getAuditData().getOrganisation());
        assertEquals(input.getMeetingUser().getEmail(), auditEvent.getAuditData().getCreatedBy());
        assertEquals(input.getCreatedTime().toInstant(), auditEvent.getAuditData().getCreatedTime().toInstant());
        assertEquals(input.getUpdatedByUser().getEmail(), auditEvent.getAuditData().getUpdatedBy());
        assertEquals(input.getUpdatedTime().toInstant(), auditEvent.getAuditData().getUpdatedTime().toInstant());
        assertEquals(input.getOrganizedByUser().getEmail(), auditEvent.getAuditData().getOrganizedBy());
        assertEquals(input.getStartTime().toInstant(), auditEvent.getAuditData().getStartTime().toInstant());
        assertEquals(input.getEndTime().toInstant(), auditEvent.getAuditData().getEndTime().toInstant());
        assertEquals(input.getDescription(), auditEvent.getAuditData().getDescription());
        assertEquals(input.getProjectCode(), auditEvent.getAuditData().getProjectCode());
        assertEquals(input.getShortId(), auditEvent.getAuditData().getShortId());
        assertEquals(input.getExternalId(), auditEvent.getAuditData().getExternalId());
        assertEquals(input.getMeetingLabels().stream().map(MeetingLabel::getLabel).collect(Collectors.toSet()), auditEvent.getAuditData().getMeetingLabels());
        assertEquals(input.getGuestMicrophone(), auditEvent.getAuditData().getGuestMicrophone());
        assertEquals(input.getGuestPinRequired(), auditEvent.getAuditData().isGuestPinRequired());
    }

    @Test
    public void testAuditServiceSchedulingInformation() {
        var input = createSchedulingInformation();

        auditService.auditSchedulingInformation(input, "action");

        var auditArgumentCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        Mockito.verify(auditClient, times(1)).addAuditEntry(auditArgumentCaptor.capture());

        assertNotNull(auditArgumentCaptor.getValue());
        AuditEvent<dk.medcom.video.api.service.domain.audit.SchedulingInfo> auditEvent = auditArgumentCaptor.getValue();

        assertNotNull(auditEvent.getAuditEventDateTime());
        assertEquals(input.getUriWithDomain(), auditEvent.getIdentifier());
        assertEquals("action", auditEvent.getOperation());
        assertEquals("schedulingInfo", auditEvent.getResource());
        assertEquals("video-api", auditEvent.getSource());
        assertEquals(input.getUpdatedByUser().getEmail(), auditEvent.getUser());
        assertEquals(input.getOrganisation().getOrganisationId(), auditEvent.getOrganisationCode());

        var schedulingInfo = auditEvent.getAuditData();
        assertThat(schedulingInfo.getId()).isEqualTo(input.getId());
        assertThat(schedulingInfo.getUuid()).isEqualTo(input.getUuid());
        assertThat(schedulingInfo.getHostPin()).isEqualTo(input.getHostPin());
        assertThat(schedulingInfo.getGuestPin()).isEqualTo(input.getGuestPin());
        assertThat(schedulingInfo.getvMRAvailableBefore()).isEqualTo(input.getVMRAvailableBefore());
        assertThat(schedulingInfo.getvMRStartTime().toInstant()).isEqualTo(input.getvMRStartTime().toInstant());
        assertThat(schedulingInfo.getMaxParticipants()).isEqualTo(input.getMaxParticipants());
        assertThat(schedulingInfo.isEndMeetingOnEndTime()).isEqualTo(input.getEndMeetingOnEndTime());
        assertThat(schedulingInfo.getUriWithDomain()).isEqualTo(input.getUriWithDomain());
        assertThat(schedulingInfo.getUriWithoutDomain()).isEqualTo(input.getUriWithoutDomain());
        assertThat(schedulingInfo.isPoolOverflow()).isEqualTo(input.getPoolOverflow());
        assertThat(schedulingInfo.isPool()).isEqualTo(input.isPool());
        assertThat(schedulingInfo.getVmrType()).isEqualTo(input.getVmrType().toString());
        assertThat(schedulingInfo.getHostView()).isEqualTo(input.getHostView().toString());
        assertThat(schedulingInfo.getGuestView()).isEqualTo(input.getGuestView().toString());
        assertThat(schedulingInfo.getVmrQuality()).isEqualTo(input.getVmrQuality().toString());
        assertThat(schedulingInfo.isEnableOverlayText()).isEqualTo(input.getEnableOverlayText());
        assertThat(schedulingInfo.isGuestsCanPresent()).isEqualTo(input.getGuestsCanPresent());
        assertThat(schedulingInfo.isForcePresenterIntoMain()).isEqualTo(input.getForcePresenterIntoMain());
        assertThat(schedulingInfo.isForceEncryption()).isEqualTo(input.getForceEncryption());
        assertThat(schedulingInfo.isMuteAllGuests()).isEqualTo(input.getMuteAllGuests());
        assertThat(schedulingInfo.getSchedulingTemplate()).isEqualTo(input.getSchedulingTemplate().getId());
        assertThat(schedulingInfo.getProvisionStatus()).isEqualTo(input.getProvisionStatus().toString());
        assertThat(schedulingInfo.getProvisionStatusDescription()).isEqualTo(input.getProvisionStatusDescription());
        assertThat(schedulingInfo.getProvisionTimestamp().toInstant()).isEqualTo(input.getProvisionTimestamp().toInstant());
        assertThat(schedulingInfo.getProvisionVMRId()).isEqualTo(input.getProvisionVMRId());
        assertThat(schedulingInfo.getOrganisation()).isEqualTo(input.getOrganisation().getOrganisationId());
        assertThat(schedulingInfo.getPortalLink()).isEqualTo(input.getPortalLink());
        assertThat(schedulingInfo.getIvrTheme()).isEqualTo(input.getIvrTheme());
        assertThat(schedulingInfo.getCreatedBy()).isEqualTo(input.getMeetingUser().getEmail());
        assertThat(schedulingInfo.getCreatedTime().toInstant()).isEqualTo(input.getCreatedTime().toInstant());
        assertThat(schedulingInfo.getUpdatedBy()).isEqualTo(input.getUpdatedByUser().getEmail());
        assertThat(schedulingInfo.getUpdatedTime().toInstant()).isEqualTo(input.getUpdatedTime().toInstant());
        assertThat(schedulingInfo.getReservationId()).isEqualTo(input.getReservationId());
    }

    private SchedulingInfo createSchedulingInformation() {
        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setId(2L);
        schedulingInfo.setUuid(UUID.randomUUID().toString());
        schedulingInfo.setHostPin(321L);
        schedulingInfo.setGuestPin(543L);
        schedulingInfo.setVMRAvailableBefore(10);
        schedulingInfo.setvMRStartTime(new Date());
        schedulingInfo.setMaxParticipants(12);
        schedulingInfo.setEndMeetingOnEndTime(true);
        schedulingInfo.setMeeting(new Meeting());
        schedulingInfo.setUriWithDomain("uri_with_domain");
        schedulingInfo.setUriWithoutDomain("uri_without_domain");
        schedulingInfo.setSchedulingTemplate(new SchedulingTemplate());
        schedulingInfo.setProvisionStatus(ProvisionStatus.AWAITS_PROVISION);
        schedulingInfo.setProvisionStatusDescription("status_description");
        schedulingInfo.setProvisionTimestamp(new Date());
        schedulingInfo.setProvisionVMRId("vmr_id");
        schedulingInfo.setPortalLink("portal_linke");
        schedulingInfo.setIvrTheme("ivr_theme");
        schedulingInfo.setMeetingUser(new MeetingUser());
        schedulingInfo.setCreatedTime(new Date());
        schedulingInfo.setUpdatedByUser(new MeetingUser());
        schedulingInfo.setUpdatedTime(new Date());
        schedulingInfo.setOrganisation(new Organisation());
        schedulingInfo.setReservationId("reservation_id");
        schedulingInfo.setPoolOverflow(true);
        schedulingInfo.setPool(true);
        schedulingInfo.setVmrType(VmrType.conference);
        schedulingInfo.setHostView(ViewType.one_main_zero_pips);
        schedulingInfo.setGuestView(ViewType.one_main_zero_pips);
        schedulingInfo.setVmrQuality(VmrQuality.sd);
        schedulingInfo.setEnableOverlayText(true);
        schedulingInfo.setGuestsCanPresent(true);
        schedulingInfo.setForcePresenterIntoMain(true);
        schedulingInfo.setForceEncryption(true);
        schedulingInfo.setMuteAllGuests(true);

        return schedulingInfo;
    }


    private Meeting createMeeting() {
        var meeting = new Meeting();
        meeting.setId(2L);
        meeting.setUuid(UUID.randomUUID().toString());
        meeting.setSubject("subject");
        var organisation = new Organisation();
        organisation.setOrganisationId("some_id");
        organisation.setId(123L);
        meeting.setOrganisation(organisation);
        var meetingUser = new MeetingUser();
        meetingUser.setEmail("some_email");
        meeting.setMeetingUser(meetingUser);
        meeting.setCreatedTime(new Date());
        var updatedBy = new MeetingUser();
        updatedBy.setEmail("updated_by");
        meeting.setUpdatedByUser(updatedBy);
        meeting.setUpdatedTime(new Date());
        var organizedBy = new MeetingUser();
        organizedBy.setEmail("organized_by");
        meeting.setOrganizedByUser(organizedBy);
        meeting.setStartTime(new Date());
        meeting.setEndTime(new Date());
        meeting.setDescription("description");
        meeting.setProjectCode("project_code");
        meeting.setShortId("short_id");
        meeting.setExternalId("external_id");
        meeting.setMeetingLabels(Stream.of("label1", "label2").map(x -> {
            MeetingLabel label = new MeetingLabel();
            label.setLabel(x);

            return label;
        }).collect(Collectors.toSet()));
        meeting.setGuestMicrophone(GuestMicrophone.off);
        meeting.setGuestPinRequired(true);

        return meeting;
    }
}
