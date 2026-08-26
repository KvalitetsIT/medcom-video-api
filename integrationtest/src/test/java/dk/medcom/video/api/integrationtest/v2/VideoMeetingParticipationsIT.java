package dk.medcom.video.api.integrationtest.v2;

import dk.medcom.video.api.integrationtest.AbstractIntegrationTest;
import dk.medcom.video.api.integrationtest.v2.helper.HeaderBuilder;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.VideoMeetingsV2Api;
import org.openapitools.client.api.VideoSchedulingInformationV2Api;
import org.openapitools.client.model.*;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoMeetingParticipationsIT extends AbstractIntegrationTest {

    private static long count = 20000;
    private final VideoMeetingsV2Api videoMeetingsV2Api;
    private final VideoMeetingsV2Api videoMeetingsV2ApiNoHeader;
    private final VideoMeetingsV2Api videoMeetingsV2ApiNoRoleAtt;
    private final VideoMeetingsV2Api videoMeetingsV2ApiExpiredJwt;
    private final VideoMeetingsV2Api videoMeetingsV2ApiInvalidIssuerJwt;
    private final VideoMeetingsV2Api videoMeetingsV2ApiTamperedJwt;
    private final VideoMeetingsV2Api videoMeetingsV2ApiMissingSignatureJwt;
    private final VideoMeetingsV2Api videoMeetingsV2ApiDifferentSignedJwt;
    private final String allRoleAttToken = HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl());

    VideoMeetingParticipationsIT() {
        var keycloakUrl = getKeycloakUrl();

        videoMeetingsV2Api = createClient(allRoleAttToken);
        videoMeetingsV2ApiNoHeader = createClient(null);
        videoMeetingsV2ApiNoRoleAtt = createClient(HeaderBuilder.getJwtNoRoleAtt(keycloakUrl));
        videoMeetingsV2ApiExpiredJwt = createClient(HeaderBuilder.getExpiredJwt(keycloakUrl));
        videoMeetingsV2ApiInvalidIssuerJwt = createClient(HeaderBuilder.getInvalidIssuerJwt());
        videoMeetingsV2ApiTamperedJwt = createClient(HeaderBuilder.getTamperedJwt(keycloakUrl));
        videoMeetingsV2ApiMissingSignatureJwt = createClient(HeaderBuilder.getMissingSignatureJwt(keycloakUrl));
        videoMeetingsV2ApiDifferentSignedJwt = createClient(HeaderBuilder.getDifferentSignedJwt(keycloakUrl));
    }

    private VideoMeetingsV2Api createClient(String token) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        if (token != null) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + token);
        }
        return new VideoMeetingsV2Api(apiClient);
    }

    // ---------- JWT errors ----------

    @Test
    void errorIfNoJwtToken_getMeetingParticipations() {
        assertStatus(401, () -> videoMeetingsV2ApiNoHeader.getMeetingParticipations(randomString(), null, null, null, null, null));
    }

    @Test
    void errorIfNoRoleAttInToken_getMeetingParticipations() {
        assertStatus(401, () -> videoMeetingsV2ApiNoRoleAtt.getMeetingParticipations(randomString(), null, null, null, null, null));
    }

    @Test
    void errorIfExpiredJwtToken_getMeetingParticipations() {
        assertStatus(401, () -> videoMeetingsV2ApiExpiredJwt.getMeetingParticipations(randomString(), null, null, null, null, null));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_getMeetingParticipations() {
        assertStatus(401, () -> videoMeetingsV2ApiInvalidIssuerJwt.getMeetingParticipations(randomString(), null, null, null, null, null));
    }

    @Test
    void errorIfTamperedJwtToken_getMeetingParticipations() {
        assertStatus(401, () -> videoMeetingsV2ApiTamperedJwt.getMeetingParticipations(randomString(), null, null, null, null, null));
    }

    @Test
    void errorIfMissingSignatureJwtToken_getMeetingParticipations() {
        assertStatus(401, () -> videoMeetingsV2ApiMissingSignatureJwt.getMeetingParticipations(randomString(), null, null, null, null, null));
    }

    @Test
    void errorIfDifferentSignedJwtToken_getMeetingParticipations() {
        assertStatus(401, () -> videoMeetingsV2ApiDifferentSignedJwt.getMeetingParticipations(randomString(), null, null, null, null, null));
    }

    // ---------- Functional tests ----------

    @Test
    void testGetMeetingParticipations() throws ApiException {
        var participantId = randomString();
        var createMeeting = randomCreateMeeting();
        var createdMeeting = videoMeetingsV2Api.v2MeetingsPost(createMeeting);

        var participant = new CreateParticipant()
                .role(ParticipantRole.HOST)
                .type(ParticipantType.USER)
                .participantId(participantId);
        videoMeetingsV2Api.v2MeetingsUuidParticipantsPost(createdMeeting.getUuid(), List.of(participant));

        var result = videoMeetingsV2Api.getMeetingParticipations(participantId, null, null, null, null, null);

        assertNotNull(result);
        assertNotNull(result.getMeetingParticipations());
        assertEquals(1, result.getMeetingParticipations().size());

        var participation = result.getMeetingParticipations().getFirst();
        assertEquals(createdMeeting.getUuid(), participation.getUuid());
        assertEquals(createMeeting.getSubject(), participation.getSubject());
        assertEquals(createMeeting.getDescription(), participation.getDescription());
        assertEquals(ParticipantRole.HOST, participation.getParticipantRole());
        assertNotNull(participation.getPin());
        assertNotNull(participation.getShortLink());
    }

    @Test
    void testGetMeetingParticipationsPinIsHostPinForHostRole() throws ApiException {
        var participantId = randomString();
        var createMeeting = randomCreateMeeting();
        var createdMeeting = videoMeetingsV2Api.v2MeetingsPost(createMeeting);

        var participant = new CreateParticipant().role(ParticipantRole.HOST).type(ParticipantType.USER).participantId(participantId);
        videoMeetingsV2Api.v2MeetingsUuidParticipantsPost(createdMeeting.getUuid(), List.of(participant));

        var schedulingInfoApi = createSchedulingInfoClient();
        var schedulingInfo = schedulingInfoApi.v2SchedulingInfoUuidGet(createdMeeting.getUuid());

        var result = videoMeetingsV2Api.getMeetingParticipations(participantId, null, null, null, null, null);
        var participation = result.getMeetingParticipations().getFirst();

        assertEquals(schedulingInfo.getHostPin().toString(), participation.getPin());
    }

    @Test
    void testGetMeetingParticipationsPinIsGuestPinForGuestRole() throws ApiException {
        var participantId = randomString();
        var createMeeting = randomCreateMeeting();
        createMeeting.setGuestPinRequired(true);
        createMeeting.setGuestPin(1234);
        var createdMeeting = videoMeetingsV2Api.v2MeetingsPost(createMeeting);

        var participant = new CreateParticipant().role(ParticipantRole.GUEST).type(ParticipantType.USER).participantId(participantId);
        videoMeetingsV2Api.v2MeetingsUuidParticipantsPost(createdMeeting.getUuid(), List.of(participant));

        var schedulingInfoApi = createSchedulingInfoClient();
        var schedulingInfo = schedulingInfoApi.v2SchedulingInfoUuidGet(createdMeeting.getUuid());

        var result = videoMeetingsV2Api.getMeetingParticipations(participantId, null, null, null, null, null);
        var participation = result.getMeetingParticipations().getFirst();

        assertNotNull(schedulingInfo.getGuestPin());
        assertEquals(schedulingInfo.getGuestPin().toString(), participation.getPin());
    }

    @Test
    void testGetMeetingParticipationsFilteredBySubject() throws ApiException {
        var participantId = randomString();
        var createMeeting1 = randomCreateMeeting();
        var createMeeting2 = randomCreateMeeting();
        var createdMeeting1 = videoMeetingsV2Api.v2MeetingsPost(createMeeting1);
        var createdMeeting2 = videoMeetingsV2Api.v2MeetingsPost(createMeeting2);

        var participant = new CreateParticipant().role(ParticipantRole.GUEST).type(ParticipantType.USER).participantId(participantId);
        videoMeetingsV2Api.v2MeetingsUuidParticipantsPost(createdMeeting1.getUuid(), List.of(participant));
        videoMeetingsV2Api.v2MeetingsUuidParticipantsPost(createdMeeting2.getUuid(), List.of(participant));

        var result = videoMeetingsV2Api.getMeetingParticipations(participantId, null, null, createMeeting1.getSubject(), null, null);

        assertEquals(1, result.getMeetingParticipations().size());
        assertEquals(createdMeeting1.getUuid(), result.getMeetingParticipations().getFirst().getUuid());
    }

    @Test
    void testGetMeetingParticipationsFilteredByOrganizedBy() throws ApiException {
        var participantId = randomString();
        var createMeeting = randomCreateMeeting();
        var createdMeeting = videoMeetingsV2Api.v2MeetingsPost(createMeeting);

        var participant = new CreateParticipant().role(ParticipantRole.GUEST).type(ParticipantType.USER).participantId(participantId);
        videoMeetingsV2Api.v2MeetingsUuidParticipantsPost(createdMeeting.getUuid(), List.of(participant));

        var result = videoMeetingsV2Api.getMeetingParticipations(participantId, null, null, null, createMeeting.getOrganizedByEmail(), null);

        assertEquals(1, result.getMeetingParticipations().size());
    }

    @Test
    void testGetMeetingParticipationsFilteredByLabel() throws ApiException {
        var participantId = randomString();
        var createMeeting = randomCreateMeeting();
        var label = randomString();
        createMeeting.setLabels(List.of(label));
        var createdMeeting = videoMeetingsV2Api.v2MeetingsPost(createMeeting);

        var participant = new CreateParticipant().role(ParticipantRole.GUEST).type(ParticipantType.USER).participantId(participantId);
        videoMeetingsV2Api.v2MeetingsUuidParticipantsPost(createdMeeting.getUuid(), List.of(participant));

        var result = videoMeetingsV2Api.getMeetingParticipations(participantId, null, null, null, null, label);

        assertEquals(1, result.getMeetingParticipations().size());
    }

    @Test
    void testGetMeetingParticipationsFilteredByStartTimeInterval() throws ApiException {
        var participantId = randomString();
        var createMeeting = randomCreateMeeting();
        var createdMeeting = videoMeetingsV2Api.v2MeetingsPost(createMeeting);

        var participant = new CreateParticipant().role(ParticipantRole.GUEST).type(ParticipantType.USER).participantId(participantId);
        videoMeetingsV2Api.v2MeetingsUuidParticipantsPost(createdMeeting.getUuid(), List.of(participant));

        var result = videoMeetingsV2Api.getMeetingParticipations(participantId,
                createMeeting.getStartTime().minusHours(1),
                createMeeting.getStartTime().plusHours(1),
                null, null, null);

        assertEquals(1, result.getMeetingParticipations().size());
    }

    @Test
    void testGetMeetingParticipationsNoMatchingParticipant() throws ApiException {
        var result = videoMeetingsV2Api.getMeetingParticipations(randomString(), null, null, null, null, null);

        assertNotNull(result);
        assertNotNull(result.getMeetingParticipations());
        assertTrue(result.getMeetingParticipations().isEmpty());
    }

    @Test
    void testGetMeetingParticipationsOnlyFromStartTimeGiven() {
        var expectedException = assertThrows(ApiException.class, () ->
                videoMeetingsV2Api.getMeetingParticipations(randomString(), OffsetDateTime.now(), null, null, null, null));
        assertEquals(400, expectedException.getCode());
        assertTrue(expectedException.getResponseBody().contains("\"detailed_error_code\":\"28\""));
    }

    @Test
    void testGetMeetingParticipationsOnlyToStartTimeGiven() {
        var expectedException = assertThrows(ApiException.class, () ->
                videoMeetingsV2Api.getMeetingParticipations(randomString(), null, OffsetDateTime.now(), null, null, null));
        assertEquals(400, expectedException.getCode());
        assertTrue(expectedException.getResponseBody().contains("\"detailed_error_code\":\"28\""));
    }

    private VideoSchedulingInformationV2Api createSchedulingInfoClient() {
        var apiClient = new ApiClient();
        apiClient.addDefaultHeader("Authorization", "Bearer " + allRoleAttToken);
        apiClient.setBasePath(getApiBasePath());
        return new VideoSchedulingInformationV2Api(apiClient);
    }

    private static CreateMeeting randomCreateMeeting() {
        return new CreateMeeting()
                .subject(randomString())
                .startTime(OffsetDateTime.now().plusHours(1).minusSeconds(count++).truncatedTo(ChronoUnit.MILLIS))
                .endTime(OffsetDateTime.now().plusHours(2).minusSeconds(count++).truncatedTo(ChronoUnit.MILLIS))
                .description(randomString())
                .organizedByEmail(randomString());
    }

    private static String randomString() {
        return UUID.randomUUID().toString();
    }
}