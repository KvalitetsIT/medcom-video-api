package dk.medcom.video.api.test;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.UriBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.SchedulingTemplateAdministrationApi;
import org.openapitools.client.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class SchedulingTemplateIT extends IntegrationWithOrganisationServiceTest {
    private SchedulingTemplateAdministrationApi schedulingTemplate;

    @BeforeEach
    public void setupApiClient() {
        var apiClient = new ApiClient()
                .setBasePath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort))
                .setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

        schedulingTemplate = new SchedulingTemplateAdministrationApi(apiClient);
    }

    @Test
    public void testCreateSchedulingTemplate_UseDefaultValues() throws ApiException {
        //Given
        SchedulingTemplateRequest create = new SchedulingTemplateRequest();
        create.setConferencingSysId(43L);
        create.setUriPrefix("43");
        create.setUriDomain("test.dk");
        create.setHostPinRequired(true);
        create.setGuestPinRequired(true);
        create.setUriNumberRangeLow(1L);
        create.setUriNumberRangeHigh(100L);
        create.setCustomPortalGuest("some_portal_guest");
        create.setCustomPortalHost("some_portal_host");
        create.setReturnUrl("return_url");
        create.setGuestView(ViewType.FIVE_MAINS_SEVEN_PIPS);
        create.setIvrTheme("ivr_theme");


        //When
        SchedulingTemplate resultCreate = schedulingTemplate.schedulingTemplatesPost(create);
        SchedulingTemplate result = this.schedulingTemplate.schedulingTemplatesIdGet(resultCreate.getId());

        //Then
        assertNotNull(result);
        assertNotNull(result.getVmrType());
        assertNotNull(result.getHostView());
        assertNotNull(result.getGuestView());
        assertNotNull(result.getVmrQuality());
        assertTrue(result.getEnableOverlayText());
        assertTrue(result.getGuestsCanPresent());
        assertTrue(result.getForcePresenterIntoMain());
        assertFalse(result.getForceEncryption());
        assertFalse(result.getMuteAllGuests());
        assertEquals(create.getCustomPortalGuest(), result.getCustomPortalGuest());
        assertEquals(create.getCustomPortalHost(), result.getCustomPortalHost());
        assertEquals(create.getReturnUrl(), result.getReturnUrl());
        assertEquals(create.getGuestView(), result.getGuestView());
        assertFalse(create.getIsPoolTemplate());
        assertEquals(DirectMedia.NEVER, result.getDirectMedia());
    }

    @Test
    public void testCreateSchedulingTemplate_OverwriteDefaultValues() throws ApiException {
        //Given
        SchedulingTemplateRequest create = new SchedulingTemplateRequest();
        create.setConferencingSysId(43L);
        create.setUriPrefix("43");
        create.setUriDomain("test.dk");
        create.setHostPinRequired(true);
        create.setGuestPinRequired(true);
        create.setUriNumberRangeLow(1L);
        create.setUriNumberRangeHigh(100L);
        create.setVmrType(VmrType.LECTURE);
        create.setHostView(ViewType.ONE_MAIN_SEVEN_PIPS);
        create.setEnableOverlayText(false);
        create.setDirectMedia(DirectMedia.BEST_EFFORT);
        create.setIvrTheme("ivr_theme");

        //When
        SchedulingTemplate resultCreate = schedulingTemplate.schedulingTemplatesPost(create);
        SchedulingTemplate result = this.schedulingTemplate.schedulingTemplatesIdGet(resultCreate.getId());

        //Then
        assertNotNull(result);
        assertEquals(create.getVmrType().toString(), result.getVmrType().toString());
        assertEquals(create.getHostView().toString(), result.getHostView().toString());
        assertEquals(create.getEnableOverlayText(), result.getEnableOverlayText());
        assertEquals(create.getDirectMedia(), result.getDirectMedia());
    }

    @Test
    public void testUpdateSchedulingTemplate() throws ApiException {
        //Given
        SchedulingTemplateRequest create = new SchedulingTemplateRequest();
        create.setConferencingSysId(43L);
        create.setUriPrefix("43");
        create.setUriDomain("test.dk");
        create.setHostPinRequired(true);
        create.setGuestPinRequired(true);
        create.setUriNumberRangeLow(1L);
        create.setUriNumberRangeHigh(100L);
        create.setIvrTheme("ivr_theme");

        SchedulingTemplateRequest updateSchedulingTemplate = new SchedulingTemplateRequest();
        updateSchedulingTemplate.setConferencingSysId(create.getConferencingSysId());
        updateSchedulingTemplate.setUriPrefix(create.getUriPrefix());
        updateSchedulingTemplate.setUriDomain(create.getUriDomain());
        updateSchedulingTemplate.setHostPinRequired(create.getHostPinRequired());
        updateSchedulingTemplate.setGuestPinRequired(create.getGuestPinRequired());
        updateSchedulingTemplate.setUriNumberRangeLow(create.getUriNumberRangeLow());
        updateSchedulingTemplate.setUriNumberRangeHigh(create.getUriNumberRangeHigh());
        updateSchedulingTemplate.setVmrType(VmrType.LECTURE);
        updateSchedulingTemplate.setHostView(ViewType.TWO_MAINS_TWENTYONE_PIPS);
        updateSchedulingTemplate.setEnableOverlayText(false);
        updateSchedulingTemplate.setCustomPortalGuest(UUID.randomUUID().toString());
        updateSchedulingTemplate.setCustomPortalHost(UUID.randomUUID().toString());
        updateSchedulingTemplate.setReturnUrl(UUID.randomUUID().toString());
        updateSchedulingTemplate.setDirectMedia(DirectMedia.BEST_EFFORT);
        updateSchedulingTemplate.setIvrTheme("ivr_theme");

        //When
        SchedulingTemplate resultCreate = schedulingTemplate.schedulingTemplatesPost(create);
        schedulingTemplate.schedulingTemplatesIdPut(resultCreate.getId(), updateSchedulingTemplate);
        SchedulingTemplate result = schedulingTemplate.schedulingTemplatesIdGet(resultCreate.getId());

        //Then
        assertNotNull(result);
        assertEquals(updateSchedulingTemplate.getVmrType().toString(), result.getVmrType().toString());
        assertEquals(updateSchedulingTemplate.getHostView().toString(), result.getHostView().toString());
        assertEquals(updateSchedulingTemplate.getEnableOverlayText(), result.getEnableOverlayText());
        assertEquals(updateSchedulingTemplate.getCustomPortalGuest(), result.getCustomPortalGuest());
        assertEquals(updateSchedulingTemplate.getCustomPortalHost(), result.getCustomPortalHost());
        assertEquals(updateSchedulingTemplate.getReturnUrl(), result.getReturnUrl());
        assertFalse(result.getIsPoolTemplate());
        assertEquals(DirectMedia.BEST_EFFORT, result.getDirectMedia());
    }

    @Test
    public void testOnlyOnePoolTemplate() throws ApiException {
        SchedulingTemplateRequest createOne = new SchedulingTemplateRequest();
        createOne.setConferencingSysId(43L);
        createOne.setUriPrefix("43");
        createOne.setUriDomain("test.dk");
        createOne.setHostPinRequired(true);
        createOne.setGuestPinRequired(true);
        createOne.setUriNumberRangeLow(1L);
        createOne.setUriNumberRangeHigh(100L);
        createOne.setIsPoolTemplate(true);
        createOne.setIvrTheme("ivr_theme");

        SchedulingTemplateRequest createTwo = new SchedulingTemplateRequest();
        createTwo.setConferencingSysId(43L);
        createTwo.setUriPrefix("43");
        createTwo.setUriDomain("test.dk");
        createTwo.setHostPinRequired(true);
        createTwo.setGuestPinRequired(true);
        createTwo.setUriNumberRangeLow(1L);
        createTwo.setUriNumberRangeHigh(100L);
        createTwo.setIsPoolTemplate(true);
        createTwo.setIvrTheme("ivr_theme");

        SchedulingTemplateRequest createThree = new SchedulingTemplateRequest();
        createThree.setConferencingSysId(43L);
        createThree.setUriPrefix("43");
        createThree.setUriDomain("test.dk");
        createThree.setHostPinRequired(true);
        createThree.setGuestPinRequired(true);
        createThree.setUriNumberRangeLow(1L);
        createThree.setUriNumberRangeHigh(100L);
        createThree.setIvrTheme("ivr_theme");

        SchedulingTemplate resultCreateOne = schedulingTemplate.schedulingTemplatesPost(createOne);
        SchedulingTemplate resultOne = this.schedulingTemplate.schedulingTemplatesIdGet(resultCreateOne.getId());
        assertNotNull(resultOne);
        assertTrue(resultOne.getIsPoolTemplate());

        var expectedExceptionCreate = assertThrows(ApiException.class, () -> schedulingTemplate.schedulingTemplatesPost(createTwo));
        assertEquals(406, expectedExceptionCreate.getCode());
        assertTrue(expectedExceptionCreate.getResponseBody().contains("Create or update of pool template failed due to only one pool template allowed"));

        SchedulingTemplate resultCreateThree = schedulingTemplate.schedulingTemplatesPost(createThree);
        SchedulingTemplate resultThree = this.schedulingTemplate.schedulingTemplatesIdGet(resultCreateThree.getId());
        assertNotNull(resultThree);
        assertFalse(resultThree.getIsPoolTemplate());

        SchedulingTemplateRequest updateSchedulingTemplateThree = new SchedulingTemplateRequest();
        updateSchedulingTemplateThree.setConferencingSysId(createThree.getConferencingSysId());
        updateSchedulingTemplateThree.setUriPrefix(createThree.getUriPrefix());
        updateSchedulingTemplateThree.setUriDomain(createThree.getUriDomain());
        updateSchedulingTemplateThree.setHostPinRequired(createThree.getHostPinRequired());
        updateSchedulingTemplateThree.setGuestPinRequired(createThree.getGuestPinRequired());
        updateSchedulingTemplateThree.setUriNumberRangeLow(createThree.getUriNumberRangeLow());
        updateSchedulingTemplateThree.setUriNumberRangeHigh(createThree.getUriNumberRangeHigh());
        updateSchedulingTemplateThree.setIsPoolTemplate(true);

        var expectedExceptionUpdate = assertThrows(ApiException.class, () -> schedulingTemplate.schedulingTemplatesIdPut(resultThree.getId(), updateSchedulingTemplateThree));
        assertEquals(406, expectedExceptionUpdate.getCode());
        assertTrue(expectedExceptionUpdate.getResponseBody().contains("Create or update of pool template failed due to only one pool template allowed"));
    }

    @Test
    void testTimestampFormat() throws JSONException {
        // POST
        var inputPost = """
                {
                  "conferencingSysId": 1234,
                  "uriPrefix": "format",
                  "uriDomain": "timestamp-test.dk",
                  "hostPinRequired": true,
                  "guestPinRequired": true,
                  "uriNumberRangeLow": 1000,
                  "uriNumberRangeHigh": 9999,
                  "ivrTheme": "10"
                }""";

        String postResult;
        try(var client = ClientBuilder.newClient()) {
            postResult = client.target(UriBuilder.fromPath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort)))
                    .path("scheduling-templates")
                    .request()
                    .post(Entity.json(inputPost), String.class);
        }
        var postResultJson = new JSONObject(postResult);
        assertThat(postResultJson.getString("createdTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2} \\+0000$");

        // PUT
        var inputPut = """
                {
                  "conferencingSysId": 1234,
                  "uriPrefix": "format",
                  "uriDomain": "timestamp-test.dk",
                  "hostPinRequired": true,
                  "guestPinRequired": true,
                  "uriNumberRangeLow": 1000,
                  "uriNumberRangeHigh": 9999,
                  "ivrTheme": "10"
                }""";

        String putResult;
        try(var client = ClientBuilder.newClient()) {
            putResult = client.target(UriBuilder.fromPath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort)))
                    .path("scheduling-templates")
                    .path(postResultJson.getString("id"))
                    .request()
                    .put(Entity.json(inputPut), String.class);
        }
        var putResultJson = new JSONObject(putResult);
        assertThat(putResultJson.getString("createdTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2} \\+0000$");
        assertThat(putResultJson.getString("updatedTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2} \\+0000$");
    }
}
