package dk.medcom.video.api.integrationtest.v2;

import dk.medcom.video.api.integrationtest.AbstractIntegrationTest;
import dk.medcom.video.api.integrationtest.v2.helper.HeaderBuilder;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.VideoMeetingsV2Api;
import org.openapitools.client.api.VideoSchedulingInformationV2Api;
import org.openapitools.client.model.*;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class VideoSchedulingInformationIT extends AbstractIntegrationTest {

    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2Api;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiNoHeader;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiInvalidJwt;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiNoScopes;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiNotAdmin;
    private final VideoSchedulingInformationV2Api videoSchedulingInformationV2ApiNotProvisionUser;
    private final VideoMeetingsV2Api videoMeetingsV2Api;

    public VideoSchedulingInformationIT() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtAllScopes(getKeycloakUrl()));

        videoSchedulingInformationV2Api = new VideoSchedulingInformationV2Api(apiClient);
        videoMeetingsV2Api = new VideoMeetingsV2Api(apiClient);

        var apiClientNoHeader = new ApiClient();
        apiClientNoHeader.setBasePath(getApiBasePath());
        videoSchedulingInformationV2ApiNoHeader = new VideoSchedulingInformationV2Api(apiClientNoHeader);

        var apiClientInvalidJwt = new ApiClient();
        apiClientInvalidJwt.setBasePath(getApiBasePath());
        apiClientInvalidJwt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getInvalidJwt());
        videoSchedulingInformationV2ApiInvalidJwt = new VideoSchedulingInformationV2Api(apiClientInvalidJwt);

        var apiClientNoScopes = new ApiClient();
        apiClientNoScopes.setBasePath(getApiBasePath());
        apiClientNoScopes.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNoScope(getKeycloakUrl()));
        videoSchedulingInformationV2ApiNoScopes = new VideoSchedulingInformationV2Api(apiClientNoScopes);

        var apiClientNotAdmin = new ApiClient();
        apiClientNotAdmin.setBasePath(getApiBasePath());
        apiClientNotAdmin.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNotAdminScope(getKeycloakUrl()));
        videoSchedulingInformationV2ApiNotAdmin = new VideoSchedulingInformationV2Api(apiClientNotAdmin);

        var apiClientNotProvisionUser = new ApiClient();
        apiClientNotProvisionUser.setBasePath(getApiBasePath());
        apiClientNotProvisionUser.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNotProvisionUserScope(getKeycloakUrl()));
        videoSchedulingInformationV2ApiNotProvisionUser = new VideoSchedulingInformationV2Api(apiClientNotProvisionUser);
    }

    //---------- JWT errors --------
    @Test
    public void errorIfNoJwtToken_v2SchedulingInfoDeprovisionGet() {
        var expectedException = assertThrows(ApiException.class, videoSchedulingInformationV2ApiNoHeader::v2SchedulingInfoDeprovisionGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingInfoDeprovisionGet() {
        var expectedException = assertThrows(ApiException.class, videoSchedulingInformationV2ApiInvalidJwt::v2SchedulingInfoDeprovisionGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotProvisionUser_v2SchedulingInfoDeprovisionGet() {
        var expectedException = assertThrows(ApiException.class, videoSchedulingInformationV2ApiNotProvisionUser::v2SchedulingInfoDeprovisionGet);
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingInfoGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoGet(OffsetDateTime.now(), OffsetDateTime.now(), ProvisionStatus.AWAITS_PROVISION));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingInfoGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiInvalidJwt.v2SchedulingInfoGet(OffsetDateTime.now(), OffsetDateTime.now(), ProvisionStatus.AWAITS_PROVISION));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoScopesInToken_v2SchedulingInfoGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNoScopes.v2SchedulingInfoGet(OffsetDateTime.now(), OffsetDateTime.now(), ProvisionStatus.AWAITS_PROVISION));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoPost(input));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiInvalidJwt.v2SchedulingInfoPost(input));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotProvisionUser_v2SchedulingInfoPost() {
        var input = new CreateSchedulingInfo()
                .organizationId("user-org-pool")
                .schedulingTemplateId(201L);
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNotProvisionUser.v2SchedulingInfoPost(input));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingInfoProvisionGet() {
        var expectedException = assertThrows(ApiException.class, videoSchedulingInformationV2ApiNoHeader::v2SchedulingInfoProvisionGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingInfoProvisionGet() {
        var expectedException = assertThrows(ApiException.class, videoSchedulingInformationV2ApiInvalidJwt::v2SchedulingInfoProvisionGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotProvisionUser_v2SchedulingInfoProvisionGet() {
        var expectedException = assertThrows(ApiException.class, videoSchedulingInformationV2ApiNotProvisionUser::v2SchedulingInfoProvisionGet);
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingInfoReserveGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoReserveGet(
                VmrType.LECTURE, ViewType.ONE_MAIN_SEVEN_PIPS, ViewType.ONE_MAIN_SEVEN_PIPS, VmrQuality.SD,
                true, false, true, false, true
        ));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingInfoReserveGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiInvalidJwt.v2SchedulingInfoReserveGet(
                VmrType.LECTURE, ViewType.ONE_MAIN_SEVEN_PIPS, ViewType.ONE_MAIN_SEVEN_PIPS, VmrQuality.SD,
                true, false, true, false, true
        ));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_v2SchedulingInfoReserveGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNotAdmin.v2SchedulingInfoReserveGet(
                VmrType.LECTURE, ViewType.ONE_MAIN_SEVEN_PIPS, ViewType.ONE_MAIN_SEVEN_PIPS, VmrQuality.SD,
                true, false, true, false, true
        ));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingInfoReserveUuidGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingInfoReserveUuidGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiInvalidJwt.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_v2SchedulingInfoReserveUuidGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNotAdmin.v2SchedulingInfoReserveUuidGet(UUID.randomUUID()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingInfoUuidGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingInfoUuidGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiInvalidJwt.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoScopesInToken_v2SchedulingInfoUuidGet() {
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNoScopes.v2SchedulingInfoUuidGet(schedulingInfo405Uuid()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNoHeader.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiInvalidJwt.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotProvisionUser_v2SchedulingInfoUuidPut() {
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.DEPROVISION_OK)
                .provisionStatusDescription(randomString())
                .provisionVmrId(randomString());
        var expectedException = assertThrows(ApiException.class, () -> videoSchedulingInformationV2ApiNotProvisionUser.v2SchedulingInfoUuidPut(schedulingInfo405Uuid(), input));
        assertEquals(403, expectedException.getCode());
    }

// ---------- No JWT errors --------

    @Test
    public void testV2SchedulingInfoDeprovisionGet() throws ApiException {
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
    public void testV2SchedulingInfoGet() throws ApiException {
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
    public void testV2SchedulingInfoPost() throws ApiException {
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
    public void testV2SchedulingInfoPostThenV2SchedulingInfoUuidGet() throws ApiException {
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
    public void testV2SchedulingInfoProvisionGet() throws ApiException {
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
    public void testV2SchedulingInfoProvisionGetDefaultValues() throws ApiException {
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
        assertEquals(ViewType.ONE_MAIN_SEVEN_PIPS, createdSchedulingInfo.getHostView());
        assertEquals(ViewType.ONE_MAIN_SEVEN_PIPS, createdSchedulingInfo.getGuestView());
        assertEquals(VmrQuality.SD, createdSchedulingInfo.getVmrQuality());
        assertEquals(Boolean.TRUE, createdSchedulingInfo.getEnableOverlayText());
        assertEquals(Boolean.TRUE, createdSchedulingInfo.getGuestsCanPresent());
        assertEquals(Boolean.TRUE, createdSchedulingInfo.getForcePresenterIntoMain());
        assertEquals(Boolean.FALSE, createdSchedulingInfo.getForceEncryption());
        assertEquals(Boolean.FALSE, createdSchedulingInfo.getMuteAllGuests());
    }

    @Test
    public void testV2SchedulingInfoReserveGet() throws ApiException {
        var result = videoSchedulingInformationV2Api.v2SchedulingInfoReserveGetWithHttpInfo(
                null,null,null,null,
                null,null,null,null,null);
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
    public void testV2SchedulingInfoReserveGetThenV2SchedulingInfoReserveUuidGet() throws ApiException {
        var result = videoSchedulingInformationV2Api.v2SchedulingInfoReserveGetWithHttpInfo(
                VmrType.LECTURE,null,null, VmrQuality.FULLHD,
                null,null,null,null,null);
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
    public void testV2SchedulingInfoReserveUuidGet() throws ApiException {
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
    public void testUseReservedSchedulingInfo() throws ApiException {
        var schedulingInfo = videoSchedulingInformationV2Api.v2SchedulingInfoReserveGet(null,null,null,null,null,null,null,null,null);
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
    }


    @Test
    public void testV2SchedulingInfoUuidGet() throws ApiException {
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
    public void testV2SchedulingInfoUuidPut() throws ApiException {
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
    public void testV2SchedulingInfoUuidPutDeprovisionSchedulingInfo() throws ApiException, SQLException {
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

}
