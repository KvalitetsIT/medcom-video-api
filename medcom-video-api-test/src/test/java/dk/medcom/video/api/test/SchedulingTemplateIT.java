package dk.medcom.video.api.test;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SchedulingTemplateAdministrationApi;
import io.swagger.client.model.*;
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
                .setBasePath(String.format("http://%s:%s/api", videoApi.getContainerIpAddress(), videoApiPort))
                .setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

        schedulingTemplate = new SchedulingTemplateAdministrationApi(apiClient);
    }

    @Test
    public void testCreateSchedulingTemplate_UseDefaultValues() throws ApiException {
        //Given
        CreateSchedulingTemplate create = new CreateSchedulingTemplate();
        create.setConferencingSysId(43);
        create.setUriPrefix("43");
        create.setUriDomain("test.dk");
        create.setHostPinRequired(true);
        create.setGuestPinRequired(true);
        create.setUriNumberRangeLow(1);
        create.setUriNumberRangeHigh(100);
        create.setCustomPortalGuest("some_portal_guest");
        create.setCustomPortalHost("some_portal_host");
        create.setReturnUrl("return_url");
        create.setGuestView(ViewType.FIVE_MAINS_SEVEN_PIPS);

        //When
        SchedulingTemplate resultCreate = schedulingTemplate.schedulingTemplatesPost(create);
        SchedulingTemplate result = this.schedulingTemplate.schedulingTemplatesIdGet(resultCreate.getId());

        //Then
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getVmrType());
        Assert.assertNotNull(result.getHostView());
        Assert.assertNotNull(result.getGuestView());
        Assert.assertNotNull(result.getVmrQuality());
        Assert.assertTrue(result.isEnableOverlayText());
        Assert.assertTrue(result.isGuestsCanPresent());
        Assert.assertTrue(result.isForcePresenterIntoMain());
        Assert.assertFalse(result.isForceEncryption());
        Assert.assertFalse(result.isMuteAllGuests());
        assertEquals(create.getCustomPortalGuest(), result.getCustomPortalGuest());
        assertEquals(create.getCustomPortalHost(), result.getCustomPortalHost());
        assertEquals(create.getReturnUrl(), result.getReturnUrl());
        assertEquals(create.getGuestView(), result.getGuestView());
        assertFalse(create.isIsPoolTemplate());
        assertEquals(DirectMedia.NEVER, result.getDirectMedia());
    }

    @Test
    public void testCreateSchedulingTemplate_OverwriteDefaultValues() throws ApiException {
        //Given
        CreateSchedulingTemplate create = new CreateSchedulingTemplate();
        create.setConferencingSysId(43);
        create.setUriPrefix("43");
        create.setUriDomain("test.dk");
        create.setHostPinRequired(true);
        create.setGuestPinRequired(true);
        create.setUriNumberRangeLow(1);
        create.setUriNumberRangeHigh(100);
        create.setVmrType(VmrType.LECTURE);
        create.setHostView(ViewType.ONE_MAIN_SEVEN_PIPS);
        create.setEnableOverlayText(false);
        create.setDirectMedia(DirectMedia.BEST_EFFORT);

        //When
        SchedulingTemplate resultCreate = schedulingTemplate.schedulingTemplatesPost(create);
        SchedulingTemplate result = this.schedulingTemplate.schedulingTemplatesIdGet(resultCreate.getId());

        //Then
        Assert.assertNotNull(result);
        assertEquals(create.getVmrType().toString(), result.getVmrType().toString());
        assertEquals(create.getHostView().toString(), result.getHostView().toString());
        assertEquals(create.isEnableOverlayText(), result.isEnableOverlayText());
        assertEquals(create.getDirectMedia(), result.getDirectMedia());
    }

    @Test
    public void testUpdateSchedulingTemplate() throws ApiException {
        //Given
        CreateSchedulingTemplate create = new CreateSchedulingTemplate();
        create.setConferencingSysId(43);
        create.setUriPrefix("43");
        create.setUriDomain("test.dk");
        create.setHostPinRequired(true);
        create.setGuestPinRequired(true);
        create.setUriNumberRangeLow(1);
        create.setUriNumberRangeHigh(100);

        UpdateSchedulingTemplate updateSchedulingTemplate = new UpdateSchedulingTemplate();
        updateSchedulingTemplate.setConferencingSysId(create.getConferencingSysId());
        updateSchedulingTemplate.setUriPrefix(create.getUriPrefix());
        updateSchedulingTemplate.setUriDomain(create.getUriDomain());
        updateSchedulingTemplate.setHostPinRequired(create.isHostPinRequired());
        updateSchedulingTemplate.setGuestPinRequired(create.isGuestPinRequired());
        updateSchedulingTemplate.setUriNumberRangeLow(create.getUriNumberRangeLow());
        updateSchedulingTemplate.setUriNumberRangeHigh(create.getUriNumberRangeHigh());
        updateSchedulingTemplate.setVmrType(VmrType.LECTURE);
        updateSchedulingTemplate.setHostView(ViewType.TWO_MAINS_TWENTYONE_PIPS);
        updateSchedulingTemplate.setEnableOverlayText(false);
        updateSchedulingTemplate.setCustomPortalGuest(UUID.randomUUID().toString());
        updateSchedulingTemplate.setCustomPortalHost(UUID.randomUUID().toString());
        updateSchedulingTemplate.setReturnUrl(UUID.randomUUID().toString());
        updateSchedulingTemplate.setDirectMedia(DirectMedia.BEST_EFFORT);

        //When
        SchedulingTemplate resultCreate = schedulingTemplate.schedulingTemplatesPost(create);
        schedulingTemplate.schedulingTemplatesIdPut(updateSchedulingTemplate, resultCreate.getId());
        SchedulingTemplate result = schedulingTemplate.schedulingTemplatesIdGet(resultCreate.getId());

        //Then
        Assert.assertNotNull(result);
        assertEquals(updateSchedulingTemplate.getVmrType().toString(), result.getVmrType().toString());
        assertEquals(updateSchedulingTemplate.getHostView().toString(), result.getHostView().toString());
        assertEquals(updateSchedulingTemplate.isEnableOverlayText(), result.isEnableOverlayText());
        assertEquals(updateSchedulingTemplate.getCustomPortalGuest(), result.getCustomPortalGuest());
        assertEquals(updateSchedulingTemplate.getCustomPortalHost(), result.getCustomPortalHost());
        assertEquals(updateSchedulingTemplate.getReturnUrl(), result.getReturnUrl());
        assertFalse(result.isIsPoolTemplate());
        assertEquals(DirectMedia.BEST_EFFORT, result.getDirectMedia());
    }

    @Test
    public void testOnlyOnePoolTemplate() throws ApiException {
        CreateSchedulingTemplate createOne = new CreateSchedulingTemplate();
        createOne.setConferencingSysId(43);
        createOne.setUriPrefix("43");
        createOne.setUriDomain("test.dk");
        createOne.setHostPinRequired(true);
        createOne.setGuestPinRequired(true);
        createOne.setUriNumberRangeLow(1);
        createOne.setUriNumberRangeHigh(100);
        createOne.setIsPoolTemplate(true);

        CreateSchedulingTemplate createTwo = new CreateSchedulingTemplate();
        createTwo.setConferencingSysId(43);
        createTwo.setUriPrefix("43");
        createTwo.setUriDomain("test.dk");
        createTwo.setHostPinRequired(true);
        createTwo.setGuestPinRequired(true);
        createTwo.setUriNumberRangeLow(1);
        createTwo.setUriNumberRangeHigh(100);
        createTwo.setIsPoolTemplate(true);

        CreateSchedulingTemplate createThree = new CreateSchedulingTemplate();
        createThree.setConferencingSysId(43);
        createThree.setUriPrefix("43");
        createThree.setUriDomain("test.dk");
        createThree.setHostPinRequired(true);
        createThree.setGuestPinRequired(true);
        createThree.setUriNumberRangeLow(1);
        createThree.setUriNumberRangeHigh(100);

        SchedulingTemplate resultCreateOne = schedulingTemplate.schedulingTemplatesPost(createOne);
        SchedulingTemplate resultOne = this.schedulingTemplate.schedulingTemplatesIdGet(resultCreateOne.getId());
        assertNotNull(resultOne);
        assertTrue(resultOne.isIsPoolTemplate());

        var expectedExceptionCreate = assertThrows(ApiException.class, () -> schedulingTemplate.schedulingTemplatesPost(createTwo));
        assertEquals(406, expectedExceptionCreate.getCode());
        assertTrue(expectedExceptionCreate.getResponseBody().contains("Create or update of pool template failed due to only one pool template allowed"));

        SchedulingTemplate resultCreateThree = schedulingTemplate.schedulingTemplatesPost(createThree);
        SchedulingTemplate resultThree = this.schedulingTemplate.schedulingTemplatesIdGet(resultCreateThree.getId());
        assertNotNull(resultThree);
        assertFalse(resultThree.isIsPoolTemplate());

        UpdateSchedulingTemplate updateSchedulingTemplateThree = new UpdateSchedulingTemplate();
        updateSchedulingTemplateThree.setConferencingSysId(createThree.getConferencingSysId());
        updateSchedulingTemplateThree.setUriPrefix(createThree.getUriPrefix());
        updateSchedulingTemplateThree.setUriDomain(createThree.getUriDomain());
        updateSchedulingTemplateThree.setHostPinRequired(createThree.isHostPinRequired());
        updateSchedulingTemplateThree.setGuestPinRequired(createThree.isGuestPinRequired());
        updateSchedulingTemplateThree.setUriNumberRangeLow(createThree.getUriNumberRangeLow());
        updateSchedulingTemplateThree.setUriNumberRangeHigh(createThree.getUriNumberRangeHigh());
        updateSchedulingTemplateThree.setIsPoolTemplate(true);

        var expectedExceptionUpdate = assertThrows(ApiException.class, () -> schedulingTemplate.schedulingTemplatesIdPut(updateSchedulingTemplateThree, resultThree.getId()));
        assertEquals(406, expectedExceptionUpdate.getCode());
        assertTrue(expectedExceptionUpdate.getResponseBody().contains("Create or update of pool template failed due to only one pool template allowed"));
    }
}
