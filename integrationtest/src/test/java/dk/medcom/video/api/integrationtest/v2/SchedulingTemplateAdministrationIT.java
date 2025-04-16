package dk.medcom.video.api.integrationtest.v2;

import dk.medcom.video.api.integrationtest.AbstractIntegrationTest;
import dk.medcom.video.api.integrationtest.v2.helper.HeaderBuilder;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.SchedulingTemplateAdministrationV2Api;
import org.openapitools.client.model.*;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class SchedulingTemplateAdministrationIT extends AbstractIntegrationTest {

    private final SchedulingTemplateAdministrationV2Api schedulingTemplateAdministrationV2Api;
    private final SchedulingTemplateAdministrationV2Api schedulingTemplateAdministrationV2ApiNoHeader;
    private final SchedulingTemplateAdministrationV2Api schedulingTemplateAdministrationV2ApiInvalidJwt;
    private final SchedulingTemplateAdministrationV2Api schedulingTemplateAdministrationV2ApiNoRoleAtt;
    private final SchedulingTemplateAdministrationV2Api schedulingTemplateAdministrationV2ApiNotAdmin;

    public SchedulingTemplateAdministrationIT() {
        var apiClient = new ApiClient();
        apiClient.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl()));
        apiClient.setBasePath(getApiBasePath());

        schedulingTemplateAdministrationV2Api = new SchedulingTemplateAdministrationV2Api(apiClient);

        var apiClientNoHeader = new ApiClient();
        apiClientNoHeader.setBasePath(getApiBasePath());
        schedulingTemplateAdministrationV2ApiNoHeader = new SchedulingTemplateAdministrationV2Api(apiClientNoHeader);

        var apiClientInvalidJwt = new ApiClient();
        apiClientInvalidJwt.setBasePath(getApiBasePath());
        apiClientInvalidJwt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getInvalidJwt());
        schedulingTemplateAdministrationV2ApiInvalidJwt = new SchedulingTemplateAdministrationV2Api(apiClientInvalidJwt);

        var apiClientNoRoleAtt = new ApiClient();
        apiClientNoRoleAtt.setBasePath(getApiBasePath());
        apiClientNoRoleAtt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl()));
        schedulingTemplateAdministrationV2ApiNoRoleAtt = new SchedulingTemplateAdministrationV2Api(apiClientNoRoleAtt);

        var apiClientNotAdmin = new ApiClient();
        apiClientNotAdmin.setBasePath(getApiBasePath());
        apiClientNotAdmin.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNotAdmin(getKeycloakUrl()));
        schedulingTemplateAdministrationV2ApiNotAdmin = new SchedulingTemplateAdministrationV2Api(apiClientNotAdmin);
    }

    // ----------- JWT error ----------
    @Test
    public void errorIfNoJwtToken_v2SchedulingTemplatesGet() {
        var expectedException = assertThrows(ApiException.class, schedulingTemplateAdministrationV2ApiNoHeader::v2SchedulingTemplatesGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingTemplatesGet() {
        var expectedException = assertThrows(ApiException.class, schedulingTemplateAdministrationV2ApiInvalidJwt::v2SchedulingTemplatesGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_v2SchedulingTemplatesGet() {
        var expectedException = assertThrows(ApiException.class, schedulingTemplateAdministrationV2ApiNoRoleAtt::v2SchedulingTemplatesGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingTemplatesIdDelete() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiNoHeader.v2SchedulingTemplatesIdDelete(201L));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingTemplatesIdDelete() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiInvalidJwt.v2SchedulingTemplatesIdDelete(201L));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_v2SchedulingTemplatesIdDelete() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiNotAdmin.v2SchedulingTemplatesIdDelete(201L));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingTemplatesIdGet() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiNoHeader.v2SchedulingTemplatesIdGet(201L));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingTemplatesIdGet() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiInvalidJwt.v2SchedulingTemplatesIdGet(201L));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_v2SchedulingTemplatesIdGet() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiNoRoleAtt.v2SchedulingTemplatesIdGet(201L));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingTemplatesIdPut() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiNoHeader.v2SchedulingTemplatesIdPut(201L, randomSchedulingTemplateRequest()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingTemplatesIdPut() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiInvalidJwt.v2SchedulingTemplatesIdPut(201L, randomSchedulingTemplateRequest()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_v2SchedulingTemplatesIdPut() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiNotAdmin.v2SchedulingTemplatesIdPut(201L, randomSchedulingTemplateRequest()));
        assertEquals(403, expectedException.getCode());
    }

    @Test
    public void errorIfNoJwtToken_v2SchedulingTemplatesPost() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiNoHeader.v2SchedulingTemplatesPost(randomSchedulingTemplateRequest()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2SchedulingTemplatesPost() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiInvalidJwt.v2SchedulingTemplatesPost(randomSchedulingTemplateRequest()));
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNotAdmin_v2SchedulingTemplatesPost() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2ApiNotAdmin.v2SchedulingTemplatesPost(randomSchedulingTemplateRequest()));
        assertEquals(403, expectedException.getCode());
    }


    //----- No JWT errors -------

    @Test
    public void testV2SchedulingTemplatesGet() throws ApiException {
        var result = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesGetWithHttpInfo();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingTemplatesResult = result.getData();
        assertTrue(schedulingTemplatesResult.size() >= 2);

        assertTrue(schedulingTemplatesResult.stream().anyMatch(x -> x.getId().equals(201L)));
        assertTrue(schedulingTemplatesResult.stream().anyMatch(x -> x.getId().equals(204L)));
        
        //Not in user-org-pool
        assertFalse(schedulingTemplatesResult.stream().anyMatch(x -> x.getId().equals(202L)));
    }

    @Test
    public void testV2SchedulingTemplatesIdDelete() throws ApiException {
        var result = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesIdDeleteWithHttpInfo(203L);
        assertNotNull(result);
        assertEquals(204, result.getStatusCode());
    }

    @Test
    public void testV2SchedulingTemplatesIdGet() throws ApiException {
        var result = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesIdGetWithHttpInfo(201L);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingTemplateResult = result.getData();
        assertEquals(201, schedulingTemplateResult.getId(), 0);
        assertEquals("user-org-pool", schedulingTemplateResult.getOrganisationId());
        assertEquals(22, schedulingTemplateResult.getConferencingSysId(), 0);
        assertEquals("default", schedulingTemplateResult.getUriPrefix());
    }

    @Test
    public void testV2SchedulingTemplatesIdGetFromOtherOrg() {
        var expectedException = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesIdGetWithHttpInfo(202L));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getCode());
        assertTrue(expectedException.getMessage().contains("Resource: schedulingTemplate in field: id not found."));
    }

    @Test
    public void testV2SchedulingTemplatesIdPut() throws ApiException {
        var input = randomSchedulingTemplateRequest();

        var result = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesIdPutWithHttpInfo(204L, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        assertNotNull(result.getData());
        var schedulingTemplateResult = result.getData();
        assertEquals(204, schedulingTemplateResult.getId(), 0);
        assertEquals("user-org-pool", schedulingTemplateResult.getOrganisationId());
        assertEquals(input.getConferencingSysId(), schedulingTemplateResult.getConferencingSysId(), 0);
        assertEquals(input.getUriPrefix(), schedulingTemplateResult.getUriPrefix());
        assertNotNull(schedulingTemplateResult.getCreatedBy());
        assertEquals("user-org-pool", schedulingTemplateResult.getCreatedBy().getOrganisationId());
        assertEquals("in-user-org@user102.dk", schedulingTemplateResult.getCreatedBy().getEmail());
        assertNotNull(schedulingTemplateResult.getUpdatedBy());
        assertEquals("user-org-pool", schedulingTemplateResult.getUpdatedBy().getOrganisationId());
        assertEquals("eva@klak.dk", schedulingTemplateResult.getUpdatedBy().getEmail());
    }

    @Test
    public void testV2SchedulingTemplatesPostV2ThenSchedulingTemplatesIdPutThenV2SchedulingTemplatesIdGet() throws ApiException {
        //Given
        var inputPost = randomSchedulingTemplateRequest();

        var inputPut = new SchedulingTemplateRequest()
                .conferencingSysId(inputPost.getConferencingSysId())
                .uriPrefix(inputPost.getUriPrefix())
                .uriDomain(inputPost.getUriDomain())
                .hostPinRequired(inputPost.getHostPinRequired())
                .guestPinRequired(inputPost.getGuestPinRequired())
                .uriNumberRangeLow(inputPost.getUriNumberRangeLow())
                .uriNumberRangeHigh(inputPost.getUriNumberRangeHigh())
                .vmrType(VmrType.CONFERENCE)
                .hostView(ViewType.SIXTEEN_MAINS_ZERO_PIPS)
                .enableOverlayText(false)
                .customPortalGuest(randomString())
                .customPortalHost(randomString())
                .returnUrl(randomString())
                .directMedia(DirectMedia.NEVER)
                .ivrTheme(randomString());

        //When
        var resultPost = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesPostWithHttpInfo(inputPost);
        assertNotNull(resultPost);
        assertEquals(200, resultPost.getStatusCode());
        assertNotNull(resultPost.getData());

        var resultPut = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesIdPutWithHttpInfo(resultPost.getData().getId(), inputPut);
        assertNotNull(resultPut);
        assertEquals(200, resultPut.getStatusCode());
        assertNotNull(resultPut.getData());

        var resultGet = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesIdGetWithHttpInfo(resultPost.getData().getId());
        assertNotNull(resultGet);
        assertEquals(200, resultGet.getStatusCode());
        assertNotNull(resultGet.getData());

        var result = resultGet.getData();
        assertNotNull(result);

        // Updated
        assertEquals(inputPut.getVmrType(), result.getVmrType());
        assertEquals(inputPut.getHostView(), result.getHostView());
        assertEquals(inputPut.getEnableOverlayText(), result.getEnableOverlayText());
        assertEquals(inputPut.getCustomPortalGuest(), result.getCustomPortalGuest());
        assertEquals(inputPut.getCustomPortalHost(), result.getCustomPortalHost());
        assertEquals(inputPut.getReturnUrl(), result.getReturnUrl());
        assertFalse(result.getIsPoolTemplate());
        assertEquals(inputPut.getDirectMedia(), result.getDirectMedia());
        assertEquals(inputPut.getIvrTheme(), result.getIvrTheme());

        // Not set, still updated
        assertNull(result.getHostPinRangeLow());
        assertNull(result.getHostPinRangeHigh());
        assertNull(result.getGuestPinRangeLow());
        assertNull(result.getGuestPinRangeHigh());
    }

    @Test
    public void testV2SchedulingTemplatesPost() throws ApiException {
        var input = randomSchedulingTemplateRequest();

        var result = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesPostWithHttpInfo(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingTemplateResult = result.getData();
        assertNotNull(schedulingTemplateResult.getId());
        assertEquals("user-org-pool", schedulingTemplateResult.getOrganisationId());

        // Overwrites default values
        assertEquals(input.getVmrAvailableBefore(), schedulingTemplateResult.getvMRAvailableBefore());
        assertEquals(input.getMaxParticipants(), schedulingTemplateResult.getMaxParticipants());
        assertEquals(input.getEndMeetingOnEndTime(), schedulingTemplateResult.getEndMeetingOnEndTime());
        assertEquals(input.getIsDefaultTemplate(), schedulingTemplateResult.getIsDefaultTemplate());
        assertEquals(input.getIsPoolTemplate(), schedulingTemplateResult.getIsPoolTemplate());
        assertEquals(input.getVmrType(), schedulingTemplateResult.getVmrType());
        assertEquals(input.getHostView(), schedulingTemplateResult.getHostView());
        assertEquals(input.getGuestView(), schedulingTemplateResult.getGuestView());
        assertEquals(input.getVmrQuality(), schedulingTemplateResult.getVmrQuality());
        assertEquals(input.getEnableOverlayText(), schedulingTemplateResult.getEnableOverlayText());
        assertEquals(input.getGuestsCanPresent(), schedulingTemplateResult.getGuestsCanPresent());
        assertEquals(input.getForcePresenterIntoMain(), schedulingTemplateResult.getForcePresenterIntoMain());
        assertEquals(input.getForceEncryption(), schedulingTemplateResult.getForceEncryption());
        assertEquals(input.getMuteAllGuests(), schedulingTemplateResult.getMuteAllGuests());
        assertEquals(input.getDirectMedia(), schedulingTemplateResult.getDirectMedia());
    }

    @Test
    public void testV2SchedulingTemplatesPostWithDefaultValues() throws ApiException {
        //Given only required parameters
        SchedulingTemplateRequest input = new SchedulingTemplateRequest();
        input.setConferencingSysId(43L);
        input.setUriPrefix(randomString());
        input.setUriDomain(randomString());
        input.setHostPinRequired(randomBoolean());
        input.setGuestPinRequired(randomBoolean());
        input.setUriNumberRangeLow(201L);
        input.setUriNumberRangeHigh(100L);
        input.setIvrTheme(randomString());

        var result = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesPostWithHttpInfo(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        var schedulingTemplateResult = result.getData();
        assertNotNull(schedulingTemplateResult);
        assertNotNull(schedulingTemplateResult.getId());
        assertEquals("user-org-pool", schedulingTemplateResult.getOrganisationId());
        assertEquals(input.getConferencingSysId(), schedulingTemplateResult.getConferencingSysId());
        assertEquals(input.getUriPrefix(), schedulingTemplateResult.getUriPrefix());
        assertEquals(input.getUriDomain(), schedulingTemplateResult.getUriDomain());
        assertEquals(input.getHostPinRequired(), schedulingTemplateResult.getHostPinRequired());
        assertNull(schedulingTemplateResult.getHostPinRangeLow());
        assertNull(schedulingTemplateResult.getHostPinRangeHigh());
        assertEquals(input.getGuestPinRequired(), schedulingTemplateResult.getGuestPinRequired());
        assertNull(schedulingTemplateResult.getGuestPinRangeLow());
        assertNull(schedulingTemplateResult.getGuestPinRangeHigh());
        assertEquals(input.getUriNumberRangeLow(), schedulingTemplateResult.getUriNumberRangeLow());
        assertEquals(input.getUriNumberRangeHigh(), schedulingTemplateResult.getUriNumberRangeHigh());
        assertEquals(input.getIvrTheme(), schedulingTemplateResult.getIvrTheme());
        assertNull(schedulingTemplateResult.getCustomPortalGuest());
        assertNull(schedulingTemplateResult.getCustomPortalHost());
        assertNull(schedulingTemplateResult.getReturnUrl());
        assertNotNull(schedulingTemplateResult.getCreatedBy());
        assertEquals("user-org-pool", schedulingTemplateResult.getCreatedBy().getOrganisationId());
        assertEquals("eva@klak.dk", schedulingTemplateResult.getCreatedBy().getEmail());
        assertNull(schedulingTemplateResult.getUpdatedBy());
        assertNotNull(schedulingTemplateResult.getCreatedTime());
        assertNull(schedulingTemplateResult.getUpdatedTime());
        assertNotNull(schedulingTemplateResult.getLinks());
        assertNotNull(schedulingTemplateResult.getLinks().getSelf());
        assertNotNull(schedulingTemplateResult.getLinks().getSelf().getHref());
        assertTrue(schedulingTemplateResult.getLinks().getSelf().getHref().getPath().contains(schedulingTemplateResult.getId().toString()));

        // Default values
        assertEquals(0, schedulingTemplateResult.getvMRAvailableBefore(), 0);
        assertEquals(0, schedulingTemplateResult.getMaxParticipants(), 0);
        assertEquals(Boolean.FALSE, schedulingTemplateResult.getEndMeetingOnEndTime());
        assertEquals(Boolean.FALSE, schedulingTemplateResult.getIsDefaultTemplate());
        assertEquals(Boolean.FALSE, schedulingTemplateResult.getIsPoolTemplate());
        assertEquals(VmrType.CONFERENCE, schedulingTemplateResult.getVmrType());
        assertEquals(ViewType.ONE_MAIN_SEVEN_PIPS, schedulingTemplateResult.getHostView());
        assertEquals(ViewType.ONE_MAIN_SEVEN_PIPS, schedulingTemplateResult.getGuestView());
        assertEquals(VmrQuality.HD, schedulingTemplateResult.getVmrQuality());
        assertEquals(Boolean.TRUE, schedulingTemplateResult.getEnableOverlayText());
        assertEquals(Boolean.TRUE, schedulingTemplateResult.getGuestsCanPresent());
        assertEquals(Boolean.TRUE, schedulingTemplateResult.getForcePresenterIntoMain());
        assertEquals(Boolean.FALSE, schedulingTemplateResult.getForceEncryption());
        assertEquals(Boolean.FALSE, schedulingTemplateResult.getMuteAllGuests());
        assertEquals(DirectMedia.NEVER, schedulingTemplateResult.getDirectMedia());
    }

    @Test
    public void testV2SchedulingTemplatesPostV2SchedulingTemplatesIdPutOnlyOnePoolTemplateAllowed() throws ApiException {
        var inputOne = randomSchedulingTemplateRequest();
        inputOne.setIsPoolTemplate(true);

        var inputTwo = randomSchedulingTemplateRequest();
        inputTwo.setIsPoolTemplate(true);

        var inputThree = randomSchedulingTemplateRequest();
        inputThree.setIsPoolTemplate(false);

        var resultOne = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesPostWithHttpInfo(inputOne);
        assertNotNull(resultOne);
        assertEquals(200, resultOne.getStatusCode());

        assertNotNull(resultOne.getData());
        assertTrue(resultOne.getData().getIsPoolTemplate());

        var expectedExceptionPost = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesPost(inputTwo));
        assertEquals(406, expectedExceptionPost.getCode());
        assertTrue(expectedExceptionPost.getResponseBody().contains("Create or update of pool template failed due to only one pool template allowed"));

        var resultThree = schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesPostWithHttpInfo(inputThree);
        assertNotNull(resultThree);
        assertEquals(200, resultThree.getStatusCode());

        assertNotNull(resultThree.getData());
        assertFalse(resultThree.getData().getIsPoolTemplate());

        var updateSchedulingTemplateThree = randomSchedulingTemplateRequest();
        updateSchedulingTemplateThree.setIsPoolTemplate(true);

        var expectedExceptionPut = assertThrows(ApiException.class, () -> schedulingTemplateAdministrationV2Api.v2SchedulingTemplatesIdPut(resultThree.getData().getId(), updateSchedulingTemplateThree));
        assertEquals(406, expectedExceptionPut.getCode());
        assertTrue(expectedExceptionPut.getResponseBody().contains("Create or update of pool template failed due to only one pool template allowed"));
    }

    // ---------- helper methods ---------
    private long count = 10000L;

    private SchedulingTemplateRequest randomSchedulingTemplateRequest() {
        return new SchedulingTemplateRequest()
                .conferencingSysId(count++)
                .uriPrefix(randomString())
                .uriDomain(randomString())
                .hostPinRequired(randomBoolean())
                .hostPinRangeLow(count++)
                .hostPinRangeHigh(count++)
                .guestPinRequired(randomBoolean())
                .guestPinRangeLow(count++)
                .guestPinRangeHigh(count++)
                .vmrAvailableBefore((int) count++)
                .maxParticipants((int) count++)
                .endMeetingOnEndTime(randomBoolean())
                .uriNumberRangeLow(count++ - 1000L)
                .uriNumberRangeHigh(count++ + 1000L)
                .ivrTheme(randomString())
                .isDefaultTemplate(false)
                .isPoolTemplate(false)
                .customPortalGuest(randomString())
                .customPortalHost(randomString())
                .returnUrl(randomString())
                .vmrType(VmrType.LECTURE)
                .hostView(ViewType.TWENTYFIVE_MAINS_ZERO_PIPS)
                .guestView(ViewType.TWO_MAINS_TWENTYONE_PIPS)
                .vmrQuality(VmrQuality.FULLHD)
                .enableOverlayText(randomBoolean())
                .guestsCanPresent(randomBoolean())
                .forcePresenterIntoMain(randomBoolean())
                .forceEncryption(randomBoolean())
                .muteAllGuests(randomBoolean())
                .directMedia(DirectMedia.BEST_EFFORT);
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    private static boolean randomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }
}
