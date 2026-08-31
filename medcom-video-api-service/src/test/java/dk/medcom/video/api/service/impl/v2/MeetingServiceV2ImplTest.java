package dk.medcom.video.api.service.impl.v2;

import dk.medcom.video.api.controller.exceptions.*;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.ParticipantDao;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.entity.ParticipantRole;
import dk.medcom.video.api.service.*;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.NotValidDataExceptionV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import dk.medcom.video.api.service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.model.DetailedError;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static dk.medcom.video.api.service.impl.v2.HelperMethods.*;
import static org.junit.jupiter.api.Assertions.*;

public class MeetingServiceV2ImplTest {

    private MeetingServiceV2 meetingServiceV2;
    private MeetingService meetingService;
    private MeetingRepository meetingRepository;
    private SchedulingInfoRepository schedulingInfoRepository;
    private ParticipantDao participantDao;
    private final String shortLinkBaseUrl = "base.url";

    @BeforeEach
    public void setup() {
        meetingService = Mockito.mock(MeetingService.class);
        participantDao = Mockito.mock(ParticipantDao.class);
        meetingRepository = Mockito.mock(MeetingRepository.class);
        schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
        Mockito.when(participantDao.findByMeeting(Mockito.any())).thenReturn(List.of());
        meetingServiceV2 = new MeetingServiceV2Impl(meetingService, shortLinkBaseUrl, participantDao, meetingRepository, schedulingInfoRepository);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(meetingService);
        Mockito.verifyNoMoreInteractions(participantDao);
        Mockito.verifyNoMoreInteractions(schedulingInfoRepository);
        Mockito.verifyNoMoreInteractions(meetingRepository);
    }

    @Test
    public void testGetMeetingsV2() throws PermissionDeniedException {
        var fromStartTime = OffsetDateTime.now().minusHours(7);
        var toStartTime = OffsetDateTime.now();

        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetings(Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()))).thenReturn(meetings);

        var result = meetingServiceV2.getMeetingsV2(fromStartTime, toStartTime);
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getFirst().getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getLast().getUuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), shortLinkBaseUrl, res1);
        assertMeeting(meetings.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(meetingService).getMeetings(Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsV2PermissionDenied() throws PermissionDeniedException {
        var fromStartTime = OffsetDateTime.now().minusHours(7);
        var toStartTime = OffsetDateTime.now();

        Mockito.when(meetingService.getMeetings(Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()))).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.getMeetingsV2(fromStartTime, toStartTime));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).getMeetings(Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingByShortIdV2() throws RessourceNotFoundException, PermissionDeniedException {
        var shortId = randomString();

        var meeting = randomMeeting();

        Mockito.when(meetingService.getMeetingByShortId(shortId)).thenReturn(meeting);

        var result = meetingServiceV2.getMeetingByShortIdV2(shortId);
        assertNotNull(result);

        assertMeeting(meeting, shortLinkBaseUrl, result);

        Mockito.verify(meetingService).getMeetingByShortId(shortId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingByShortIdV2ResourceNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        var shortId = randomString();

        Mockito.when(meetingService.getMeetingByShortId(shortId)).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> meetingServiceV2.getMeetingByShortIdV2(shortId));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingByShortId(shortId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingByShortIdV2PermissionDenied() throws RessourceNotFoundException, PermissionDeniedException {
        var shortId = randomString();

        Mockito.when(meetingService.getMeetingByShortId(shortId)).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.getMeetingByShortIdV2(shortId));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingByShortId(shortId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsBySubjectV2() throws PermissionDeniedException {
        var subject = randomString();

        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetingsBySubject(subject)).thenReturn(meetings);

        var result = meetingServiceV2.getMeetingsBySubjectV2(subject);
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getFirst().getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getLast().getUuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), shortLinkBaseUrl, res1);
        assertMeeting(meetings.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(meetingService).getMeetingsBySubject(subject);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsBySubjectV2PermissionDenied() throws PermissionDeniedException {
        var subject = randomString();

        Mockito.when(meetingService.getMeetingsBySubject(subject)).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.getMeetingsBySubjectV2(subject));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingsBySubject(subject);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByOrganizedByV2() throws PermissionDeniedException {
        var organizedBy = randomString();

        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetingsByOrganizedBy(organizedBy)).thenReturn(meetings);

        var result = meetingServiceV2.getMeetingsByOrganizedByV2(organizedBy);
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getFirst().getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getLast().getUuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), shortLinkBaseUrl, res1);
        assertMeeting(meetings.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(meetingService).getMeetingsByOrganizedBy(organizedBy);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByOrganizedByV2PermissionDenied() throws PermissionDeniedException {
        var organizedBy = randomString();

        Mockito.when(meetingService.getMeetingsByOrganizedBy(organizedBy)).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.getMeetingsByOrganizedByV2(organizedBy));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingsByOrganizedBy(organizedBy);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByUriWithDomainV2() throws PermissionDeniedException {
        var uriWithDomain = randomString();

        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetingsByUriWithDomain(uriWithDomain)).thenReturn(meetings);

        var result = meetingServiceV2.getMeetingsByUriWithDomainV2(uriWithDomain);
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getFirst().getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getLast().getUuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), shortLinkBaseUrl, res1);
        assertMeeting(meetings.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(meetingService).getMeetingsByUriWithDomain(uriWithDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByUriWithDomainV2PermissionDenied() throws PermissionDeniedException {
        var uriWithDomain = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithDomain(uriWithDomain)).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.getMeetingsByUriWithDomainV2(uriWithDomain));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithDomain(uriWithDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testSearchMeetingsV2() throws PermissionDeniedException {
        var search = randomString();
        var fromStartTime = OffsetDateTime.now().minusHours(7);
        var toStartTime = OffsetDateTime.now();

        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.searchMeetings(search, Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()))).thenReturn(meetings);

        var result = meetingServiceV2.searchMeetingsV2(search, fromStartTime, toStartTime);
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getFirst().getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getLast().getUuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), shortLinkBaseUrl, res1);
        assertMeeting(meetings.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(meetingService).searchMeetings(search, Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testSearchMeetingsV2PermissionDenied() throws PermissionDeniedException {
        var search = randomString();
        var fromStartTime = OffsetDateTime.now().minusHours(7);
        var toStartTime = OffsetDateTime.now();

        Mockito.when(meetingService.searchMeetings(search, Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()))).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.searchMeetingsV2(search, fromStartTime, toStartTime));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).searchMeetings(search, Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByUriWithDomainSingleV2() throws RessourceNotFoundException, PermissionDeniedException {
        var uriWithDomain = randomString();

        var meeting = randomMeeting();

        Mockito.when(meetingService.getMeetingsByUriWithDomainSingle(uriWithDomain)).thenReturn(meeting);

        var result = meetingServiceV2.getMeetingsByUriWithDomainSingleV2(uriWithDomain);
        assertNotNull(result);

        assertMeeting(meeting, shortLinkBaseUrl, result);

        Mockito.verify(meetingService).getMeetingsByUriWithDomainSingle(uriWithDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByUriWithDomainSingleV2ResourceNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        var uriWithDomain = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithDomainSingle(uriWithDomain)).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> meetingServiceV2.getMeetingsByUriWithDomainSingleV2(uriWithDomain));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithDomainSingle(uriWithDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByUriWithDomainSingleV2PermissionDenied() throws RessourceNotFoundException, PermissionDeniedException {
        var uriWithDomain = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithDomainSingle(uriWithDomain)).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.getMeetingsByUriWithDomainSingleV2(uriWithDomain));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithDomainSingle(uriWithDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByUriWithoutDomainV2() throws RessourceNotFoundException, PermissionDeniedException {
        var uriWithoutDomain = randomString();

        var meeting = randomMeeting();

        Mockito.when(meetingService.getMeetingsByUriWithoutDomain(uriWithoutDomain)).thenReturn(meeting);

        var result = meetingServiceV2.getMeetingsByUriWithoutDomainV2(uriWithoutDomain);
        assertNotNull(result);

        assertMeeting(meeting, shortLinkBaseUrl, result);

        Mockito.verify(meetingService).getMeetingsByUriWithoutDomain(uriWithoutDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByUriWithoutDomainV2ResourceNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        var uriWithoutDomain = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithoutDomain(uriWithoutDomain)).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> meetingServiceV2.getMeetingsByUriWithoutDomainV2(uriWithoutDomain));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithoutDomain(uriWithoutDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByUriWithoutDomainV2PermissionDenied() throws RessourceNotFoundException, PermissionDeniedException {
        var uriWithoutDomain = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithoutDomain(uriWithoutDomain)).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.getMeetingsByUriWithoutDomainV2(uriWithoutDomain));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithoutDomain(uriWithoutDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByLabelV2() throws PermissionDeniedException {
        var label = randomString();

        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetingsByLabel(label)).thenReturn(meetings);

        var result = meetingServiceV2.getMeetingsByLabelV2(label);
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getFirst().getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(meetings.getLast().getUuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), shortLinkBaseUrl, res1);
        assertMeeting(meetings.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(meetingService).getMeetingsByLabel(label);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingsByLabelV2PermissionDenied() throws PermissionDeniedException {
        var label = randomString();

        Mockito.when(meetingService.getMeetingsByLabel(label)).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.getMeetingsByLabelV2(label));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingsByLabel(label);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingByUuidV2() throws RessourceNotFoundException, PermissionDeniedException {
        var uuid = UUID.randomUUID();

        var meeting = randomMeeting();

        Mockito.when(meetingService.getMeetingByUuid(uuid.toString())).thenReturn(meeting);

        var result = meetingServiceV2.getMeetingByUuidV2(uuid);
        assertNotNull(result);

        assertMeeting(meeting, shortLinkBaseUrl, result);

        Mockito.verify(meetingService).getMeetingByUuid(uuid.toString());
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingByUuidV2ResourceNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        var uuid = UUID.randomUUID();

        Mockito.when(meetingService.getMeetingByUuid(uuid.toString())).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> meetingServiceV2.getMeetingByUuidV2(uuid));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingByUuid(uuid.toString());
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingByUuidV2PermissionDenied() throws RessourceNotFoundException, PermissionDeniedException {
        var uuid = UUID.randomUUID();

        Mockito.when(meetingService.getMeetingByUuid(uuid.toString())).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.getMeetingByUuidV2(uuid));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).getMeetingByUuid(uuid.toString());
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateMeetingV2() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var input = randomCreateMeetingModel();

        var meeting = randomMeeting();
        Mockito.when(meetingService.createMeeting(Mockito.any())).thenReturn(meeting);

        var result = meetingServiceV2.createMeetingV2(input);
        assertNotNull(result);

        assertMeeting(meeting, shortLinkBaseUrl, result);

        Mockito.verify(meetingService).createMeeting(Mockito.argThat(x -> assertCreateMeeting(input, x)));

        if (input.participants() != null) {
            for (var expectedParticipant : input.participants()) {
                Mockito.verify(participantDao).save(Mockito.argThat(actual ->
                        actual.meetingId().equals(meeting.getId())
                                && actual.meetingUuid().equals(UUID.fromString(meeting.getUuid()))
                                && actual.type() == expectedParticipant.type()
                                && actual.participantId().equals(expectedParticipant.participantId())
                                && java.util.Objects.equals(actual.organisationId(), expectedParticipant.organisation())
                                && actual.role() == expectedParticipant.role()));
            }
        }

        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateMeetingV2NotValidData() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var input = randomCreateMeetingModel();

        Mockito.when(meetingService.createMeeting(Mockito.any())).thenThrow(new NotValidDataException(NotValidDataErrors.CUSTOM_MEETING_ADDRESS_NOT_ALLOWED));

        var expectedException = assertThrows(NotValidDataExceptionV2.class, () -> meetingServiceV2.createMeetingV2(input));
        assertNotNull(expectedException);
        assertEquals("Organisation does not allow setting custom meeting address.", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._31, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).createMeeting(Mockito.argThat(x -> assertCreateMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateMeetingV2NotAcceptable() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var input = randomCreateMeetingModel();

        Mockito.when(meetingService.createMeeting(Mockito.any())).thenThrow(new NotAcceptableException(NotAcceptableErrors.HOST_PINCODE_ASSIGNMENT_FAILED));

        var expectedException = assertThrows(NotAcceptableExceptionV2.class, () -> meetingServiceV2.createMeetingV2(input));
        assertNotNull(expectedException);
        assertEquals("The host pincode assignment failed due to invalid setup on the template used", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._17, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).createMeeting(Mockito.argThat(x -> assertCreateMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateMeetingV2PermissionDenied() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var input = randomCreateMeetingModel();

        Mockito.when(meetingService.createMeeting(Mockito.any())).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.createMeetingV2(input));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).createMeeting(Mockito.argThat(x -> assertCreateMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateMeetingV2() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingModel();

        var meeting = randomMeeting();
        Mockito.when(meetingService.updateMeeting(Mockito.eq(uuid.toString()), Mockito.any())).thenReturn(meeting);

        var result = meetingServiceV2.updateMeetingV2(uuid, input);
        assertNotNull(result);

        assertMeeting(meeting, shortLinkBaseUrl, result);

        Mockito.verify(meetingService).updateMeeting(Mockito.eq(uuid.toString()), Mockito.argThat(x -> assertUpdateMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateMeetingV2ResourceNotFound() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingModel();

        Mockito.when(meetingService.updateMeeting(Mockito.eq(uuid.toString()), Mockito.any())).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> meetingServiceV2.updateMeetingV2(uuid, input));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(meetingService).updateMeeting(Mockito.eq(uuid.toString()), Mockito.argThat(x -> assertUpdateMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateMeetingV2NotValidData() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingModel();

        Mockito.when(meetingService.updateMeeting(Mockito.eq(uuid.toString()), Mockito.any())).thenThrow(new NotValidDataException(NotValidDataErrors.SCHEDULING_TEMPLATE_NOT_IN_ORGANISATION, "message1", "message2"));

        var expectedException = assertThrows(NotValidDataExceptionV2.class, () -> meetingServiceV2.updateMeetingV2(uuid, input));
        assertNotNull(expectedException);
        assertEquals("Scheduling template message1 does not belong to organisation message2.", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._27, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).updateMeeting(Mockito.eq(uuid.toString()), Mockito.argThat(x -> assertUpdateMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateMeetingV2NotAcceptable() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingModel();

        Mockito.when(meetingService.updateMeeting(Mockito.eq(uuid.toString()), Mockito.any())).thenThrow(new NotAcceptableException(NotAcceptableErrors.GUEST_PINCODE_ASSIGNMENT_FAILED, "message1", "message2"));

        var expectedException = assertThrows(NotAcceptableExceptionV2.class, () -> meetingServiceV2.updateMeetingV2(uuid, input));
        assertNotNull(expectedException);
        assertEquals("The guest pincode assignment failed due to invalid setup on the template used", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._16, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).updateMeeting(Mockito.eq(uuid.toString()), Mockito.argThat(x -> assertUpdateMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateMeetingV2PermissionDenied() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingModel();

        Mockito.when(meetingService.updateMeeting(Mockito.eq(uuid.toString()), Mockito.any())).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.updateMeetingV2(uuid, input));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).updateMeeting(Mockito.eq(uuid.toString()), Mockito.argThat(x -> assertUpdateMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteMeetingV2() throws RessourceNotFoundException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();

        meetingServiceV2.deleteMeetingV2(uuid);

        Mockito.verify(meetingService).deleteMeeting(uuid.toString());
        verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteMeetingV2ResourceNotFound() throws RessourceNotFoundException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();

        Mockito.doThrow(new RessourceNotFoundException("resource", "field")).when(meetingService).deleteMeeting(uuid.toString());

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> meetingServiceV2.deleteMeetingV2(uuid));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(meetingService).deleteMeeting(uuid.toString());
        verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteMeetingV2NotAcceptable() throws RessourceNotFoundException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();

        Mockito.doThrow(new NotAcceptableException(NotAcceptableErrors.MUST_HAVE_STATUS_AWAITS_PROVISION_OR_PROVISIONED_OK)).when(meetingService).deleteMeeting(uuid.toString());

        var expectedException = assertThrows(NotAcceptableExceptionV2.class, () -> meetingServiceV2.deleteMeetingV2(uuid));
        assertNotNull(expectedException);
        assertEquals("Meeting must have status AWAITS_PROVISION (0)  or PROVISIONED_OK (3) in order to be updated", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._12, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).deleteMeeting(uuid.toString());
        verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteMeetingV2PermissionDenied() throws RessourceNotFoundException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();

        Mockito.doThrow(new PermissionDeniedException()).when(meetingService).deleteMeeting(uuid.toString());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.deleteMeetingV2(uuid));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).deleteMeeting(uuid.toString());
        verifyNoMoreInteractions();
    }

    @Test
    public void testPatchMeetingV2() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingModel();
        var meeting = randomMeeting();
        Mockito.when(meetingService.patchMeeting(Mockito.eq(uuid), Mockito.any())).thenReturn(meeting);

        var result = meetingServiceV2.patchMeetingV2(uuid, input);
        assertNotNull(result);

        assertMeeting(meeting, shortLinkBaseUrl, result);

        Mockito.verify(meetingService).patchMeeting(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testPatchMeetingV2NullValues() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = new PatchMeetingModel(null, null, null, null, null, null,
                null, null, null, null, null, null, null);
        var meeting = randomMeeting();
        Mockito.when(meetingService.patchMeeting(Mockito.eq(uuid), Mockito.any())).thenReturn(meeting);

        var result = meetingServiceV2.patchMeetingV2(uuid, input);
        assertNotNull(result);

        assertMeeting(meeting, shortLinkBaseUrl, result);

        Mockito.verify(meetingService).patchMeeting(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testPatchMeetingV2ResourceNotFound() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingModel();

        Mockito.when(meetingService.patchMeeting(Mockito.eq(uuid), Mockito.any())).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> meetingServiceV2.patchMeetingV2(uuid, input));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(meetingService).patchMeeting(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testPatchMeetingV2NotValidData() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingModel();

        Mockito.when(meetingService.patchMeeting(Mockito.eq(uuid), Mockito.any())).thenThrow(new NotValidDataException(NotValidDataErrors.NON_AD_HOC_ORGANIZATION, "message"));

        var expectedException = assertThrows(NotValidDataExceptionV2.class, () -> meetingServiceV2.patchMeetingV2(uuid, input));
        assertNotNull(expectedException);
        assertEquals("Can not create ad hoc meeting on non ad hoc organization: message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._19, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).patchMeeting(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testPatchMeetingV2NotAcceptable() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingModel();

        Mockito.when(meetingService.patchMeeting(Mockito.eq(uuid), Mockito.any())).thenThrow(new NotAcceptableException(NotAcceptableErrors.MUST_HAVE_STATUS_AWAITS_PROVISION, "message1", "message2"));

        var expectedException = assertThrows(NotAcceptableExceptionV2.class, () -> meetingServiceV2.patchMeetingV2(uuid, input));
        assertNotNull(expectedException);
        assertEquals("Meeting must have status AWAITS_PROVISION (0) in order to be deleted", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._13, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).patchMeeting(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(input, x)));
        verifyNoMoreInteractions();
    }


    @Test
    public void testPatchMeetingV2PermissionDenied() throws RessourceNotFoundException, NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingModel();

        Mockito.when(meetingService.patchMeeting(Mockito.eq(uuid), Mockito.any())).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> meetingServiceV2.patchMeetingV2(uuid, input));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(meetingService).patchMeeting(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingParticipations() {
        var participantId = randomString();

        var meeting1 = randomMeeting();
        var meeting2 = randomMeeting();

        var participant1 = randomParticipant(meeting1.getId());
        var participant2 = randomParticipant(meeting2.getId());

        var schedulingInfo1 = randomSchedulingInfo();
        schedulingInfo1.setMeeting(meeting1);

        var schedulingInfo2 = randomSchedulingInfo();
        schedulingInfo2.setMeeting(meeting2);

        Mockito.when(participantDao.findByParticipantId(participantId)).thenReturn(List.of(participant1, participant2));
        Mockito.when(meetingRepository.findAllById(List.of(meeting1.getId(), meeting2.getId())))
                .thenReturn(List.of(meeting1, meeting2));
        Mockito.when(schedulingInfoRepository.findByMeetingIn(List.of(meeting1, meeting2)))
                .thenReturn(List.of(schedulingInfo1, schedulingInfo2));

        var result = meetingServiceV2.getMeetingParticipations(participantId, null, null);
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(meeting1.getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(meeting2.getUuid())).findFirst().orElseThrow();

        assertMeetingParticipation(meeting1, schedulingInfo1, participant1, 0, shortLinkBaseUrl, res1);
        assertMeetingParticipation(meeting2, schedulingInfo2, participant2, 0, shortLinkBaseUrl, res2);

        Mockito.verify(participantDao).findByParticipantId(participantId);
        Mockito.verify(meetingRepository).findAllById(List.of(meeting1.getId(), meeting2.getId()));
        Mockito.verify(schedulingInfoRepository).findByMeetingIn(List.of(meeting1, meeting2));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingParticipationsFilteredByStartTimeInterval() {
        var participantId = randomString();
        var meeting = randomMeeting();
        var participant = randomParticipant(meeting.getId());
        var schedulingInfo = randomSchedulingInfo();
        schedulingInfo.setMeeting(meeting);

        Mockito.when(participantDao.findByParticipantId(participantId)).thenReturn(List.of(participant));
        Mockito.when(meetingRepository.findAllById(List.of(meeting.getId()))).thenReturn(List.of(meeting));
        Mockito.when(schedulingInfoRepository.findByMeetingIn(List.of(meeting))).thenReturn(List.of(schedulingInfo));

        var meetingStart = meeting.getStartTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime();
        var fromStartTime = meetingStart.minusHours(1);
        var toStartTime = meetingStart.plusHours(1);

        var result = meetingServiceV2.getMeetingParticipations(participantId, fromStartTime, toStartTime);
        assertEquals(1, result.size());
        assertMeetingParticipation(meeting, schedulingInfo, participant, 0, shortLinkBaseUrl, result.getFirst());

        Mockito.verify(participantDao).findByParticipantId(participantId);
        Mockito.verify(meetingRepository).findAllById(List.of(meeting.getId()));
        Mockito.verify(schedulingInfoRepository).findByMeetingIn(List.of(meeting));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingParticipationsFilteredByStartTimeIntervalNoMatch() {
        var participantId = randomString();
        var meeting = randomMeeting();
        var participant = randomParticipant(meeting.getId());

        Mockito.when(participantDao.findByParticipantId(participantId)).thenReturn(List.of(participant));
        Mockito.when(meetingRepository.findAllById(List.of(meeting.getId()))).thenReturn(List.of(meeting));
        Mockito.when(schedulingInfoRepository.findByMeetingIn(List.of())).thenReturn(List.of());

        var fromStartTime = OffsetDateTime.now().plusDays(10);
        var toStartTime = OffsetDateTime.now().plusDays(20);

        var result = meetingServiceV2.getMeetingParticipations(participantId, fromStartTime, toStartTime);
        assertEquals(0, result.size());

        Mockito.verify(participantDao).findByParticipantId(participantId);
        Mockito.verify(meetingRepository).findAllById(List.of(meeting.getId()));
        Mockito.verify(schedulingInfoRepository).findByMeetingIn(List.of());
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingParticipationsPinIsHostPinWhenParticipantIsHost() {
        var participantId = randomString();
        var meeting = randomMeeting();
        var participant = randomParticipant(meeting.getId(), ParticipantRole.HOST);
        var schedulingInfo = randomSchedulingInfo();
        schedulingInfo.setMeeting(meeting);

        Mockito.when(participantDao.findByParticipantId(participantId)).thenReturn(List.of(participant));
        Mockito.when(meetingRepository.findAllById(List.of(meeting.getId()))).thenReturn(List.of(meeting));
        Mockito.when(schedulingInfoRepository.findByMeetingIn(List.of(meeting))).thenReturn(List.of(schedulingInfo));

        var result = meetingServiceV2.getMeetingParticipations(participantId, null, null);
        assertEquals(1, result.size());
        assertEquals(schedulingInfo.getHostPin(), result.getFirst().pin());

        Mockito.verify(participantDao).findByParticipantId(participantId);
        Mockito.verify(meetingRepository).findAllById(List.of(meeting.getId()));
        Mockito.verify(schedulingInfoRepository).findByMeetingIn(List.of(meeting));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingParticipationsPinIsGuestPinWhenParticipantIsGuest() {
        var participantId = randomString();
        var meeting = randomMeeting();
        var participant = randomParticipant(meeting.getId(), ParticipantRole.GUEST);
        var schedulingInfo = randomSchedulingInfo();
        schedulingInfo.setMeeting(meeting);

        Mockito.when(participantDao.findByParticipantId(participantId)).thenReturn(List.of(participant));
        Mockito.when(meetingRepository.findAllById(List.of(meeting.getId()))).thenReturn(List.of(meeting));
        Mockito.when(schedulingInfoRepository.findByMeetingIn(List.of(meeting))).thenReturn(List.of(schedulingInfo));

        var result = meetingServiceV2.getMeetingParticipations(participantId, null, null);
        assertEquals(1, result.size());
        assertEquals(schedulingInfo.getGuestPin(), result.getFirst().pin());

        Mockito.verify(participantDao).findByParticipantId(participantId);
        Mockito.verify(meetingRepository).findAllById(List.of(meeting.getId()));
        Mockito.verify(schedulingInfoRepository).findByMeetingIn(List.of(meeting));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingParticipationsNoSchedulingInfo() {
        var participantId = randomString();
        var meeting = randomMeeting();
        var participant = randomParticipant(meeting.getId());

        Mockito.when(participantDao.findByParticipantId(participantId)).thenReturn(List.of(participant));
        Mockito.when(meetingRepository.findAllById(List.of(meeting.getId()))).thenReturn(List.of(meeting));
        Mockito.when(schedulingInfoRepository.findByMeetingIn(List.of(meeting))).thenReturn(List.of());

        var result = meetingServiceV2.getMeetingParticipations(participantId, null, null);
        assertEquals(1, result.size());
        assertEquals(0, result.getFirst().pin());
        assertNull(result.getFirst().uriWithDomain());
        assertNull(result.getFirst().portalLink());

        Mockito.verify(participantDao).findByParticipantId(participantId);
        Mockito.verify(meetingRepository).findAllById(List.of(meeting.getId()));
        Mockito.verify(schedulingInfoRepository).findByMeetingIn(List.of(meeting));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingParticipationsMeetingNotFound() {
        var participantId = randomString();
        var participant = randomParticipant(999999L);

        Mockito.when(participantDao.findByParticipantId(participantId)).thenReturn(List.of(participant));
        Mockito.when(meetingRepository.findAllById(List.of(participant.meetingId()))).thenReturn(List.of());

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class,
                () -> meetingServiceV2.getMeetingParticipations(participantId, null, null));
        assertNotNull(expectedException);

        Mockito.verify(participantDao).findByParticipantId(participantId);
        Mockito.verify(meetingRepository).findAllById(List.of(participant.meetingId()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetMeetingParticipationsEmptyParticipantList() {
        var participantId = randomString();

        Mockito.when(participantDao.findByParticipantId(participantId)).thenReturn(List.of());

        var result = meetingServiceV2.getMeetingParticipations(participantId, null, null);
        assertNotNull(result);
        assertEquals(0, result.size());

        Mockito.verify(participantDao).findByParticipantId(participantId);
        verifyNoMoreInteractions();
    }
}