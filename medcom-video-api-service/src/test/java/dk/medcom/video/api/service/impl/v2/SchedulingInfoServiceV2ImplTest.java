package dk.medcom.video.api.service.impl.v2;

import dk.medcom.video.api.controller.exceptions.*;
import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.dao.entity.ViewType;
import dk.medcom.video.api.dao.entity.VmrQuality;
import dk.medcom.video.api.dao.entity.VmrType;
import dk.medcom.video.api.service.SchedulingInfoService;
import dk.medcom.video.api.service.SchedulingInfoServiceV2;
import dk.medcom.video.api.service.SchedulingInfoServiceV2Impl;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.NotValidDataExceptionV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import dk.medcom.video.api.service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.model.DetailedError;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static dk.medcom.video.api.service.impl.v2.HelperMethods.*;
import static org.junit.jupiter.api.Assertions.*;

public class SchedulingInfoServiceV2ImplTest {

    private SchedulingInfoServiceV2 schedulingInfoServiceV2;

    private SchedulingInfoService schedulingInfoService;
    private final String shortLinkBaseUrl = "base.url";

    @BeforeEach
    public void setup() {
        schedulingInfoService = Mockito.mock(SchedulingInfoService.class);

        schedulingInfoServiceV2 = new SchedulingInfoServiceV2Impl(schedulingInfoService, shortLinkBaseUrl);
    }
    
    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(schedulingInfoService);
    }

    @Test
    public void testGetSchedulingInfoV2() {
        var fromStartTime = OffsetDateTime.now();
        var toEndTime = OffsetDateTime.now();
        var schedulingInfos = List.of(randomSchedulingInfo(), randomSchedulingInfo());

        Mockito.when(schedulingInfoService.getSchedulingInfo(Date.from(fromStartTime.toInstant()), Date.from(toEndTime.toInstant()), ProvisionStatus.PROVISION_PROBLEMS)).thenReturn(schedulingInfos);
        var result = schedulingInfoServiceV2.getSchedulingInfoV2(fromStartTime, toEndTime, ProvisionStatusModel.PROVISION_PROBLEMS);
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(schedulingInfos.getFirst().getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(schedulingInfos.getLast().getUuid())).findFirst().orElseThrow();

        assertSchedulingInfo(schedulingInfos.getFirst(), shortLinkBaseUrl, res1);
        assertSchedulingInfo(schedulingInfos.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(schedulingInfoService).getSchedulingInfo(Date.from(fromStartTime.toInstant()), Date.from(toEndTime.toInstant()), ProvisionStatus.PROVISION_PROBLEMS);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingInfoAwaitsProvisionV2() {
        var schedulingInfos = List.of(randomSchedulingInfo(), randomSchedulingInfo());

        Mockito.when(schedulingInfoService.getSchedulingInfoAwaitsProvision()).thenReturn(schedulingInfos);

        var result = schedulingInfoServiceV2.getSchedulingInfoAwaitsProvisionV2();
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(schedulingInfos.getFirst().getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(schedulingInfos.getLast().getUuid())).findFirst().orElseThrow();

        assertSchedulingInfo(schedulingInfos.getFirst(), shortLinkBaseUrl, res1);
        assertSchedulingInfo(schedulingInfos.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(schedulingInfoService).getSchedulingInfoAwaitsProvision();
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingInfoAwaitsProvisionV2NoSchedulingInfo() {
        Mockito.when(schedulingInfoService.getSchedulingInfoAwaitsProvision()).thenReturn(List.of());

        var result = schedulingInfoServiceV2.getSchedulingInfoAwaitsProvisionV2();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(schedulingInfoService).getSchedulingInfoAwaitsProvision();
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingInfoAwaitsDeProvisionV2() {
        var schedulingInfos = List.of(randomSchedulingInfo(), randomSchedulingInfo());

        Mockito.when(schedulingInfoService.getSchedulingInfoAwaitsDeProvision()).thenReturn(schedulingInfos);

        var result = schedulingInfoServiceV2.getSchedulingInfoAwaitsDeProvisionV2();
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.uuid().toString().equals(schedulingInfos.getFirst().getUuid())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.uuid().toString().equals(schedulingInfos.getLast().getUuid())).findFirst().orElseThrow();

        assertSchedulingInfo(schedulingInfos.getFirst(), shortLinkBaseUrl, res1);
        assertSchedulingInfo(schedulingInfos.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(schedulingInfoService).getSchedulingInfoAwaitsDeProvision();
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingInfoAwaitsDeProvisionV2NoSchedulingInfo() {
        Mockito.when(schedulingInfoService.getSchedulingInfoAwaitsDeProvision()).thenReturn(List.of());

        var result = schedulingInfoServiceV2.getSchedulingInfoAwaitsDeProvisionV2();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(schedulingInfoService).getSchedulingInfoAwaitsDeProvision();
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingInfoByUuidV2() throws RessourceNotFoundException {
        var uuid = UUID.randomUUID();
        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.getSchedulingInfoByUuid(uuid.toString())).thenReturn(schedulingInfo);
        var result = schedulingInfoServiceV2.getSchedulingInfoByUuidV2(uuid);
        assertNotNull(result);

        assertSchedulingInfo(schedulingInfo, shortLinkBaseUrl, result);

        Mockito.verify(schedulingInfoService).getSchedulingInfoByUuid(uuid.toString());
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingInfoByUuidV2ResourceNotFound() throws RessourceNotFoundException {
        var uuid = UUID.randomUUID();

        Mockito.when(schedulingInfoService.getSchedulingInfoByUuid(uuid.toString())).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> schedulingInfoServiceV2.getSchedulingInfoByUuidV2(uuid));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(schedulingInfoService).getSchedulingInfoByUuid(uuid.toString());
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateSchedulingInfoV2() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var input = new CreateSchedulingInfoModel(randomString(), 123L);
        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.createSchedulingInfo(Mockito.any())).thenReturn(schedulingInfo);
        var result = schedulingInfoServiceV2.createSchedulingInfoV2(input);
        assertNotNull(result);

        assertSchedulingInfo(schedulingInfo, shortLinkBaseUrl, result);

        Mockito.verify(schedulingInfoService).createSchedulingInfo(Mockito.argThat(x -> {
            assertEquals(input.schedulingTemplateId(), x.getSchedulingTemplateId());
            assertEquals(input.organizationId(), x.getOrganizationId());
            return true;
        }));
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateSchedulingInfoV2NotValidData() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var input = new CreateSchedulingInfoModel(randomString(), 123L);

        Mockito.when(schedulingInfoService.createSchedulingInfo(Mockito.any())).thenThrow(new NotValidDataException(NotValidDataErrors.SCHEDULING_INFO_CAN_NOT_BE_CREATED, "message"));

        var expectedException = assertThrows(NotValidDataExceptionV2.class, () -> schedulingInfoServiceV2.createSchedulingInfoV2(input));
        assertNotNull(expectedException);
        assertEquals("Scheduling information can not be created on organisation message that is not pool enabled.", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._23, expectedException.getDetailedErrorCode());

        Mockito.verify(schedulingInfoService).createSchedulingInfo(Mockito.argThat(x -> {
            assertEquals(input.schedulingTemplateId(), x.getSchedulingTemplateId());
            assertEquals(input.organizationId(), x.getOrganizationId());
            return true;
        }));
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateSchedulingInfoV2NotAcceptable() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var input = new CreateSchedulingInfoModel(randomString(), 123L);

        Mockito.when(schedulingInfoService.createSchedulingInfo(Mockito.any())).thenThrow(new NotAcceptableException(NotAcceptableErrors.URI_ASSIGNMENT_FAILED_NOT_POSSIBLE_TO_CREATE_UNIQUE));

        var expectedException = assertThrows(NotAcceptableExceptionV2.class, () -> schedulingInfoServiceV2.createSchedulingInfoV2(input));
        assertNotNull(expectedException);
        assertEquals("The Uri assignment failed. It was not possible to create a unique. Consider changing the interval on the template", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._15, expectedException.getDetailedErrorCode());

        Mockito.verify(schedulingInfoService).createSchedulingInfo(Mockito.argThat(x -> {
            assertEquals(input.schedulingTemplateId(), x.getSchedulingTemplateId());
            assertEquals(input.organizationId(), x.getOrganizationId());
            return true;
        }));
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateSchedulingInfoV2PermissionDenied() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var input = new CreateSchedulingInfoModel(randomString(), 123L);

        Mockito.when(schedulingInfoService.createSchedulingInfo(Mockito.any())).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> schedulingInfoServiceV2.createSchedulingInfoV2(input));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(schedulingInfoService).createSchedulingInfo(Mockito.argThat(x -> {
            assertEquals(input.schedulingTemplateId(), x.getSchedulingTemplateId());
            assertEquals(input.organizationId(), x.getOrganizationId());
            return true;
        }));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateSchedulingInfoV2() throws RessourceNotFoundException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = new UpdateSchedulingInfoModel(ProvisionStatusModel.STARTING_TO_PROVISION, randomString(), randomString());
        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.updateSchedulingInfo(Mockito.eq(uuid.toString()), Mockito.any())).thenReturn(schedulingInfo);

        var result = schedulingInfoServiceV2.updateSchedulingInfoV2(uuid, input);
        assertNotNull(result);

        assertSchedulingInfo(schedulingInfo, shortLinkBaseUrl, result);

        Mockito.verify(schedulingInfoService).updateSchedulingInfo(Mockito.eq(uuid.toString()), Mockito.argThat(x -> {
            assertEquals(ProvisionStatus.STARTING_TO_PROVISION, x.getProvisionStatus());
            assertEquals(input.provisionStatusDescription(), x.getProvisionStatusDescription());
            assertEquals(input.provisionVmrId(), x.getProvisionVmrId());
            return true;
        }));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateSchedulingInfoV2ResourceNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = new UpdateSchedulingInfoModel(ProvisionStatusModel.PROVISIONED_OK, randomString(), randomString());

        Mockito.when(schedulingInfoService.updateSchedulingInfo(Mockito.eq(uuid.toString()), Mockito.any())).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> schedulingInfoServiceV2.updateSchedulingInfoV2(uuid, input));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(schedulingInfoService).updateSchedulingInfo(Mockito.eq(uuid.toString()), Mockito.argThat(x -> {
            assertEquals(ProvisionStatus.PROVISIONED_OK, x.getProvisionStatus());
            assertEquals(input.provisionStatusDescription(), x.getProvisionStatusDescription());
            assertEquals(input.provisionVmrId(), x.getProvisionVmrId());
            return true;
        }));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateSchedulingInfoV2PermissionDenied() throws RessourceNotFoundException, PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var input = new UpdateSchedulingInfoModel(ProvisionStatusModel.AWAITS_PROVISION, randomString(), randomString());

        Mockito.when(schedulingInfoService.updateSchedulingInfo(Mockito.eq(uuid.toString()), Mockito.any())).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> schedulingInfoServiceV2.updateSchedulingInfoV2(uuid, input));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(schedulingInfoService).updateSchedulingInfo(Mockito.eq(uuid.toString()), Mockito.argThat(x -> {
            assertEquals(ProvisionStatus.AWAITS_PROVISION, x.getProvisionStatus());
            assertEquals(input.provisionStatusDescription(), x.getProvisionStatusDescription());
            assertEquals(input.provisionVmrId(), x.getProvisionVmrId());
            return true;
        }));
        verifyNoMoreInteractions();
    }

    @Test
    public void testReserveSchedulingInfoV2() throws RessourceNotFoundException {
        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.reserveSchedulingInfo(
                VmrType.lecture,
                ViewType.one_main_twentyone_pips,
                ViewType.five_mains_seven_pips,
                VmrQuality.hd,
                true,
                false,
                true,
                false,
                true
        )).thenReturn(schedulingInfo);

        var result = schedulingInfoServiceV2.reserveSchedulingInfoV2(
                VmrTypeModel.lecture,
                ViewTypeModel.one_main_twentyone_pips,
                ViewTypeModel.five_mains_seven_pips,
                VmrQualityModel.hd,
                true,
                false,
                true,
                false,
                true);
        assertNotNull(result);

        assertSchedulingInfo(schedulingInfo, shortLinkBaseUrl, result);

        Mockito.verify(schedulingInfoService).reserveSchedulingInfo(
                VmrType.lecture,
                ViewType.one_main_twentyone_pips,
                ViewType.five_mains_seven_pips,
                VmrQuality.hd,
                true,
                false,
                true,
                false,
                true);
        verifyNoMoreInteractions();
    }

    @Test
    public void testReserveSchedulingInfoV2ResourceNotFound() throws RessourceNotFoundException {
        Mockito.when(schedulingInfoService.reserveSchedulingInfo(
                VmrType.lecture,
                ViewType.one_main_twentyone_pips,
                ViewType.five_mains_seven_pips,
                VmrQuality.hd,
                true,
                false,
                true,
                false,
                true)).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> schedulingInfoServiceV2.reserveSchedulingInfoV2(
                VmrTypeModel.lecture,
                ViewTypeModel.one_main_twentyone_pips,
                ViewTypeModel.five_mains_seven_pips,
                VmrQualityModel.hd,
                true,
                false,
                true,
                false,
                true));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(schedulingInfoService).reserveSchedulingInfo(
                VmrType.lecture,
                ViewType.one_main_twentyone_pips,
                ViewType.five_mains_seven_pips,
                VmrQuality.hd,
                true,
                false,
                true,
                false,
                true);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingInfoByReservationV2() throws RessourceNotFoundException {
        var uuid = UUID.randomUUID();
        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.getSchedulingInfoByReservation(uuid)).thenReturn(schedulingInfo);

        var result = schedulingInfoServiceV2.getSchedulingInfoByReservationV2(uuid);
        assertNotNull(result);

        assertSchedulingInfo(schedulingInfo, shortLinkBaseUrl, result);

        Mockito.verify(schedulingInfoService).getSchedulingInfoByReservation(uuid);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingInfoByReservationV2ResourceNotFound() throws RessourceNotFoundException {
        var uuid = UUID.randomUUID();

        Mockito.when(schedulingInfoService.getSchedulingInfoByReservation(uuid)).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> schedulingInfoServiceV2.getSchedulingInfoByReservationV2(uuid));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(schedulingInfoService).getSchedulingInfoByReservation(uuid);
        verifyNoMoreInteractions();
    }
}
