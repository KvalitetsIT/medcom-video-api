package dk.medcom.video.api.test;

import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.SchedulingTemplateAdministrationApi;
import org.openapitools.client.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SchedulingTemplateIT extends IntegrationWithOrganisationServiceTest {
    private SchedulingTemplateAdministrationApi schedulingTemplate;

    @Before
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
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getVmrType());
        Assert.assertNotNull(result.getHostView());
        Assert.assertNotNull(result.getGuestView());
        Assert.assertNotNull(result.getVmrQuality());
        Assert.assertTrue(result.getEnableOverlayText());
        Assert.assertTrue(result.getGuestsCanPresent());
        Assert.assertTrue(result.getForcePresenterIntoMain());
        Assert.assertFalse(result.getForceEncryption());
        Assert.assertFalse(result.getMuteAllGuests());
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
        Assert.assertNotNull(result);
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
        Assert.assertNotNull(result);
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
}
