package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.controller.v2.exception.NotAcceptableException;
import dk.medcom.video.api.controller.v2.exception.NotValidDataException;
import dk.medcom.video.api.controller.v2.exception.PermissionDeniedException;
import dk.medcom.video.api.controller.v2.exception.ResourceNotFoundException;
import dk.medcom.video.api.service.SchedulingInfoServiceV2;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.NotValidDataExceptionV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import dk.medcom.video.api.service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.model.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static dk.medcom.video.api.controller.v2.HelperMethods.*;
import static org.junit.jupiter.api.Assertions.*;

public class VideoSchedulingInformationControllerV2Test {

    private VideoSchedulingInformationControllerV2 videoSchedulingInformationControllerV2;
    private SchedulingInfoServiceV2 schedulingInfoService;

    @BeforeEach
    public void setup() {
        schedulingInfoService = Mockito.mock(SchedulingInfoServiceV2.class);

        videoSchedulingInformationControllerV2 = new VideoSchedulingInformationControllerV2(schedulingInfoService);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(schedulingInfoService);
    }

    @Test
    public void testV2SchedulingInfoDeprovisionGet() {
        var schedulingInfoList = List.of(randomSchedulingInfo(), randomSchedulingInfo(), randomSchedulingInfo());
        Mockito.when(schedulingInfoService.getSchedulingInfoAwaitsDeProvisionV2()).thenReturn(schedulingInfoList);

        var result = videoSchedulingInformationControllerV2.v2SchedulingInfoDeprovisionGet();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(3, result.getBody().size());
        var res1 = result.getBody().stream().filter(x -> x.getUuid() == schedulingInfoList.getFirst().uuid()).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getUuid() == schedulingInfoList.get(1).uuid()).findFirst().orElseThrow();
        var res3 = result.getBody().stream().filter(x -> x.getUuid() == schedulingInfoList.getLast().uuid()).findFirst().orElseThrow();

        assertSchedulingInfo(schedulingInfoList.getFirst(), res1);
        assertSchedulingInfo(schedulingInfoList.get(1), res2);
        assertSchedulingInfo(schedulingInfoList.getLast(), res3);

        Mockito.verify(schedulingInfoService).getSchedulingInfoAwaitsDeProvisionV2();
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoGet() {
        var fromDate = OffsetDateTime.now().minusHours(5);
        var toDate = OffsetDateTime.now();

        var schedulingInfoList = List.of(randomSchedulingInfo(), randomSchedulingInfo());
        Mockito.when(schedulingInfoService.getSchedulingInfoV2(fromDate, toDate, ProvisionStatusModel.AWAITS_PROVISION)).thenReturn(schedulingInfoList);

        var result = videoSchedulingInformationControllerV2.v2SchedulingInfoGet(fromDate, toDate, ProvisionStatus.AWAITS_PROVISION);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        var res1 = result.getBody().stream().filter(x -> x.getUuid() == schedulingInfoList.getFirst().uuid()).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getUuid() == schedulingInfoList.getLast().uuid()).findFirst().orElseThrow();

        assertSchedulingInfo(schedulingInfoList.getFirst(), res1);
        assertSchedulingInfo(schedulingInfoList.getLast(), res2);

        Mockito.verify(schedulingInfoService).getSchedulingInfoV2(fromDate, toDate, ProvisionStatusModel.AWAITS_PROVISION);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoPost() {
        var input = randomCreateSchedulingInfoInput();

        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.createSchedulingInfoV2(new CreateSchedulingInfoModel(input.getOrganizationId(), input.getSchedulingTemplateId())))
                .thenReturn(schedulingInfo);

        var result = videoSchedulingInformationControllerV2.v2SchedulingInfoPost(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingInfo(schedulingInfo, result.getBody());

        Mockito.verify(schedulingInfoService).createSchedulingInfoV2(new CreateSchedulingInfoModel(input.getOrganizationId(), input.getSchedulingTemplateId()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoPostPermissionDenied() {
        var input = randomCreateSchedulingInfoInput();

        Mockito.when(schedulingInfoService.createSchedulingInfoV2(new CreateSchedulingInfoModel(input.getOrganizationId(), input.getSchedulingTemplateId())))
                .thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoSchedulingInformationControllerV2.v2SchedulingInfoPost(input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(schedulingInfoService).createSchedulingInfoV2(new CreateSchedulingInfoModel(input.getOrganizationId(), input.getSchedulingTemplateId()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoPostNotValidData() {
        var input = randomCreateSchedulingInfoInput();

        Mockito.when(schedulingInfoService.createSchedulingInfoV2(new CreateSchedulingInfoModel(input.getOrganizationId(), input.getSchedulingTemplateId())))
                .thenThrow(new NotValidDataExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotValidDataException.class, () -> videoSchedulingInformationControllerV2.v2SchedulingInfoPost(input));
        assertNotNull(expectedException);
        assertEquals(400, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(schedulingInfoService).createSchedulingInfoV2(new CreateSchedulingInfoModel(input.getOrganizationId(), input.getSchedulingTemplateId()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoPostNotAcceptable() {
        var input = randomCreateSchedulingInfoInput();

        Mockito.when(schedulingInfoService.createSchedulingInfoV2(new CreateSchedulingInfoModel(input.getOrganizationId(), input.getSchedulingTemplateId())))
                .thenThrow(new NotAcceptableExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotAcceptableException.class, () -> videoSchedulingInformationControllerV2.v2SchedulingInfoPost(input));
        assertNotNull(expectedException);
        assertEquals(406, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(schedulingInfoService).createSchedulingInfoV2(new CreateSchedulingInfoModel(input.getOrganizationId(), input.getSchedulingTemplateId()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoProvisionGet() {
        var schedulingInfoList = List.of(randomSchedulingInfo(), randomSchedulingInfo(), randomSchedulingInfo());
        Mockito.when(schedulingInfoService.getSchedulingInfoAwaitsProvisionV2()).thenReturn(schedulingInfoList);

        var result = videoSchedulingInformationControllerV2.v2SchedulingInfoProvisionGet();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(3, result.getBody().size());
        var res1 = result.getBody().stream().filter(x -> x.getUuid() == schedulingInfoList.getFirst().uuid()).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getUuid() == schedulingInfoList.get(1).uuid()).findFirst().orElseThrow();
        var res3 = result.getBody().stream().filter(x -> x.getUuid() == schedulingInfoList.getLast().uuid()).findFirst().orElseThrow();

        assertSchedulingInfo(schedulingInfoList.getFirst(), res1);
        assertSchedulingInfo(schedulingInfoList.get(1), res2);
        assertSchedulingInfo(schedulingInfoList.getLast(), res3);

        Mockito.verify(schedulingInfoService).getSchedulingInfoAwaitsProvisionV2();
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoReserveGet() {
        var schedulingInfo = randomSchedulingInfo();
        Mockito.when(schedulingInfoService.reserveSchedulingInfoV2(VmrTypeModel.lecture,
                ViewTypeModel.one_main_twentyone_pips,
                ViewTypeModel.five_mains_seven_pips,
                VmrQualityModel.hd,
                true,
                false,
                true,
                false,
                true)).thenReturn(schedulingInfo);

        var result = videoSchedulingInformationControllerV2.v2SchedulingInfoReserveGet(
                VmrType.LECTURE,
                ViewType.ONE_MAIN_TWENTYONE_PIPS,
                ViewType.FIVE_MAINS_SEVEN_PIPS,
                VmrQuality.HD,
                true,
                false,
                true,
                false,
                true);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingInfo(schedulingInfo, result.getBody());

        Mockito.verify(schedulingInfoService).reserveSchedulingInfoV2(VmrTypeModel.lecture,
                ViewTypeModel.one_main_twentyone_pips,
                ViewTypeModel.five_mains_seven_pips,
                VmrQualityModel.hd,
                true,
                false,
                true,
                false,
                true);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoReserveGetResourceNotFound() {
        Mockito.when(schedulingInfoService.reserveSchedulingInfoV2(VmrTypeModel.lecture,
                ViewTypeModel.one_main_twentyone_pips,
                ViewTypeModel.five_mains_seven_pips,
                VmrQualityModel.hd,
                true,
                false,
                true,
                false,
                true)).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoSchedulingInformationControllerV2.v2SchedulingInfoReserveGet(
                VmrType.LECTURE,
                ViewType.ONE_MAIN_TWENTYONE_PIPS,
                ViewType.FIVE_MAINS_SEVEN_PIPS,
                VmrQuality.HD,
                true,
                false,
                true,
                false,
                true));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(schedulingInfoService).reserveSchedulingInfoV2(VmrTypeModel.lecture,
                ViewTypeModel.one_main_twentyone_pips,
                ViewTypeModel.five_mains_seven_pips,
                VmrQualityModel.hd,
                true,
                false,
                true,
                false,
                true);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoReserveUuidGet() {
        var uuid = UUID.randomUUID();
        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.getSchedulingInfoByReservationV2(uuid)).thenReturn(schedulingInfo);

        var result = videoSchedulingInformationControllerV2.v2SchedulingInfoReserveUuidGet(uuid);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingInfo(schedulingInfo, result.getBody());

        Mockito.verify(schedulingInfoService).getSchedulingInfoByReservationV2(uuid);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoReserveUuidGetResourceNotFound() {
        var uuid = UUID.randomUUID();

        Mockito.when(schedulingInfoService.getSchedulingInfoByReservationV2(uuid)).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoSchedulingInformationControllerV2.v2SchedulingInfoReserveUuidGet(uuid));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(schedulingInfoService).getSchedulingInfoByReservationV2(uuid);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoUuidGet() {
        var uuid = UUID.randomUUID();
        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.getSchedulingInfoByUuidV2(uuid)).thenReturn(schedulingInfo);

        var result = videoSchedulingInformationControllerV2.v2SchedulingInfoUuidGet(uuid);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingInfo(schedulingInfo, result.getBody());

        Mockito.verify(schedulingInfoService).getSchedulingInfoByUuidV2(uuid);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoUuidGetResourceNotFound() {
        var uuid = UUID.randomUUID();

        Mockito.when(schedulingInfoService.getSchedulingInfoByUuidV2(uuid)).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoSchedulingInformationControllerV2.v2SchedulingInfoUuidGet(uuid));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(schedulingInfoService).getSchedulingInfoByUuidV2(uuid);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoUuidPut() {
        var uuid = UUID.randomUUID();
        var input = randomUpdateSchedulingInfoInput();
        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.updateSchedulingInfoV2(uuid, new UpdateSchedulingInfoModel(ProvisionStatusModel.PROVISIONED_OK,
                input.getProvisionStatusDescription(), input.getProvisionVmrId()))).thenReturn(schedulingInfo);

        var result = videoSchedulingInformationControllerV2.v2SchedulingInfoUuidPut(uuid, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingInfo(schedulingInfo, result.getBody());

        Mockito.verify(schedulingInfoService).updateSchedulingInfoV2(uuid, new UpdateSchedulingInfoModel(ProvisionStatusModel.PROVISIONED_OK,
                input.getProvisionStatusDescription(), input.getProvisionVmrId()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoUuidPutOnlyRequiredValues() {
        var uuid = UUID.randomUUID();
        var input = new UpdateSchedulingInfo()
                .provisionStatus(ProvisionStatus.PROVISIONED_OK);
        var schedulingInfo = randomSchedulingInfo();

        Mockito.when(schedulingInfoService.updateSchedulingInfoV2(uuid, new UpdateSchedulingInfoModel(ProvisionStatusModel.PROVISIONED_OK,
                input.getProvisionStatusDescription(), input.getProvisionVmrId()))).thenReturn(schedulingInfo);

        var result = videoSchedulingInformationControllerV2.v2SchedulingInfoUuidPut(uuid, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingInfo(schedulingInfo, result.getBody());

        Mockito.verify(schedulingInfoService).updateSchedulingInfoV2(uuid, new UpdateSchedulingInfoModel(ProvisionStatusModel.PROVISIONED_OK,
                input.getProvisionStatusDescription(), input.getProvisionVmrId()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoUuidPutResourceNotFound() {
        var uuid = UUID.randomUUID();
        var input = randomUpdateSchedulingInfoInput();

        Mockito.when(schedulingInfoService.updateSchedulingInfoV2(uuid, new UpdateSchedulingInfoModel(ProvisionStatusModel.PROVISIONED_OK,
                input.getProvisionStatusDescription(), input.getProvisionVmrId()))).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> videoSchedulingInformationControllerV2.v2SchedulingInfoUuidPut(uuid, input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(schedulingInfoService).updateSchedulingInfoV2(uuid, new UpdateSchedulingInfoModel(ProvisionStatusModel.PROVISIONED_OK,
                input.getProvisionStatusDescription(), input.getProvisionVmrId()));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingInfoUuidPutPermissionDenied() {
        var uuid = UUID.randomUUID();
        var input = randomUpdateSchedulingInfoInput();

        Mockito.when(schedulingInfoService.updateSchedulingInfoV2(uuid, new UpdateSchedulingInfoModel(ProvisionStatusModel.PROVISIONED_OK,
                input.getProvisionStatusDescription(), input.getProvisionVmrId()))).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> videoSchedulingInformationControllerV2.v2SchedulingInfoUuidPut(uuid, input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(schedulingInfoService).updateSchedulingInfoV2(uuid, new UpdateSchedulingInfoModel(ProvisionStatusModel.PROVISIONED_OK,
                input.getProvisionStatusDescription(), input.getProvisionVmrId()));
        verifyNoMoreInteractions();
    }
}
