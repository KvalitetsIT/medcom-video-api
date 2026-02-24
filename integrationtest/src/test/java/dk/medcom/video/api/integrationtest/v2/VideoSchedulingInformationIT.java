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
import org.openapitools.client.api.VideoSchedulingInformationV2Api;
import org.openapitools.client.api.VideoMeetingsV2Api;
import org.openapitools.client.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class VideoSchedulingInformationIT extends AbstractIntegrationTest {

    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2Api;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiNoHeader;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiNoRoleAtt;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiNotAdmin;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiNotProvisionUser;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiExpiredJwt;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiInvalidIssuerJwt;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiTamperedJwt;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiMissingSignatureJwt;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiDifferentSignedJwt;
    private final VideoMeetingsV2Api videoMeetingsV2Api;
    private final String allRoleAttToken = HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl());

    VideoSchedulingInformationIT() {
        var keycloakUrl = getKeycloakUrl();

        videoSchedulingInformationV2Api = createClient(allRoleAttToken);
        videoSchedulingInformationV2ApiNoHeader = createClient(null);
        videoSchedulingInformationV2ApiNoRoleAtt = createClient(HeaderBuilder.getJwtNoRoleAtt(keycloakUrl));
        videoSchedulingInformationV2ApiNotAdmin = createClient(HeaderBuilder.getJwtNotAdmin(keycloakUrl));
        videoSchedulingInformationV2ApiNotProvisionUser = createClient(HeaderBuilder.getJwtNotProvisionUser(keycloakUrl));
        videoSchedulingInformationV2ApiExpiredJwt = createClient(HeaderBuilder.getExpiredJwt(keycloakUrl));
        videoSchedulingInformationV2ApiInvalidIssuerJwt = createClient(HeaderBuilder.getInvalidIssuerJwt());
        videoSchedulingInformationV2ApiTamperedJwt = createClient(HeaderBuilder.getTamperedJwt(keycloakUrl));
        videoSchedulingInformationV2ApiMissingSignatureJwt = createClient(HeaderBuilder.getMissingSignatureJwt(keycloakUrl));
        videoSchedulingInformationV2ApiDifferentSignedJwt = createClient(HeaderBuilder.getDifferentSignedJwt(keycloakUrl));

        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + allRoleAttToken);
        videoMeetingsV2Api = new VideoMeetingsV2Api(apiClient);
    }

    private VideoSchedulingInformationV2Api createClient(String token) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        if (token != null) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + token);
        }
        return new VideoSchedulingInformationV2Api(apiClient);
    }

    //---------- JWT errors --------
    @Test
    void errorIfNoJwtToken_v2SchedulingInfoDeprovisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiNoHeader::v2SchedulingInfoDeprovisionGet);
    }

    @Test
    void errorIfNotProvisionUser_v2SchedulingInfoDeprovisionGet() {
        assertStatus(403, videoSchedulingInformationV2ApiNotProvisionUser::v2SchedulingInfoDeprovisionGet);
    }

    @Test
    void errorIfExpiredJwtToken_v2SchedulingInfoDeprovisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiExpiredJwt::v2SchedulingInfoDeprovisionGet);
    }

    @Test
    void errorIfInvalidIssuerJwtToken_v2SchedulingInfoDeprovisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiInvalidIssuerJwt::v2SchedulingInfoDeprovisionGet);
    }

    @Test
    void errorIfTamperedJwtToken_v2SchedulingInfoDeprovisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiTamperedJwt::v2SchedulingInfoDeprovisionGet);
    }

    @Test
    void errorIfMissingSignatureJwtToken_v2SchedulingInfoDeprovisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiMissingSignatureJwt::v2SchedulingInfoDeprovisionGet);
    }

    @Test
    void errorIfDifferentSignedJwtToken_v2SchedulingInfoDeprovisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiDifferentSignedJwt::v2SchedulingInfoDeprovisionGet);
    }

    @Test
    void errorIfNoJwtToken_v2SchedulingInfoGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoGet(OffsetDateTime.now(), OffsetDateTime.now(), ProvisionStatus.AWAITS_PROVISION));
    }

    @Test
    void errorIfNoRoleAttInToken_v2SchedulingInfoGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiNoRoleAtt.v2SchedulingInfoGet(
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                ProvisionStatus.AWAITS_PROVISION
        ));
    }

    @Test
    void errorIfExpiredJwtToken_v2SchedulingInfoGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiExpiredJwt.v2SchedulingInfoGet(
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                ProvisionStatus.AWAITS_PROVISION
        ));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_v2SchedulingInfoGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiInvalidIssuerJwt.v2SchedulingInfoGet(
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                ProvisionStatus.AWAITS_PROVISION
        ));
    }

    @Test
    void errorIfTamperedJwtToken_v2SchedulingInfoGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiTamperedJwt.v2SchedulingInfoGet(
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                ProvisionStatus.AWAITS_PROVISION
        ));
    }

    @Test
    void errorIfMissingSignatureJwtToken_v2SchedulingInfoGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiMissingSignatureJwt.v2SchedulingInfoGet(
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                ProvisionStatus.AWAITS_PROVISION
        ));
    }

    @Test
    void errorIfDifferentSignedJwtToken_v2SchedulingInfoGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiDifferentSignedJwt.v2SchedulingInfoGet(
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                ProvisionStatus.AWAITS_PROVISION
        ));
    }

    @Test
    void errorIfNoJwtToken_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        assertStatus(401, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoPost(input));
    }

    @Test
    void errorIfNotProvisionUser_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        assertStatus(403, () -> videoSchedulingInformationV2ApiNotProvisionUser.v2SchedulingInfoPost(input));
    }

    @Test
    void errorIfExpiredJwtToken_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        assertStatus(401, () -> videoSchedulingInformationV2ApiExpiredJwt.v2SchedulingInfoPost(input));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        assertStatus(401, () -> videoSchedulingInformationV2ApiInvalidIssuerJwt.v2SchedulingInfoPost(input));
    }

    @Test
    void errorIfTamperedJwtToken_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        assertStatus(401, () -> videoSchedulingInformationV2ApiTamperedJwt.v2SchedulingInfoPost(input));
    }

    @Test
    void errorIfMissingSignatureJwtToken_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        assertStatus(401, () -> videoSchedulingInformationV2ApiMissingSignatureJwt.v2SchedulingInfoPost(input));
    }

    @Test
    void errorIfDifferentSignedJwtToken_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        assertStatus(401, () -> videoSchedulingInformationV2ApiDifferentSignedJwt.v2SchedulingInfoPost(input));
    }

    @Test
    void errorIfNoJwtToken_v2SchedulingInfoProvisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiNoHeader::v2SchedulingInfoProvisionGet);
    }

    @Test
    void errorIfNotProvisionUser_v2SchedulingInfoProvisionGet() {
        assertStatus(403, videoSchedulingInformationV2ApiNotProvisionUser::v2SchedulingInfoProvisionGet);
    }

    @Test
    void errorIfExpiredJwtToken_v2SchedulingInfoProvisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiExpiredJwt::v2SchedulingInfoProvisionGet);
    }

    @Test
    void errorIfInvalidIssuerJwtToken_v2SchedulingInfoProvisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiInvalidIssuerJwt::v2SchedulingInfoProvisionGet);
    }

    @Test
    void errorIfTamperedJwtToken_v2SchedulingInfoProvisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiTamperedJwt::v2SchedulingInfoProvisionGet);
    }

    @Test
    void errorIfMissingSignatureJwtToken_v2SchedulingInfoProvisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiMissingSignatureJwt::v2SchedulingInfoProvisionGet);
    }

    @Test
    void errorIfDifferentSignedJwtToken_v2SchedulingInfoProvisionGet() {
        assertStatus(401, videoSchedulingInformationV2ApiDifferentSignedJwt::v2SchedulingInfoProvisionGet);
    }

    @Test
    void errorIfNoJwtToken_v2SchedulingInfoReserveGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoReserveGet(
                VmrType.LECTURE, ViewType.ONE_MAIN_SEVEN_PIPS, ViewType.ONE_MAIN_SEVEN_PIPS, VmrQuality.SD,
                true, false, true, false, true
        ));
    }

    @Test
    void errorIfNotAdmin_v2SchedulingInfoReserveGet() {
        assertStatus(403, () -> videoSchedulingInformationV2ApiNotAdmin.v2SchedulingInfoReserveGet(
                VmrType.LECTURE,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                VmrQuality.SD,
                true,
                false,
                true,
                false,
                true
        ));
    }

    @Test
    void errorIfExpiredJwtToken_v2SchedulingInfoReserveGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiExpiredJwt.v2SchedulingInfoReserveGet(
                VmrType.LECTURE,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                VmrQuality.SD,
                true,
                false,
                true,
                false,
                true
        ));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_v2SchedulingInfoReserveGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiInvalidIssuerJwt.v2SchedulingInfoReserveGet(
                VmrType.LECTURE,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                VmrQuality.SD,
                true,
                false,
                true,
                false,
                true
        ));
    }

    @Test
    void errorIfTamperedJwtToken_v2SchedulingInfoReserveGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiTamperedJwt.v2SchedulingInfoReserveGet(
                VmrType.LECTURE,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                VmrQuality.SD,
                true,
                false,
                true,
                false,
                true
        ));
    }

    @Test
    void errorIfMissingSignatureJwtToken_v2SchedulingInfoReserveGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiMissingSignatureJwt.v2SchedulingInfoReserveGet(
                VmrType.LECTURE,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                VmrQuality.SD,
                true,
                false,
                true,
                false,
                true
        ));
    }

    @Test
    void errorIfDifferentSignedJwtToken_v2SchedulingInfoReserveGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiDifferentSignedJwt.v2SchedulingInfoReserveGet(
                VmrType.LECTURE,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                ViewType.ONE_MAIN_SEVEN_PIPS,
                VmrQuality.SD,
                true,
                false,
                true,
                false,
                true
        ));
    }

    @Test
    void errorIfNoJwtToken_v2SchedulingInfoReserveUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
    }

    @Test
    void errorIfNotAdmin_v2SchedulingInfoReserveUuidGet() {
        assertStatus(403, () -> videoSchedulingInformationV2ApiNotAdmin.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
    }

    @Test
    void errorIfExpiredJwtToken_v2SchedulingInfoReserveUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiExpiredJwt.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_v2SchedulingInfoReserveUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiInvalidIssuerJwt.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
    }

    @Test
    void errorIfTamperedJwtToken_v2SchedulingInfoReserveUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiTamperedJwt.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
    }

    @Test
    void errorIfMissingSignatureJwtToken_v2SchedulingInfoReserveUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiMissingSignatureJwt.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
    }

    @Test
    void errorIfDifferentSignedJwtToken_v2SchedulingInfoReserveUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiDifferentSignedJwt.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
    }

    @Test
    void errorIfNoJwtToken_v2SchedulingInfoUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
    }

    @Test
    void errorIfNoRoleAttInToken_v2SchedulingInfoUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiNoRoleAtt.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
    }

    @Test
    void errorIfExpiredJwtToken_v2SchedulingInfoUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiExpiredJwt.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_v2SchedulingInfoUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiInvalidIssuerJwt.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
    }

    @Test
    void errorIfTamperedJwtToken_v2SchedulingInfoUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiTamperedJwt.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
    }

    @Test
    void errorIfMissingSignatureJwtToken_v2SchedulingInfoUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiMissingSignatureJwt.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
    }

    @Test
    void errorIfDifferentSignedJwtToken_v2SchedulingInfoUuidGet() {
        assertStatus(401, () -> videoSchedulingInformationV2ApiDifferentSignedJwt.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
    }

    @Test
    void errorIfNoJwtToken_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        assertStatus(401, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
    }

    @Test
    void errorIfNotProvisionUser_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        assertStatus(403, () -> videoSchedulingInformationV2ApiNotProvisionUser.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
    }

    @Test
    void errorIfExpiredJwtToken_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        assertStatus(401, () -> videoSchedulingInformationV2ApiExpiredJwt.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
    }

    @Test
    void errorIfInvalidIssuerJwtToken_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        assertStatus(401, () -> videoSchedulingInformationV2ApiInvalidIssuerJwt.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
    }

    @Test
    void errorIfTamperedJwtToken_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        assertStatus(401, () -> videoSchedulingInformationV2ApiTamperedJwt.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
    }

    @Test
    void errorIfMissingSignatureJwtToken_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        assertStatus(401, () -> videoSchedulingInformationV2ApiMissingSignatureJwt.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
    }

    @Test
    void errorIfDifferentSignedJwtToken_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        assertStatus(401, () -> videoSchedulingInformationV2ApiDifferentSignedJwt.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
    }

// ---------- No JWT errors --------

    @Test
    void testV2SchedulingInfoDeprovisionGet() throws ApiException {
        var result = videoSchedulingInformationV2Api.v2SchedulingInfoDeprovisionGetWithHttpInfo();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());

        var resultData = result.getData();
        assertFalse(resultData.isEmpty());
        assertFalse(resultData.stream().anyMatch(x -> x.getProvisionStatus().equals(ProvisionStatus.AWAITS_PROVISION)));
        assertTrue(resultData.stream().anyMatch(x -> x.getProvisionStatus().equals(ProvisionStatus.PROVISIONED_OK)));

        assertTrue(resultData.stream().anyMatch(x -> x.getUriWithDomain().equals("4001@test.dk")));
        assertTrue(resultData.stream().anyMatch(x -> x.getUriWithDomain().equals("4004@test.dk")));
        assertFalse(resultData.stream().anyMatch(x -> x.getUriWithDomain().equals("4002@test.dk")));
    }

    @Test
    void testV2SchedulingInfoGet() throws ApiException {
        var result = videoSchedulingInformationV2Api.v2SchedulingInfoGetWithHttpInfo(
                OffsetDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2025, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC),
                ProvisionStatus.AWAITS_PROVISION);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingInfosResult = result.getData();
        assertTrue(schedulingInfosResult.size() >= 2);

        assertTrue(schedulingInfosResult.stream().anyMatch(x -> x.getUriWithDomain().equals("4003@test.dk")));
        assertTrue(schedulingInfosResult.stream().anyMatch(x -> x.getUriWithDomain().equals("4005@test.dk")));
    }

    @Test
    void testV2SchedulingInfoPost() throws ApiException {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);

        var result = videoSchedulingInformationV2Api.v2SchedulingInfoPostWithHttpInfo(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingInfoResult = result.getData();
        assertNotNull(schedulingInfoResult.getUuid());
        assertNull(schedulingInfoResult.getMeetingDetails());
        assertTrue(schedulingInfoResult.getUriWithDomain().contains("@def-test.dk"));
        assertNotNull(schedulingInfoResult.getCreatedBy());
        assertEquals("user-org-pool", schedulingInfoResult.getCreatedBy().getOrganisationId());
        assertEquals("eva@klak.dk", schedulingInfoResult.getCreatedBy().getEmail());
        assertEquals("custom.portal/guest", schedulingInfoResult.getCustomPortalGuest());
        assertEquals("custom.portal/host", schedulingInfoResult.getCustomPortalHost());
        assertEquals("return-url", schedulingInfoResult.getReturnUrl());
    }

    @Test
    void testV2SchedulingInfoPostThenV2SchedulingInfoUuidGet() throws ApiException {
        var input = new CreateSchedulingInfo()
                .schedulingTemplateId(201L)
                .organizationId("user-org-pool");

        var resultPost = videoSchedulingInformationV2Api.v2SchedulingInfoPostWithHttpInfo(input);
        assertNotNull(resultPost);
        assertEquals(200, resultPost.getStatusCode());
        assertNotNull(resultPost);

        var createdSchedulingInfo = resultPost.getData();
        assertNotNull(createdSchedulingInfo.getUuid());
        assertNotNull(createdSchedulingInfo.getCreatedBy());
        assertEquals("user-org-pool", createdSchedulingInfo.getCreatedBy().getOrganisationId());
        assertEquals("eva@klak.dk", createdSchedulingInfo.getCreatedBy().getEmail());
        assertEquals("custom.portal/guest", createdSchedulingInfo.getCustomPortalGuest());
        assertEquals("custom.portal/host", createdSchedulingInfo.getCustomPortalHost());
        assertEquals("return-url", createdSchedulingInfo.getReturnUrl());

        var resultGet = videoSchedulingInformationV2Api.v2SchedulingInfoUuidGetWithHttpInfo(createdSchedulingInfo.getUuid());
        assertNotNull(resultGet);
        assertEquals(200, resultGet.getStatusCode());
        assertNotNull(resultGet.getData());

        var readSchedulingInfo = resultGet.getData();
        assertEquals(createdSchedulingInfo.getUuid(), readSchedulingInfo.getUuid());
        assertNotNull(createdSchedulingInfo.getCreatedBy());
        assertEquals("user-org-pool", createdSchedulingInfo.getCreatedBy().getOrganisationId());
        assertEquals("eva@klak.dk", createdSchedulingInfo.getCreatedBy().getEmail());
        assertEquals("custom.portal/guest", readSchedulingInfo.getCustomPortalGuest());
        assertEquals("custom.portal/host", readSchedulingInfo.getCustomPortalHost());
        assertEquals("return-url", readSchedulingInfo.getReturnUrl());
    }

    @Test
    void testV2SchedulingInfoProvisionGet() throws ApiException {
        var result = videoSchedulingInformationV2Api.v2SchedulingInfoProvisionGetWithHttpInfo();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());

        var resultData = result.getData();
        assertFalse(resultData.isEmpty());
        assertTrue(resultData.stream().anyMatch(x -> x.getProvisionStatus().equals(ProvisionStatus.AWAITS_PROVISION)));
        assertFalse(resultData.stream().anyMatch(x -> x.getProvisionStatus().equals(ProvisionStatus.PROVISIONED_OK)));

        assertTrue(resultData.stream().anyMatch(x -> x.getUriWithDomain().equals("4000@test.dk")));
        assertTrue(resultData.stream().anyMatch(x -> x.getUriWithDomain().equals("4007@test.dk")));
        assertFalse(resultData.stream().anyMatch(x -> x.getUriWithDomain().equals("4008@test.dk")));
    }

    @Test
    void testV2SchedulingInfoProvisionGetDefaultValues() throws ApiException {
        // Create meeting should trigger create scheduling info with AWAITS_PROVISION
        var inputMeeting = new CreateMeeting()
                .subject(randomString())
                .startTime(OffsetDateTime.now().plusMinutes(3))
                .endTime(OffsetDateTime.now().plusHours(1));
        inputMeeting.setGuestMicrophone(GuestMicrophone.MUTED);
        var createResponse = videoMeetingsV2Api.v2MeetingsPost(inputMeeting);
        assertNotNull(createResponse.getUuid());

        var res = videoSchedulingInformationV2Api.v2SchedulingInfoProvisionGetWithHttpInfo();
        assertNotNull(res);
        assertEquals(200, res.getStatusCode());
        assertNotNull(res.getData());

        assertTrue(res.getData().stream().anyMatch(x -> x.getUuid().toString().equals(createResponse.getUuid().toString())));
        var createdSchedulingInfo = res.getData().stream().filter(x -> x.getUuid().toString().equals(createResponse.getUuid().toString())).findFirst().orElseThrow();
        // Expects to be created with default values, i.e. values from default template of user-org-pool, scheduling template with id = 1
        assertEquals(VmrType.CONFERENCE, createdSchedulingInfo.getVmrType());
        assertEquals(ViewType.ONE_MAIN_ZERO_PIPS, createdSchedulingInfo.getHostView());
        assertEquals(ViewType.ONE_MAIN_TWENTYONE_PIPS, createdSchedulingInfo.getGuestView());
        assertEquals(VmrQuality.SD, createdSchedulingInfo.getVmrQuality());
        assertEquals(Boolean.TRUE, createdSchedulingInfo.getEnableOverlayText());
        assertEquals(Boolean.FALSE, createdSchedulingInfo.getGuestsCanPresent());
        assertEquals(Boolean.TRUE, createdSchedulingInfo.getForcePresenterIntoMain());
        assertEquals(Boolean.FALSE, createdSchedulingInfo.getForceEncryption());
        assertEquals(Boolean.TRUE, createdSchedulingInfo.getMuteAllGuests());
    }

    @Test
    void testV2SchedulingInfoReserveGet() throws ApiException {
        var result = videoSchedulingInformationV2Api.v2SchedulingInfoReserveGetWithHttpInfo(
                null, null, null, null,
                null, null, null, null, null);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingInfoResult = result.getData();
        assertNotNull(schedulingInfoResult.getReservationId());
        assertEquals(VmrType.CONFERENCE, schedulingInfoResult.getVmrType());
        assertEquals(ViewType.ONE_MAIN_SEVEN_PIPS, schedulingInfoResult.getHostView());
        assertEquals(ViewType.ONE_MAIN_SEVEN_PIPS, schedulingInfoResult.getGuestView());
        assertEquals(VmrQuality.HD, schedulingInfoResult.getVmrQuality());
        assertEquals(Boolean.TRUE, schedulingInfoResult.getEnableOverlayText());
        assertEquals(Boolean.TRUE, schedulingInfoResult.getGuestsCanPresent());
        assertEquals(Boolean.TRUE, schedulingInfoResult.getForcePresenterIntoMain());
        assertEquals(Boolean.FALSE, schedulingInfoResult.getForceEncryption());
        assertEquals(Boolean.FALSE, schedulingInfoResult.getMuteAllGuests());
    }

    @Test
    void testV2SchedulingInfoReserveGetThenV2SchedulingInfoReserveUuidGet() throws ApiException {
        var result = videoSchedulingInformationV2Api.v2SchedulingInfoReserveGetWithHttpInfo(
                VmrType.LECTURE, null, null, VmrQuality.FULLHD,
                null, null, null, null, null);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingInfoResult = result.getData();
        assertNotNull(schedulingInfoResult.getReservationId());
        assertEquals(VmrType.LECTURE, schedulingInfoResult.getVmrType());
        assertEquals(ViewType.ONE_MAIN_SEVEN_PIPS, schedulingInfoResult.getHostView());
        assertEquals(ViewType.ONE_MAIN_SEVEN_PIPS, schedulingInfoResult.getGuestView());
        assertEquals(VmrQuality.FULLHD, schedulingInfoResult.getVmrQuality());
        assertEquals(Boolean.TRUE, schedulingInfoResult.getEnableOverlayText());
        assertEquals(Boolean.TRUE, schedulingInfoResult.getGuestsCanPresent());
        assertEquals(Boolean.TRUE, schedulingInfoResult.getForcePresenterIntoMain());
        assertEquals(Boolean.FALSE, schedulingInfoResult.getForceEncryption());
        assertEquals(Boolean.FALSE, schedulingInfoResult.getMuteAllGuests());

        var reservationId = schedulingInfoResult.getReservationId();

        var resultFromUuid = videoSchedulingInformationV2Api.v2SchedulingInfoReserveUuidGet(schedulingInfoResult.getReservationId());
        assertNotNull(resultFromUuid);
        assertEquals(reservationId, resultFromUuid.getReservationId());
    }

    @Test
    void testV2SchedulingInfoReserveUuidGet() throws ApiException {
        var reservationId = schedulingInfo413ReservationUuid();
        var result = videoSchedulingInformationV2Api.v2SchedulingInfoReserveUuidGetWithHttpInfo(reservationId);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingInfoResult = result.getData();
        assertEquals(UUID.fromString("e96b8928-cd76-5bd1-937d-7d7cb77f27f7"), schedulingInfoResult.getUuid());
        assertEquals(reservationId, schedulingInfoResult.getReservationId());
        assertNull(schedulingInfoResult.getMeetingDetails());
    }

    @Test
    void testUseReservedSchedulingInfo() throws ApiException {
        var schedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoReserveGet(null, null, null, null, null, null, null, null, null);
        assertNotNull(schedulingInfo);
        var reservationId = schedulingInfo.getReservationId();

        var createMeeting = new CreateMeeting()
                .description("This is a description")
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plusHours(2))
                .subject("This is a subject!")
                .schedulingInfoReservationId(schedulingInfo.getReservationId());

        var result = videoMeetingsV2Api.v2MeetingsPost(createMeeting);
        assertNotNull(result);

        schedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoUuidGet(result.getUuid());
        assertNotNull(schedulingInfo);
        assertEquals(reservationId, schedulingInfo.getReservationId());

        testMeetingLinks(result.getUuid(), result.getLinks());
    }

    @Test
    void testV2SchedulingInfoUuidGet() throws ApiException {
        var result = videoSchedulingInformationV2Api.v2SchedulingInfoUuidGetWithHttpInfo(schedulingInfo405Uuid());
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingInfoResult = result.getData();
        assertNotNull(schedulingInfoResult.getMeetingDetails());
        assertNotNull(schedulingInfoResult.getMeetingDetails().getShortId());
        assertEquals("https://video.link/" + schedulingInfoResult.getMeetingDetails().getShortId(), schedulingInfoResult.getShortLink());
        assertEquals(schedulingInfoResult.getShortLink(), schedulingInfoResult.getShortlink());
        assertEquals("custom_portal_guest", schedulingInfoResult.getCustomPortalGuest());
        assertEquals("custom_portal_host", schedulingInfoResult.getCustomPortalHost());
        assertEquals("return_url", schedulingInfoResult.getReturnUrl());
        assertEquals(DirectMedia.NEVER, schedulingInfoResult.getDirectMedia());
    }

    @Test
    void testV2SchedulingInfoUuidPut() throws ApiException {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.PROVISION_PROBLEMS)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());

        var result = videoSchedulingInformationV2Api.v2SchedulingInfoUuidPutWithHttpInfo(putSchedulingInfoUuid(), input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingInfoResult = result.getData();
        assertEquals(input.getProvisionStatus(), schedulingInfoResult.getProvisionStatus());
        assertEquals(input.getProvisionStatusDescription(), schedulingInfoResult.getProvisionStatusDescription());
        assertEquals(input.getProvisionVmrId(), schedulingInfoResult.getProvisionVmrId());
    }

    @Test
    void testV2SchedulingInfoUuidPutDeprovisionSchedulingInfo() throws ApiException, SQLException {
        // Create scheduling info.
        var inputPost = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);

        var createdSchedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoPost(inputPost);
        verifyRowExistsInDatabase("select * from scheduling_info where uri_domain = 'def-test.dk' and uri_without_domain is not null and uuid = '" + createdSchedulingInfo.getUuid() + "'");

        // Deprovision(update) scheduling info
        var inputPut = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());

        var updatedSchedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoUuidPut(createdSchedulingInfo.getUuid(), inputPut);
        verifyRowExistsInDatabase("select * from scheduling_info where uri_domain is null and uri_without_domain is null and uuid = '" + updatedSchedulingInfo.getUuid() + "'");
    }

    @Test
    void testV2TimestampFormat() throws JSONException {
        // POST
        var inputPost = """
                {
                  "organizationId": "user-org-pool",
                  "schedulingTemplateId": "201"
                }""";

        String postResult;
        try (var client = ClientBuilder.newClient()) {
            postResult = client.target(UriBuilder.fromPath(getApiBasePath()))
                    .path("v2")
                    .path("scheduling-info")
                    .request()
                    .header("Authorization", "Bearer " + allRoleAttToken)
                    .post(Entity.json(inputPost), String.class);
        }
        var postResultJson = new JSONObject(postResult);
        assertThat(postResultJson.getString("createdTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\+01:00|\\+02:00|Z)$");

        // PUT
        var inputPut = """
                {
                  "provisionStatus": "AWAITS_PROVISION"
                }""";

        String putResult;
        try (var client = ClientBuilder.newClient()) {
            putResult = client.target(UriBuilder.fromPath(getApiBasePath()))
                    .path("v2")
                    .path("scheduling-info")
                    .path(postResultJson.getString("uuid"))
                    .request()
                    .header("Authorization", "Bearer " + allRoleAttToken)
                    .put(Entity.json(inputPut), String.class);
        }
        var putResultJson = new JSONObject(putResult);
        assertThat(putResultJson.getString("provisionTimestamp")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\+01:00|\\+02:00|Z)$");
        assertThat(putResultJson.getString("createdTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\+01:00|\\+02:00|Z)$");
        assertThat(putResultJson.getString("updatedTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\+01:00|\\+02:00|Z)$");
    }

    //----------- CORS tests -----------
    @Test
    void testV2SchedulingInfoGetCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoGetCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoPostCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("POST"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoPostCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "POST")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoUuidPutCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info/" + schedulingInfo405Uuid(), getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "PUT")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("PUT"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoUuidPutCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info/" + schedulingInfo405Uuid(), getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "PUT")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoUuidGetCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info/" + schedulingInfo405Uuid(), getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoUuidGetCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info/" + schedulingInfo405Uuid(), getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoProvisionGetCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info-provision", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoProvisionGetCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info-provision", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoDeprovisionGetCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info-deprovision", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoDeprovisionGetCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info-deprovision", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoReserveGetCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info-reserve", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoReserveGetCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info-reserve", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoReserveUuidGetCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info-reserve/" + schedulingInfo413ReservationUuid(), getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testV2SchedulingInfoReserveUuidGetCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/scheduling-info-reserve/" + schedulingInfo413ReservationUuid(), getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    // ----------- test data help -----------
    private UUID schedulingInfo405Uuid() {
        return UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc3");
    }

    private UUID schedulingInfo413ReservationUuid() {
        return UUID.fromString("5af163cb-f769-44c5-811e-7d7cb77f27f7");
    }

    private UUID putSchedulingInfoUuid() {
        return UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc2");
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private void testMeetingLinks(UUID meetingUuid, MeetingLinks links) {
        assertNotNull(links);
        assertNotNull(links.getSelf());
        assertNotNull(links.getSelf().getHref());
        var meetingLink = links.getSelf().getHref().toString().replace("https", "http");

        var requestMeeting = HttpRequest.newBuilder(URI.create(meetingLink))
                .header("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl()))
                .build();

        String schedulingInfoUuid;
        HttpResponse<String> responseSchedulingInfo;
        try (var client = HttpClient.newBuilder().build()) {
            var responseMeeting = client.send(requestMeeting, HttpResponse.BodyHandlers.ofString());
            assertNotNull(responseMeeting);
            assertEquals(200, responseMeeting.statusCode());
            assertNotNull(responseMeeting.body());
            assertTrue(responseMeeting.body().contains("\"uuid\":\"%s\"".formatted(meetingUuid)));

            assertNotNull(links.getSchedulingInfo());
            assertNotNull(links.getSchedulingInfo().getHref());
            var schedulingInfoLink = links.getSchedulingInfo().getHref().toString().replace("https", "http");
            schedulingInfoUuid = schedulingInfoLink.replace(getApiBasePath() + "/v2/scheduling-info/", "");
            var requestSchedulingInfo = HttpRequest.newBuilder(URI.create(schedulingInfoLink))
                    .header("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl()))
                    .build();

            responseSchedulingInfo = client.send(requestSchedulingInfo, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(responseSchedulingInfo);
        assertEquals(200, responseSchedulingInfo.statusCode());
        assertNotNull(responseSchedulingInfo.body());
        assertTrue(responseSchedulingInfo.body().matches("\\{\"uuid\":\"" + schedulingInfoUuid + "\".*\"meetingDetails\":.*\"uuid\":\"" + meetingUuid + "\".*}"));
    }
}
