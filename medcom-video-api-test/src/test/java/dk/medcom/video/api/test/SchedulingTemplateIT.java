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

import static org.junit.Assert.assertEquals;

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

        //When
        SchedulingTemplate resultCreate = schedulingTemplate.schedulingTemplatesPost(create);
        SchedulingTemplate result = this.schedulingTemplate.schedulingTemplatesIdGet(resultCreate.getId());

        //Then
        Assert.assertNotNull(result);
        assertEquals(create.getVmrType().toString(), result.getVmrType().toString());
        assertEquals(create.getHostView().toString(), result.getHostView().toString());
        assertEquals(create.isEnableOverlayText(), result.isEnableOverlayText());
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
    }
}
