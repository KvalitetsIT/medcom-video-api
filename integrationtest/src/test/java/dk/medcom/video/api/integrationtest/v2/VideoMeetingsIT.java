package dk.medcom.video.api.integrationtest.v2;

import dk.medcom.video.api.integrationtest.AbstractIntegrationTest;
import dk.medcom.video.api.integrationtest.v2.helper.HeaderBuilder;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.UriBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.VideoMeetingsV2Api;
import org.openapitools.client.api.VideoSchedulingInformationV2Api;
import org.openapitools.client.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class VideoMeetingsIT extends AbstractIntegrationTest {

    private final VideoMeetingsV2Api videoMeetingsV2Api;
    private final VideoMeetingsV2Api videoMeetingsV2ApiNoHeader;
    private final VideoMeetingsV2Api videoMeetingsV2ApiInvalidJwt;
    private final VideoMeetingsV2Api videoMeetingsV2ApiNoRoleAtt;
    private final VideoMeetingsV2Api videoMeetingsV2ApiOnlyProvisioner;
    private final String allRoleAttToken = HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl());

    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2Api;

    VideoMeetingsIT() {
        var apiClient = new ApiClient();
        apiClient.addDefaultHeader("Authorization", "Bearer " + allRoleAttToken);
        apiClient.setBasePath(getApiBasePath());

        videoMeetingsV2Api = new VideoMeetingsV2Api(apiClient);
        videoSchedulingInformationV2Api = new VideoSchedulingInformationV2Api(apiClient);

        var apiClientNoHeader = new ApiClient();
        apiClientNoHeader.setBasePath(getApiBasePath());
        videoMeetingsV2ApiNoHeader = new VideoMeetingsV2Api(apiClientNoHeader);

        var apiClientInvalidJwt = new ApiClient();
        apiClientInvalidJwt.setBasePath(getApiBasePath());
        apiClientInvalidJwt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getInvalidJwt());
        videoMeetingsV2ApiInvalidJwt = new VideoMeetingsV2Api(apiClientInvalidJwt);

        var apiClientNoRoleAtt = new ApiClient();
        apiClientNoRoleAtt.setBasePath(getApiBasePath());
        apiClientNoRoleAtt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl()));
        videoMeetingsV2ApiNoRoleAtt = new VideoMeetingsV2Api(apiClientNoRoleAtt);

        var apiClientOnlyProvisionerRoleAtt = new ApiClient();
        apiClientOnlyProvisionerRoleAtt.setBasePath(getApiBasePath());
        apiClientOnlyProvisionerRoleAtt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtOnlyProvisioner(getKeycloakUrl()));
        videoMeetingsV2ApiOnlyProvisioner = new VideoMeetingsV2Api(apiClientOnlyProvisionerRoleAtt);
    }

// ------ JWT errors -------

    @Test
    void errorIfNoJwtToken_v2MeetingsFindByUriWithDomainGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoHeader.v2MeetingsFindByUriWithDomainGet(UUID.randomUUID().toString()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_v2MeetingsFindByUriWithDomainGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoRoleAtt.v2MeetingsFindByUriWithDomainGet(UUID.randomUUID().toString()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_v2MeetingsFindByUriWithDomainGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiInvalidJwt.v2MeetingsFindByUriWithDomainGet(UUID.randomUUID().toString()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_v2MeetingsFindByUriWithoutDomainGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoHeader.v2MeetingsFindByUriWithoutDomainGet(UUID.randomUUID().toString()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_v2MeetingsFindByUriWithoutDomainGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoRoleAtt.v2MeetingsFindByUriWithoutDomainGet(UUID.randomUUID().toString()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_v2MeetingsFindByUriWithoutDomainGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiInvalidJwt.v2MeetingsFindByUriWithoutDomainGet(UUID.randomUUID().toString()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_v2MeetingsGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoHeader.v2MeetingsGet(OffsetDateTime.now(), OffsetDateTime.now(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_v2MeetingsGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoRoleAtt.v2MeetingsGet(OffsetDateTime.now(), OffsetDateTime.now(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_v2MeetingsGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiInvalidJwt.v2MeetingsGet(OffsetDateTime.now(), OffsetDateTime.now(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_v2MeetingsPost() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoHeader.v2MeetingsPost(randomCreateMeeting()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_v2MeetingsPost() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoRoleAtt.v2MeetingsPost(randomCreateMeeting()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_v2MeetingsPost() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiInvalidJwt.v2MeetingsPost(randomCreateMeeting()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfOnlyProvisionerRoleAtt_v2MeetingsPost() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiOnlyProvisioner.v2MeetingsPost(randomCreateMeeting()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_v2MeetingsUuidDelete() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoHeader.v2MeetingsUuidDelete(meeting301Uuid()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_v2MeetingsUuidDelete() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoRoleAtt.v2MeetingsUuidDelete(meeting301Uuid()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_v2MeetingsUuidDelete() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiInvalidJwt.v2MeetingsUuidDelete(meeting301Uuid()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfOnlyProvisionerRoleAtt_v2MeetingsUuidDelete() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiOnlyProvisioner.v2MeetingsUuidDelete(meeting301Uuid()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_v2MeetingsUuidGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoHeader.v2MeetingsUuidGet(meeting301Uuid()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_v2MeetingsUuidGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoRoleAtt.v2MeetingsUuidGet(meeting301Uuid()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_v2MeetingsUuidGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiInvalidJwt.v2MeetingsUuidGet(meeting301Uuid()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_v2MeetingsUuidPatch() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoHeader.v2MeetingsUuidPatch(meeting301Uuid(), randomPatchMeeting()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_v2MeetingsUuidPatch() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoRoleAtt.v2MeetingsUuidPatch(meeting301Uuid(), randomPatchMeeting()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_v2MeetingsUuidPatch() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiInvalidJwt.v2MeetingsUuidPatch(meeting301Uuid(), randomPatchMeeting()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfOnlyProvisionerRoleAtt_v2MeetingsUuidPatch() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiOnlyProvisioner.v2MeetingsUuidPatch(meeting301Uuid(), randomPatchMeeting()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    void errorIfNoJwtToken_v2MeetingsUuidPut() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoHeader.v2MeetingsUuidPut(meeting301Uuid(), randomUpdateMeeting()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_v2MeetingsUuidPut() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiNoRoleAtt.v2MeetingsUuidPut(meeting301Uuid(), randomUpdateMeeting()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_v2MeetingsUuidPut() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiInvalidJwt.v2MeetingsUuidPut(meeting301Uuid(), randomUpdateMeeting()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfOnlyProvisionerRoleAtt_v2MeetingsUuidPut() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2ApiOnlyProvisioner.v2MeetingsUuidPut(meeting301Uuid(), randomUpdateMeeting()));
        assertEquals(403, expectedException.getCode());
    }


// -------- No JWT errors ----------

    @Test
    void testV2MeetingsFindByUriWithDomainGet() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsFindByUriWithDomainGetWithHttpInfo("4005@test.dk");
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertEquals("Get by uri 4005", meetingResult.getSubject());
        assertEquals("Get this meeting by uri 4005", meetingResult.getDescription());
    }

    @Test
    void testV2MeetingsPostThenV2MeetingsFindByUriWithDomain() throws ApiException {
        var createMeeting = randomCreateMeeting();
        var createdMeeting = videoMeetingsV2Api.v2MeetingsPost(createMeeting);
        var schedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoUuidGet(createdMeeting.getUuid());
        var result = videoMeetingsV2Api.v2MeetingsFindByUriWithDomainGet(schedulingInfo.getUriWithDomain());

        assertNotNull(result);
        assertEquals(createdMeeting.getUuid(), result.getUuid());
        assertEquals("custom.portal/guest", schedulingInfo.getCustomPortalGuest());
        assertEquals("custom.portal/host", schedulingInfo.getCustomPortalHost());
        assertEquals("return-url", schedulingInfo.getReturnUrl());
    }

    @Test
    void testV2MeetingsFindByUriWithoutDomainGet() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsFindByUriWithoutDomainGetWithHttpInfo("4005");
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertEquals("Get by uri 4005", meetingResult.getSubject());
        assertEquals("Get this meeting by uri 4005", meetingResult.getDescription());
    }

    @Test
    void testV2MeetingsPostThenV2MeetingsFindByUriWithoutDomainGet() throws ApiException {
        var createMeeting = randomCreateMeeting();
        var createdMeeting = videoMeetingsV2Api.v2MeetingsPost(createMeeting);
        var schedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoUuidGet(createdMeeting.getUuid());
        var result = videoMeetingsV2Api.v2MeetingsFindByUriWithoutDomainGet(schedulingInfo.getUriWithoutDomain());

        assertNotNull(result);
        assertEquals(createdMeeting.getUuid(), result.getUuid());
        assertEquals("custom.portal/guest", schedulingInfo.getCustomPortalGuest());
        assertEquals("custom.portal/host", schedulingInfo.getCustomPortalHost());
        assertEquals("return-url", schedulingInfo.getReturnUrl());
    }

    @Test
    void testV2MeetingsGetByStartTime() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsGetWithHttpInfo(
                OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC),
                null, null, null, null, null, null);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingsResult = result.getData();
        assertFalse(meetingsResult.isEmpty());
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meeting301Uuid().toString())));
    }

    @Test
    void testV2MeetingsGetBySubject() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsGetWithHttpInfo(
                null, null,null, "Get this meeting", null, null, null, null);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingsResult = result.getData();
        assertFalse(meetingsResult.isEmpty());
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meeting301Uuid().toString())));
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meeting302Uuid().toString())));

        assertFalse(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(otherOrgMeetingUuid().toString())));
    }

    @Test
    void testV2MeetingsGetByOrganizedBy() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsGetWithHttpInfo(
                null, null,null, null, "in-user-org@user102.dk", null, null, null);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingsResult = result.getData();
        assertFalse(meetingsResult.isEmpty());
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meeting301Uuid().toString())));
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meeting302Uuid().toString())));
    }

    @Test
    void testV2MeetingsGetBySearch() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsGetWithHttpInfo(
                null, null,null, null, null, "Mødebeskrivelse", null, null);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingsResult = result.getData();
        assertFalse(meetingsResult.isEmpty());
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meetingMicMuted304Uuid().toString())));
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meetingMicNullUuid().toString())));
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(provisionedOkMeetingUuid().toString())));

        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meeting301Uuid().toString())));
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meeting302Uuid().toString())));

        assertFalse(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meetingUri4005Uuid().toString())));
        assertFalse(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(otherOrgMeetingUuid().toString())));
    }

    @Test
    void testV2MeetingsGetByLabel() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsGetWithHttpInfo(
                null, null,null, null, null, null, "this is a label", null);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingsResult = result.getData();
        assertFalse(meetingsResult.isEmpty());
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meeting301Uuid().toString())));
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meeting302Uuid().toString())));
    }

    @Test
    void testV2MeetingsGetByUriWithDomain() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsGetWithHttpInfo(
                null, null,null, null, null, null, null, "4005@test.dk");
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingsResult = result.getData();
        assertFalse(meetingsResult.isEmpty());
        assertTrue(meetingsResult.stream().anyMatch(x -> x.getUuid().toString().equals(meetingUri4005Uuid().toString())));
    }

    @Test
    void testV2MeetingsPostThenV2MeetingsGetByShortId() throws ApiException {
        var createMeeting = new CreateMeeting()
                .description("This is a description")
                .startTime(OffsetDateTime.now().plusHours(1))
                .endTime(OffsetDateTime.now().plusHours(2))
                .subject("This is a subject!");

        var resultPost = videoMeetingsV2Api.v2MeetingsPost(createMeeting);

        assertNotNull(resultPost.getUuid());

        var resultGet = videoMeetingsV2Api.v2MeetingsGet(null, null, resultPost.getShortId(), null, null, null, null, null);

        assertNotNull(resultGet);
        assertEquals(1, resultGet.size());
        assertEquals(resultPost.getUuid(), resultGet.getFirst().getUuid());
        assertEquals("https://video.link/" + resultGet.getFirst().getShortId(), resultGet.getFirst().getShortLink());
    }

    @Test
    void testV2MeetingsPost() throws ApiException {
        var input = randomCreateMeeting();
        var result = videoMeetingsV2Api.v2MeetingsPostWithHttpInfo(input);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertNotNull(meetingResult.getUuid());
        assertEquals(input.getSubject(), meetingResult.getSubject());
    }

    @Test
    void testV2MeetingsPostPoolMeeting() throws ApiException {
        var input = randomCreateMeeting();
        input.meetingType(MeetingType.POOL);

        var result = videoMeetingsV2Api.v2MeetingsPostWithHttpInfo(input);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertNotNull(meetingResult.getUuid());
    }

    @Test
    void testV2MeetingsPostOnlyRequiredCreateMeetingValues() throws ApiException {
        var input = new CreateMeeting()
                .subject(UUID.randomUUID().toString())
                .startTime(OffsetDateTime.now().plusHours(1))
                .endTime(OffsetDateTime.now().plusHours(2));

        var result = videoMeetingsV2Api.v2MeetingsPostWithHttpInfo(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var meetingResult = result.getData();
        assertEquals(input.getSubject(), meetingResult.getSubject());

        assertNotNull(meetingResult.getUuid());
        assertNotNull(meetingResult.getOrganizedBy());
        assertEquals("user-org-pool", meetingResult.getOrganizedBy().getOrganisationId());
        assertEquals("eva@klak.dk", meetingResult.getOrganizedBy().getEmail());
        assertNotNull(meetingResult.getCreatedBy());
        assertEquals("user-org-pool", meetingResult.getCreatedBy().getOrganisationId());
        assertEquals("eva@klak.dk", meetingResult.getCreatedBy().getEmail());
        assertNotNull(meetingResult.getUpdatedBy());
        assertNull(meetingResult.getUpdatedBy().getOrganisationId());
        assertNull(meetingResult.getUpdatedBy().getEmail());
        assertNull(meetingResult.getDescription());
        assertNull(meetingResult.getProjectCode());
        assertNotNull(meetingResult.getCreatedTime());
        assertNull(meetingResult.getUpdatedTime());
        assertNotNull(meetingResult.getShortId());
        assertEquals("https://video.link/" + meetingResult.getShortId(), meetingResult.getShortLink());
        assertEquals(meetingResult.getShortLink(), meetingResult.getShortlink());
        assertNull(meetingResult.getExternalId());
        assertNotNull(meetingResult.getLabels());
        assertTrue(meetingResult.getLabels().isEmpty());
        assertNotNull(meetingResult.getAdditionalInformation());
        assertTrue(meetingResult.getAdditionalInformation().isEmpty());

        // Default values
        assertEquals(GuestMicrophone.ON, meetingResult.getGuestMicrophone());

        // From scheduling template 201 (default in user-org-pool)
        assertNotNull(meetingResult.getGuestPinRequired());
        assertFalse(meetingResult.getGuestPinRequired());
    }

    @Test
    void testV2MeetingsPostWhereMicIsMuted() throws ApiException {
        var input = randomCreateMeeting();
        input.setGuestMicrophone(org.openapitools.client.model.GuestMicrophone.MUTED);

        var result = videoMeetingsV2Api.v2MeetingsPostWithHttpInfo(input);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertNotNull(meetingResult);
        assertEquals(input.getExternalId(), meetingResult.getExternalId());
        assertEquals(org.openapitools.client.model.GuestMicrophone.MUTED, meetingResult.getGuestMicrophone());
    }

    @Test
    void testV2MeetingsPostWhereGuestPinRequiredSetFalse() throws ApiException {
        var input = randomCreateMeeting();
        input.setGuestPinRequired(false);

        var result = videoMeetingsV2Api.v2MeetingsPostWithHttpInfo(input);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertEquals(input.getExternalId(), meetingResult.getExternalId());
        assertNotNull(meetingResult.getGuestPinRequired());
        assertFalse(meetingResult.getGuestPinRequired());
    }

    @Test
    void testV2MeetingsPostWhereGuestPinRequiredSetTrue() throws ApiException {
        var input = randomCreateMeeting();
        input.setGuestPinRequired(true);

        var result = videoMeetingsV2Api.v2MeetingsPostWithHttpInfo(input);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertEquals(input.getExternalId(), meetingResult.getExternalId());
        assertNotNull(meetingResult.getGuestPinRequired());
        assertTrue(meetingResult.getGuestPinRequired());
    }

    @Test
    void testV2MeetingsPostDuplicateOrganisationExternalId() {
        var createMeeting = randomCreateMeeting();
        createMeeting.setExternalId("external_id");
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2Api.v2MeetingsPost(createMeeting));
        assertEquals(400, expectedException.getCode());
        assertTrue(expectedException.getResponseBody().contains("\"detailed_error_code\":\"21\""));
        assertTrue(expectedException.getResponseBody().contains("\"detailed_error\":\"ExternalId not unique within organisation.\""));
    }

    @Test
    void testV2MeetingsUuidDelete() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsUuidDeleteWithHttpInfo(deleteMeetingUuid());
        assertEquals(204, result.getStatusCode());
    }

    @Test
    void testV2MeetingsUuidDeleteNotAcceptableException() {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2Api.v2MeetingsUuidDelete(provisionedOkMeetingUuid()));
        assertEquals(406, expectedException.getCode());
        assertTrue(expectedException.getResponseBody().contains("\"detailed_error_code\":\"13\""));
        assertTrue(expectedException.getResponseBody().contains("\"detailed_error\":\"Meeting must have status AWAITS_PROVISION (0) in order to be deleted\""));
    }

    @Test
    void testV2MeetingsUuidGet() throws ApiException {
        var meetingUuid = meeting301Uuid();
        var result = videoMeetingsV2Api.v2MeetingsUuidGetWithHttpInfo(meetingUuid);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertEquals(meetingUuid, meetingResult.getUuid());
        assertEquals("Get this meeting", meetingResult.getSubject());
        assertEquals(12, meetingResult.getShortId().length());
        assertEquals("https://video.link/" + meetingResult.getShortId(), meetingResult.getShortLink());
        assertEquals("external_id", meetingResult.getExternalId());
    }

    @Test
    void testV2MeetingsUuidGetCanNotReadOtherOrganisation()  {
        var expectedException = assertThrows(ApiException.class, () -> videoMeetingsV2Api.v2MeetingsUuidGet(otherOrgMeetingUuid()));
        assertEquals(403, expectedException.getCode());
        assertTrue(expectedException.getMessage().contains("\"error\":\"Forbidden\""));
    }

    @Test
    void testV2MeetingsUuidGetWhereGuestMicIsMuted() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsUuidGet(meetingMicMuted304Uuid());

        assertNotNull(result);
        assertEquals("external_id_1", result.getExternalId());
        assertEquals(GuestMicrophone.MUTED, result.getGuestMicrophone());
    }

    @Test
    void testV2MeetingsUuidGetWhereGuestMicIsNull() throws ApiException {
        var result = videoMeetingsV2Api.v2MeetingsUuidGet(meetingMicNullUuid());

        assertNotNull(result);
        assertEquals("external_id_2", result.getExternalId());
        assertNull(result.getGuestMicrophone());
    }

    @Test
    void testV2MeetingsUuidPatch() throws ApiException {
        var input = randomPatchMeeting();
        var meetingUuid = patchMeetingUuid();
        var result = videoMeetingsV2Api.v2MeetingsUuidPatchWithHttpInfo(meetingUuid, input);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertEquals(meetingUuid, meetingResult.getUuid());
        assertEquals(input.getSubject(), meetingResult.getSubject());
    }

    @Test
    void testV2MeetingsUuidPatchTwiceWithSchedulingInfo() throws ApiException {
        // POST
        var inputPost = new CreateMeeting()
                .description("This is a description")
                .startTime(OffsetDateTime.now().plusHours(1))
                .endTime(OffsetDateTime.now().plusHours(2))
                .subject("This is a subject!")
                .guestMicrophone(GuestMicrophone.MUTED)
                .additionalInformation(List.of(new AdditionalInformationType().key("key").value("value")));

        var resultPost = videoMeetingsV2Api.v2MeetingsPost(inputPost);
        assertNotNull(resultPost.getUuid());

        var originalSchedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoUuidGet(resultPost.getUuid());

        // First PATCH
        var inputFirstPatch = new PatchMeeting()
                .description(randomString())
                .guestPinRequired(true)
                .additionalInformation(randomAdditionalInformation());
        var resultFirstPatch = videoMeetingsV2Api.v2MeetingsUuidPatch(resultPost.getUuid(), inputFirstPatch);
        var updatedSchedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoUuidGet(resultPost.getUuid());

        assertNotNull(resultFirstPatch);

        var resultGet = videoMeetingsV2Api.v2MeetingsUuidGet(resultPost.getUuid());

        assertNotNull(resultGet);
        assertNotEquals(inputPost.getDescription(), resultGet.getDescription());
        assertNotEquals(inputFirstPatch.getEndTime(), resultGet.getEndTime());
        assertEquals(org.openapitools.client.model.GuestMicrophone.MUTED, resultGet.getGuestMicrophone());
        assertEquals(true, resultGet.getGuestPinRequired());
        assertNotNull(resultGet.getAdditionalInformation());
        assertEquals(2, resultGet.getAdditionalInformation().size());
        assertTrue(resultGet.getAdditionalInformation().containsAll(inputFirstPatch.getAdditionalInformation()));
        assertEquals(originalSchedulingInfo.getHostPin(), updatedSchedulingInfo.getHostPin());
        assertEquals(originalSchedulingInfo.getGuestPin(), updatedSchedulingInfo.getGuestPin());

        // Second PATCH
        var inputSecondPatch = new PatchMeeting()
                .hostPin(4321)
                .guestPin(1234);
        var resultSecondPatch = videoMeetingsV2Api.v2MeetingsUuidPatch(resultPost.getUuid(), inputSecondPatch);

        assertNotNull(resultSecondPatch);
        assertNotNull(resultSecondPatch.getAdditionalInformation());
        assertEquals(2, resultSecondPatch.getAdditionalInformation().size());

        var resultSecondGet = videoMeetingsV2Api.v2MeetingsUuidGet(resultPost.getUuid());
        var updatedSchedulingInfoTwo = videoSchedulingInformationV2Api.v2SchedulingInfoUuidGet(resultPost.getUuid());

        assertNotNull(resultSecondGet);
        assertEquals(inputSecondPatch.getHostPin(), updatedSchedulingInfoTwo.getHostPin());
        assertEquals(inputSecondPatch.getGuestPin(), updatedSchedulingInfoTwo.getGuestPin());
        assertNotNull(resultSecondGet.getAdditionalInformation());
        assertEquals(2, resultSecondGet.getAdditionalInformation().size());
    }

    @Test
    void testV2MeetingsUuidPut() throws ApiException {
        var input = randomUpdateMeeting();
        var meetingUuid = updateMeetingUuid();
        var result = videoMeetingsV2Api.v2MeetingsUuidPutWithHttpInfo(meetingUuid, input);
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getData());

        var meetingResult = result.getData();
        assertEquals(meetingUuid, meetingResult.getUuid());
        assertEquals(input.getSubject(), meetingResult.getSubject());
    }

    @Test
    void testV2MeetingsPostThenV2MeetingsUuidPutThenV2MeetingsUuidGet() throws ApiException {
        var inputPost = randomCreateMeeting();
        var labels = List.of("Label One", "Label Two");
        inputPost.setLabels(labels);
        inputPost.setUriWithoutDomain("12345");
        inputPost.setGuestPin(4321);
        inputPost.setHostPin(1234);
        inputPost.setAdditionalInformation(randomAdditionalInformation());

        var resultPost = videoMeetingsV2Api.v2MeetingsPost(inputPost);
        assertNotNull(resultPost);
        assertEquals(inputPost.getExternalId(), resultPost.getExternalId());
        assertNotNull(resultPost.getLabels());
        assertEquals(2, resultPost.getLabels().size());
        assertTrue(inputPost.getLabels().containsAll(resultPost.getLabels()));
        assertNotNull(resultPost.getAdditionalInformation());
        assertTrue(inputPost.getAdditionalInformation().containsAll(resultPost.getAdditionalInformation()));

        var resultSchedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoUuidGet(resultPost.getUuid());
        assertNotNull(resultSchedulingInfo);
        assertEquals(inputPost.getUriWithoutDomain(), resultSchedulingInfo.getUriWithoutDomain());
        assertTrue(resultSchedulingInfo.getUriWithDomain().startsWith(inputPost.getUriWithoutDomain()));
        assertEquals(inputPost.getHostPin(), resultSchedulingInfo.getHostPin());
        assertEquals(inputPost.getGuestPin(), resultSchedulingInfo.getGuestPin());

        var inputPut = new UpdateMeeting()
                .subject("SUBJECT")
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plusHours(1))
                .labels(List.of("Another Label"));

        var resultPut = videoMeetingsV2Api.v2MeetingsUuidPut(resultPost.getUuid(), inputPut);
        assertNotNull(resultPut);
        assertEquals(inputPost.getExternalId(), resultPut.getExternalId());
        assertNotNull(resultPut.getLabels());
        assertEquals(1, resultPut.getLabels().size());
        assertTrue(inputPut.getLabels().containsAll(resultPut.getLabels()));
        assertNotNull(resultPut.getAdditionalInformation());
        assertTrue(resultPut.getAdditionalInformation().isEmpty());

        var resultGet = videoMeetingsV2Api.v2MeetingsUuidGet(resultPost.getUuid());
        assertNotNull(resultGet);
        assertEquals(inputPost.getExternalId(), resultGet.getExternalId());
        assertNotNull(resultGet.getLabels());
        assertEquals(1, resultGet.getLabels().size());
        assertTrue(inputPut.getLabels().containsAll(resultGet.getLabels()));
        assertNotNull(resultGet.getAdditionalInformation());
        assertTrue(resultGet.getAdditionalInformation().isEmpty());
    }

    @Test
    void testV2TimestampFormat() throws JSONException {
        // POST
        var inputPost = """
                {
                  "subject": "Will timestamp format correct.",
                  "startTime": "2025-11-02T15:00:00.123456+02:00",
                  "endTime": "2025-11-02T15:00:00.987654+02:00"
                }""";

        String postResult;
        try(var client = ClientBuilder.newClient()) {
            postResult = client.target(UriBuilder.fromPath(getApiBasePath()))
                    .path("v2")
                    .path("meetings")
                    .request()
                    .header("Authorization", "Bearer " + allRoleAttToken)
                    .post(Entity.json(inputPost), String.class);
        }
        var postResultJson = new JSONObject(postResult);

        var runInDocker = Boolean.getBoolean("runInDocker");
        if (runInDocker) {
            assertTrue(postResult.contains("\"startTime\":\"2025-11-02T13:00:00Z\""));
            assertTrue(postResult.contains("\"endTime\":\"2025-11-02T13:00:00Z\""));
        } else {
            assertTrue(postResult.contains("\"startTime\":\"2025-11-02T14:00:00+01:00\""));
            assertTrue(postResult.contains("\"endTime\":\"2025-11-02T14:00:00+01:00\""));
        }
        assertFalse(postResult.contains("00:00.123456"));
        assertFalse(postResult.contains("00:00.987654"));
        assertThat(postResultJson.getString("createdTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\+01:00|\\+02:00|Z)$");

        // PUT
        var inputPut = """
                {
                  "subject": "Will timestamp format correct.",
                  "startTime": "2025-10-02T15:00:00.123456+02:00",
                  "endTime": "2025-10-02T17:00:00.987654+02:00"
                }""";

        String putResult;
        try(var client = ClientBuilder.newClient()) {
            putResult = client.target(UriBuilder.fromPath(getApiBasePath()))
                    .path("v2")
                    .path("meetings")
                    .path(postResultJson.getString("uuid"))
                    .request()
                    .header("Authorization", "Bearer " + allRoleAttToken)
                    .put(Entity.json(inputPut), String.class);
        }
        var putResultJson = new JSONObject(putResult);

        if (runInDocker) {
            assertTrue(putResult.contains("\"startTime\":\"2025-10-02T13:00:00Z\""));
            assertTrue(putResult.contains("\"endTime\":\"2025-10-02T15:00:00Z\""));
        } else {
            assertTrue(putResult.contains("\"startTime\":\"2025-10-02T15:00:00+02:00\""));
            assertTrue(putResult.contains("\"endTime\":\"2025-10-02T17:00:00+02:00\""));
        }
        assertFalse(putResult.contains("00:00.123456"));
        assertFalse(putResult.contains("00:00.987654"));
        assertThat(putResultJson.getString("createdTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\+01:00|\\+02:00|Z)$");
        assertThat(putResultJson.getString("updatedTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\+01:00|\\+02:00|Z)$");
    }


    //----------- From v1 -----------
    @Test
    void testCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/meetings", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void testCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/meetings", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testViewIsHealthy() throws SQLException {
        // Only test that we can select from the view and it contains data.
        try(var connection = DriverManager.getConnection(getJdbcUrl(), "videouser", "secret1234");
            var result = connection.createStatement().executeQuery("select * from view_pool_history")) {
            assertTrue(result.next());
        }
    }



    //-------- test data help ---------
    private UUID meeting301Uuid() {
        return UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc8");
    }

    private UUID meeting302Uuid() {
        return UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc9");
    }

    private UUID otherOrgMeetingUuid() {
        return UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc1");
    }

    private UUID meetingMicNullUuid() {
        return UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc3");
    }

    private UUID meetingMicMuted304Uuid() {
        return UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc2");
    }

    private UUID meetingUri4005Uuid() {
        return UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc4");
    }

    private UUID patchMeetingUuid() {
        return UUID.fromString("a5c13cfb-4d67-475a-a77a-a62af1647c85");
    }

    private UUID updateMeetingUuid() {
        return UUID.fromString("d811eda7-79f0-46c4-9a75-19687423de80");
    }

    private UUID deleteMeetingUuid() {
        return UUID.fromString("8787faaa-9716-48aa-bbeb-703c2c312f1a");
    }

    private UUID provisionedOkMeetingUuid() {
        return UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc5");
    }

    private static long count = 10000;

    private static CreateMeeting randomCreateMeeting() {
        return new CreateMeeting()
                .subject(randomString())
                .startTime(OffsetDateTime.now().minusSeconds(count++).truncatedTo(ChronoUnit.MILLIS))
                .endTime(OffsetDateTime.now().minusSeconds(count++).truncatedTo(ChronoUnit.MILLIS))
                .description(randomString())
                .projectCode(randomString())
                .organizedByEmail(randomString())
                .maxParticipants((int) count++)
                .endMeetingOnEndTime(randomBoolean())
                .schedulingTemplateId(count++)
                .meetingType(MeetingType.NORMAL)
                .uuid(UUID.randomUUID())
                .externalId(randomString())
                .guestMicrophone(GuestMicrophone.MUTED)
                .guestPinRequired(randomBoolean())
                .labels(List.of(randomString(), randomString()))
                .vmrType(VmrType.CONFERENCE)
                .hostView(ViewType.ONE_MAIN_SEVEN_PIPS)
                .guestView(ViewType.ONE_MAIN_SEVEN_PIPS)
                .vmrQuality(VmrQuality.HD)
                .enableOverlayText(randomBoolean())
                .guestsCanPresent(randomBoolean())
                .forcePresenterIntoMain(randomBoolean())
                .forceEncryption(randomBoolean())
                .muteAllGuests(randomBoolean())
                .uriWithoutDomain(randomString().replace("-", ""))
                .guestPin((int) count++)
                .hostPin((int) count++)
                .additionalInformation(randomAdditionalInformation());
    }

    private static UpdateMeeting randomUpdateMeeting() {
        return new UpdateMeeting()
                .subject(randomString())
                .startTime(OffsetDateTime.now().minusSeconds(count++).truncatedTo(ChronoUnit.MILLIS))
                .endTime(OffsetDateTime.now().minusSeconds(count++).truncatedTo(ChronoUnit.MILLIS))
                .description(randomString())
                .projectCode(randomString())
                .organizedByEmail(randomString())
                .labels(List.of(randomString(), randomString()))
                .additionalInformation(randomAdditionalInformation());
    }

    private static PatchMeeting randomPatchMeeting() {
        return new PatchMeeting()
                .subject(randomString())
                .startTime(OffsetDateTime.now().minusSeconds(count++).truncatedTo(ChronoUnit.MILLIS))
                .endTime(OffsetDateTime.now().minusSeconds(count++).truncatedTo(ChronoUnit.MILLIS))
                .description(randomString())
                .projectCode(randomString())
                .organizedByEmail(randomString())
                .labels(List.of(randomString(), randomString()))
                .guestMicrophone(GuestMicrophone.MUTED)
                .guestPinRequired(randomBoolean())
                .guestPin((int) count++)
                .hostPin((int) count++)
                .additionalInformation(randomAdditionalInformation());
    }

    private static List<AdditionalInformationType> randomAdditionalInformation() {
        return List.of(
                new AdditionalInformationType().key(randomString()).value(randomString()),
                new AdditionalInformationType().key(randomString()).value(randomString())
        );
    }

    private static String randomString() {
        return UUID.randomUUID().toString();
    }

    private static boolean randomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }
}
