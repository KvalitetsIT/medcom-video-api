package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.controller.v2.exception.NotAcceptableException;
import dk.medcom.video.api.controller.v2.exception.NotValidDataException;
import dk.medcom.video.api.controller.v2.exception.PermissionDeniedException;
import dk.medcom.video.api.controller.v2.exception.ResourceNotFoundException;
import dk.medcom.video.api.service.MeetingServiceV2;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.NotValidDataExceptionV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static dk.medcom.video.api.controller.v2.HelperMethods.*;
import static org.junit.Assert.*;

public class VideoMeetingsControllerV2Test {

    private VideoMeetingsControllerV2 videoMeetingsControllerV2;
    private MeetingServiceV2 meetingService;

    @Before
    public void setup() {
        meetingService = Mockito.mock(MeetingServiceV2.class);

        videoMeetingsControllerV2 = new VideoMeetingsControllerV2(meetingService);
    }
    
    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(meetingService);
    }

    @Test
    public void testV2MeetingsFindByUriWithDomainGet() {
        var input = randomString();
        var meeting = randomMeeting();

        Mockito.when(meetingService.getMeetingsByUriWithDomainSingleV2(input)).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsFindByUriWithDomainGet(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertMeeting(meeting, result.getBody());

        Mockito.verify(meetingService).getMeetingsByUriWithDomainSingleV2(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsFindByUriWithDomainGetPermissionDenied() {
        var input = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithDomainSingleV2(input)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsFindByUriWithDomainGet(input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithDomainSingleV2(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsFindByUriWithDomainGetResourceNotFound() {
        var input = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithDomainSingleV2(input)).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoMeetingsControllerV2.v2MeetingsFindByUriWithDomainGet(input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithDomainSingleV2(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsFindByUriWithoutDomainGet() {
        var input = randomString();
        var meeting = randomMeeting();

        Mockito.when(meetingService.getMeetingsByUriWithoutDomainV2(input)).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsFindByUriWithoutDomainGet(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertMeeting(meeting, result.getBody());

        Mockito.verify(meetingService).getMeetingsByUriWithoutDomainV2(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsFindByUriWithoutDomainGetPermissionDenied() {
        var input = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithoutDomainV2(input)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsFindByUriWithoutDomainGet(input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithoutDomainV2(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsFindByUriWithoutDomainGetResourceNotFound() {
        var input = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithoutDomainV2(input)).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoMeetingsControllerV2.v2MeetingsFindByUriWithoutDomainGet(input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithoutDomainV2(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetByShortId() {
        var shortId = randomString();
        var meeting = randomMeeting();

        Mockito.when(meetingService.getMeetingByShortIdV2(shortId)).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsGet(null, null, shortId, null,
                null, null, null, null);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertMeeting(meeting, result.getBody().getFirst());

        Mockito.verify(meetingService).getMeetingByShortIdV2(shortId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetByShortIdResourceNotFound() {
        var shortId = randomString();

        Mockito.when(meetingService.getMeetingByShortIdV2(shortId)).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoMeetingsControllerV2.v2MeetingsGet(null, null, shortId, null,
                null, null, null, null));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingByShortIdV2(shortId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetByShortIdPermissionDenied() {
        var shortId = randomString();

        Mockito.when(meetingService.getMeetingByShortIdV2(shortId)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsGet(null, null, shortId, null,
                null, null, null, null));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingByShortIdV2(shortId);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetByLabel() {
        var label = randomString();
        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetingsByLabelV2(label)).thenReturn(meetings);

        var result = videoMeetingsControllerV2.v2MeetingsGet(null, null, null, null,
                null, null, label, null);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        var res1 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getFirst().uuid())).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getLast().uuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), res1);
        assertMeeting(meetings.getLast(), res2);

        Mockito.verify(meetingService).getMeetingsByLabelV2(label);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetByLabelPermissionDenied() {
        var label = randomString();

        Mockito.when(meetingService.getMeetingsByLabelV2(label)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsGet(null, null, null, null,
                null, null, label, null));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingsByLabelV2(label);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetBySubject() {
        var subject = randomString();
        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetingsBySubjectV2(subject)).thenReturn(meetings);

        var result = videoMeetingsControllerV2.v2MeetingsGet(null, null, null, subject,
                null, null, null, null);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        var res1 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getFirst().uuid())).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getLast().uuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), res1);
        assertMeeting(meetings.getLast(), res2);

        Mockito.verify(meetingService).getMeetingsBySubjectV2(subject);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetBySubjectPermissionDenied() {
        var subject = randomString();

        Mockito.when(meetingService.getMeetingsBySubjectV2(subject)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsGet(null, null, null, subject,
                null, null, null, null));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingsBySubjectV2(subject);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetOrganizedBy() {
        var organizer = randomString();
        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetingsByOrganizedByV2(organizer)).thenReturn(meetings);

        var result = videoMeetingsControllerV2.v2MeetingsGet(null, null, null, null,
                organizer, null, null, null);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        var res1 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getFirst().uuid())).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getLast().uuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), res1);
        assertMeeting(meetings.getLast(), res2);

        Mockito.verify(meetingService).getMeetingsByOrganizedByV2(organizer);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetOrganizedByPermissionDenied() {
        var organizer = randomString();

        Mockito.when(meetingService.getMeetingsByOrganizedByV2(organizer)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsGet(null, null, null, null,
                organizer, null, null, null));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingsByOrganizedByV2(organizer);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetSearch() {
        var search = randomString();
        var fromStartTime = OffsetDateTime.now().minusHours(5);
        var toStartTime = OffsetDateTime.now();
        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.searchMeetingsV2(search, fromStartTime, toStartTime)).thenReturn(meetings);

        var result = videoMeetingsControllerV2.v2MeetingsGet(fromStartTime, toStartTime, null, null,
                null, search, null, null);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        var res1 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getFirst().uuid())).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getLast().uuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), res1);
        assertMeeting(meetings.getLast(), res2);

        Mockito.verify(meetingService).searchMeetingsV2(search, fromStartTime, toStartTime);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetSearchPermissionDenied() {
        var search = randomString();
        var fromStartTime = OffsetDateTime.now().minusHours(5);
        var toStartTime = OffsetDateTime.now();

        Mockito.when(meetingService.searchMeetingsV2(search, fromStartTime, toStartTime)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsGet(fromStartTime, toStartTime, null, null,
                null, search, null, null));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).searchMeetingsV2(search, fromStartTime, toStartTime);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetStartTime() {
        var fromStartTime = OffsetDateTime.now().minusHours(5);
        var toStartTime = OffsetDateTime.now();
        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetingsV2(fromStartTime, toStartTime)).thenReturn(meetings);

        var result = videoMeetingsControllerV2.v2MeetingsGet(fromStartTime, toStartTime, null, null,
                null, null, null, null);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        var res1 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getFirst().uuid())).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getLast().uuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), res1);
        assertMeeting(meetings.getLast(), res2);

        Mockito.verify(meetingService).getMeetingsV2(fromStartTime, toStartTime);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetStartTimePermissionDenied() {
        var fromStartTime = OffsetDateTime.now().minusHours(5);
        var toStartTime = OffsetDateTime.now();

        Mockito.when(meetingService.getMeetingsV2(fromStartTime, toStartTime)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsGet(fromStartTime, toStartTime, null, null,
                null, null, null, null));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingsV2(fromStartTime, toStartTime);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetByUriWithDomain() {
        var uriWithDomain = randomString();
        var meetings = List.of(randomMeeting(), randomMeeting());

        Mockito.when(meetingService.getMeetingsByUriWithDomainV2(uriWithDomain)).thenReturn(meetings);

        var result = videoMeetingsControllerV2.v2MeetingsGet(null, null, null, null,
                null, null, null, uriWithDomain);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        var res1 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getFirst().uuid())).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getUuid().equals(meetings.getLast().uuid())).findFirst().orElseThrow();

        assertMeeting(meetings.getFirst(), res1);
        assertMeeting(meetings.getLast(), res2);

        Mockito.verify(meetingService).getMeetingsByUriWithDomainV2(uriWithDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetByUriWithDomainPermissionDenied() {
        var uriWithDomain = randomString();

        Mockito.when(meetingService.getMeetingsByUriWithDomainV2(uriWithDomain)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsGet(null, null, null, null,
                null, null, null, uriWithDomain));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingsByUriWithDomainV2(uriWithDomain);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsGetNoQueryParametersSet() {
        var expectedException = assertThrows(NotValidDataException.class, () -> videoMeetingsControllerV2.v2MeetingsGet(null, null, null, null,
                null, null, null, null));
        assertNotNull(expectedException);
        assertEquals(400, expectedException.getHttpStatus().value());
        assertEquals(DetailedError.DetailedErrorCodeEnum._36, expectedException.getDetailedErrorCode());
        assertEquals("Must set at least one query parameter, when searching for meeting.", expectedException.getDetailedError());

        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsPost() {
        var input = randomCreateMeetingInput();

        var meeting = randomMeeting();
        Mockito.when(meetingService.createMeetingV2(Mockito.any())).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsPost(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertMeeting(meeting, result.getBody());

        Mockito.verify(meetingService).createMeetingV2(Mockito.argThat(x -> assertCreateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsPostOnlyRequiredValues() {
        var input = new CreateMeeting().subject(randomString()).startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now());

        var meeting = randomMeeting();
        Mockito.when(meetingService.createMeetingV2(Mockito.any())).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsPost(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertMeeting(meeting, result.getBody());

        Mockito.verify(meetingService).createMeetingV2(Mockito.argThat(x -> assertCreateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsPostPermissionDenied() {
        var input = randomCreateMeetingInput();

        Mockito.when(meetingService.createMeetingV2(Mockito.any())).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsPost(input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).createMeetingV2(Mockito.argThat(x -> assertCreateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsPostNotAcceptable() {
        var input = randomCreateMeetingInput();

        Mockito.when(meetingService.createMeetingV2(Mockito.any())).thenThrow(new NotAcceptableExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotAcceptableException.class, () -> videoMeetingsControllerV2.v2MeetingsPost(input));
        assertNotNull(expectedException);
        assertEquals(406, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).createMeetingV2(Mockito.argThat(x -> assertCreateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsPostNotValidData() {
        var input = randomCreateMeetingInput();

        Mockito.when(meetingService.createMeetingV2(Mockito.any())).thenThrow(new NotValidDataExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotValidDataException.class, () -> videoMeetingsControllerV2.v2MeetingsPost(input));
        assertNotNull(expectedException);
        assertEquals(400, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).createMeetingV2(Mockito.argThat(x -> assertCreateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidDelete() {
        var uuid = UUID.randomUUID();

        var result = videoMeetingsControllerV2.v2MeetingsUuidDelete(uuid);
        assertNotNull(result);
        assertEquals(204, result.getStatusCode().value());

        Mockito.verify(meetingService).deleteMeetingV2(uuid);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidDeleteResourceNotFound() {
        var uuid = UUID.randomUUID();

        Mockito.doThrow(new ResourceNotFoundExceptionV2("Message1", "Message2")).when(meetingService).deleteMeetingV2(uuid);

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidDelete(uuid));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(meetingService).deleteMeetingV2(uuid);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidDeletePermissionDenied() {
        var uuid = UUID.randomUUID();

        Mockito.doThrow(new PermissionDeniedExceptionV2()).when(meetingService).deleteMeetingV2(uuid);

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidDelete(uuid));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).deleteMeetingV2(uuid);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidDeleteNotAcceptable() {
        var uuid = UUID.randomUUID();

        Mockito.doThrow(new NotAcceptableExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message")).when(meetingService).deleteMeetingV2(uuid);

        var expectedException = assertThrows(NotAcceptableException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidDelete(uuid));
        assertNotNull(expectedException);
        assertEquals(406, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).deleteMeetingV2(uuid);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidGet() {
        var input = UUID.randomUUID();
        var meeting = randomMeeting();

        Mockito.when(meetingService.getMeetingByUuidV2(input)).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsUuidGet(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertMeeting(meeting, result.getBody());

        Mockito.verify(meetingService).getMeetingByUuidV2(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidGetResourceNotFound() {
        var input = UUID.randomUUID();

        Mockito.when(meetingService.getMeetingByUuidV2(input)).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidGet(input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingByUuidV2(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidGetPermissionDenied() {
        var input = UUID.randomUUID();

        Mockito.when(meetingService.getMeetingByUuidV2(input)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidGet(input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).getMeetingByUuidV2(input);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPatch() {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingInput();

        var meeting = randomMeeting();
        Mockito.when(meetingService.patchMeetingV2(Mockito.eq(uuid), Mockito.any())).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsUuidPatch(uuid, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertMeeting(meeting, result.getBody());

        Mockito.verify(meetingService).patchMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPatchOnlyRequiredValues() {
        var uuid = UUID.randomUUID();
        var input = new PatchMeeting();

        var meeting = randomMeeting();
        Mockito.when(meetingService.patchMeetingV2(Mockito.eq(uuid), Mockito.any())).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsUuidPatch(uuid, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertMeeting(meeting, result.getBody());

        Mockito.verify(meetingService).patchMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPatchPermissionDenied() {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingInput();

        Mockito.when(meetingService.patchMeetingV2(Mockito.eq(uuid), Mockito.any())).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidPatch(uuid, input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).patchMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPatchNotValidData() {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingInput();

        Mockito.when(meetingService.patchMeetingV2(Mockito.eq(uuid), Mockito.any())).thenThrow(new NotValidDataExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotValidDataException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidPatch(uuid, input));
        assertNotNull(expectedException);
        assertEquals(400, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).patchMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPatchResourceNotFound() {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingInput();

        Mockito.when(meetingService.patchMeetingV2(Mockito.eq(uuid), Mockito.any())).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidPatch(uuid, input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(meetingService).patchMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPatchNotAcceptable() {
        var uuid = UUID.randomUUID();
        var input = randomPatchMeetingInput();

        Mockito.when(meetingService.patchMeetingV2(Mockito.eq(uuid), Mockito.any())).thenThrow(new NotAcceptableExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotAcceptableException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidPatch(uuid, input));
        assertNotNull(expectedException);
        assertEquals(406, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).patchMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertPatchMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPut() {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingInput();

        var meeting = randomMeeting();
        Mockito.when(meetingService.updateMeetingV2(Mockito.eq(uuid), Mockito.any())).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsUuidPut(uuid, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertMeeting(meeting, result.getBody());

        Mockito.verify(meetingService).updateMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertUpdateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPutOnlyRequiredValues() {
        var uuid = UUID.randomUUID();
        var input = new UpdateMeeting().subject(randomString()).startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now());

        var meeting = randomMeeting();
        Mockito.when(meetingService.updateMeetingV2(Mockito.eq(uuid), Mockito.any())).thenReturn(meeting);

        var result = videoMeetingsControllerV2.v2MeetingsUuidPut(uuid, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertMeeting(meeting, result.getBody());

        Mockito.verify(meetingService).updateMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertUpdateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPutResourceNotFound() {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingInput();

        Mockito.when(meetingService.updateMeetingV2(Mockito.eq(uuid), Mockito.any())).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidPut(uuid, input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(meetingService).updateMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertUpdateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPutPermissionDenied() {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingInput();

        Mockito.when(meetingService.updateMeetingV2(Mockito.eq(uuid), Mockito.any())).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidPut(uuid, input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(meetingService).updateMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertUpdateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPutNotAcceptable() {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingInput();

        Mockito.when(meetingService.updateMeetingV2(Mockito.eq(uuid), Mockito.any())).thenThrow(new NotAcceptableExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotAcceptableException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidPut(uuid, input));
        assertNotNull(expectedException);
        assertEquals(406, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).updateMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertUpdateMeeting(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2MeetingsUuidPutNotValidData() {
        var uuid = UUID.randomUUID();
        var input = randomUpdateMeetingInput();

        Mockito.when(meetingService.updateMeetingV2(Mockito.eq(uuid), Mockito.any())).thenThrow(new NotValidDataExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotValidDataException.class, () -> videoMeetingsControllerV2.v2MeetingsUuidPut(uuid, input));
        assertNotNull(expectedException);
        assertEquals(400, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(meetingService).updateMeetingV2(Mockito.eq(uuid), Mockito.argThat(x -> assertUpdateMeeting(x, input)));
        verifyNoMoreInteractions();
    }
}
