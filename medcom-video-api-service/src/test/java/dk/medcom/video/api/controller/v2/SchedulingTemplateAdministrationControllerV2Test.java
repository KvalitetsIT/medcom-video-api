package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.controller.v2.exception.NotAcceptableException;
import dk.medcom.video.api.controller.v2.exception.PermissionDeniedException;
import dk.medcom.video.api.controller.v2.exception.ResourceNotFoundException;
import dk.medcom.video.api.service.SchedulingTemplateServiceV2;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.model.*;

import java.util.List;

import static dk.medcom.video.api.controller.v2.HelperMethods.*;
import static org.junit.jupiter.api.Assertions.*;

public class SchedulingTemplateAdministrationControllerV2Test {

    private SchedulingTemplateAdministrationControllerV2 schedulingTemplateAdministrationControllerV2;
    private SchedulingTemplateServiceV2 schedulingTemplateService;

    @BeforeEach
    public void setup() {
        schedulingTemplateService = Mockito.mock(SchedulingTemplateServiceV2.class);

        schedulingTemplateAdministrationControllerV2 = new SchedulingTemplateAdministrationControllerV2(schedulingTemplateService);
    }
    
    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(schedulingTemplateService);
    }

    @Test
    public void testV2SchedulingTemplatesGet() {
        var schedulingTemplates = List.of(randomSchedulingTemplate(), randomSchedulingTemplate());
        Mockito.when(schedulingTemplateService.getSchedulingTemplatesV2()).thenReturn(schedulingTemplates);

        var result = schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesGet();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        var res1 = result.getBody().stream().filter(x -> x.getOrganisationId().equals(schedulingTemplates.getFirst().organisationId())).findFirst().orElseThrow();
        var res2 = result.getBody().stream().filter(x -> x.getOrganisationId().equals(schedulingTemplates.getLast().organisationId())).findFirst().orElseThrow();

        assertSchedulingTemplate(schedulingTemplates.getFirst(), res1);
        assertSchedulingTemplate(schedulingTemplates.getLast(), res2);

        Mockito.verify(schedulingTemplateService).getSchedulingTemplatesV2();
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesGetPermissionDenied() {
        Mockito.when(schedulingTemplateService.getSchedulingTemplatesV2()).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesGet());
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(schedulingTemplateService).getSchedulingTemplatesV2();
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesIdDelete() {
        var id = 2345L;

        var result = schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdDelete(id);
        assertNotNull(result);
        assertEquals(204, result.getStatusCode().value());

        Mockito.verify(schedulingTemplateService).deleteSchedulingTemplateV2(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesIdDeletePermissionDenied() {
        var id = 2345L;

        Mockito.doThrow(new PermissionDeniedExceptionV2()).when(schedulingTemplateService).deleteSchedulingTemplateV2(id);

        var expectedException = assertThrows(PermissionDeniedException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdDelete(id));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(schedulingTemplateService).deleteSchedulingTemplateV2(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesIdDeleteResourceNotFound() {
        var id = 2345L;

        Mockito.doThrow(new ResourceNotFoundExceptionV2("Message1", "Message2")).when(schedulingTemplateService).deleteSchedulingTemplateV2(id);

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdDelete(id));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(schedulingTemplateService).deleteSchedulingTemplateV2(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesIdGet() {
        var id = 3456L;
        var schedulingTemplate = randomSchedulingTemplate();

        Mockito.when(schedulingTemplateService.getSchedulingTemplateFromOrganisationAndIdV2(id)).thenReturn(schedulingTemplate);

        var result = schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdGet(id);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingTemplate(schedulingTemplate, result.getBody());

        Mockito.verify(schedulingTemplateService).getSchedulingTemplateFromOrganisationAndIdV2(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesIdGetPermissionDenied() {
        var id = 3456L;

        Mockito.when(schedulingTemplateService.getSchedulingTemplateFromOrganisationAndIdV2(id)).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdGet(id));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(schedulingTemplateService).getSchedulingTemplateFromOrganisationAndIdV2(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesIdGetResourceNotFound() {
        var id = 3456L;

        Mockito.when(schedulingTemplateService.getSchedulingTemplateFromOrganisationAndIdV2(id)).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdGet(id));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(schedulingTemplateService).getSchedulingTemplateFromOrganisationAndIdV2(id);
        verifyNoMoreInteractions();
    }

    @Test
    public  void testV2SchedulingTemplatesIdPut() {
        var id = 3456L;
        var input = randomSchedulingTemplateRequestInput();
        var schedulingTemplate = randomSchedulingTemplate();

        Mockito.when(schedulingTemplateService.updateSchedulingTemplateV2(Mockito.eq(id), Mockito.any())).thenReturn(schedulingTemplate);

        var result = schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdPut(id, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingTemplate(schedulingTemplate, result.getBody());

        Mockito.verify(schedulingTemplateService).updateSchedulingTemplateV2(Mockito.eq(id), Mockito.argThat( x -> assertSchedulingTemplateRequest(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public  void testV2SchedulingTemplatesIdPutOnlyRequiredValues() {
        var id = 3456L;
        var input = new SchedulingTemplateRequest().conferencingSysId(45678L).uriPrefix(randomString())
                .uriDomain(randomString()).hostPinRequired(false).guestPinRequired(true)
                .uriNumberRangeLow(65432L).uriNumberRangeHigh(34567L).ivrTheme(randomString());
        var schedulingTemplate = randomSchedulingTemplate();

        Mockito.when(schedulingTemplateService.updateSchedulingTemplateV2(Mockito.eq(id), Mockito.any())).thenReturn(schedulingTemplate);

        var result = schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdPut(id, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingTemplate(schedulingTemplate, result.getBody());

        Mockito.verify(schedulingTemplateService).updateSchedulingTemplateV2(Mockito.eq(id), Mockito.argThat( x -> assertSchedulingTemplateRequest(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public  void testV2SchedulingTemplatesIdPutPermissionDenied() {
        var id = 3456L;
        var input = randomSchedulingTemplateRequestInput();

        Mockito.when(schedulingTemplateService.updateSchedulingTemplateV2(Mockito.eq(id), Mockito.any())).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdPut(id, input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(schedulingTemplateService).updateSchedulingTemplateV2(Mockito.eq(id), Mockito.argThat( x -> assertSchedulingTemplateRequest(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public  void testV2SchedulingTemplatesIdPutResourceNotFound() {
        var id = 3456L;
        var input = randomSchedulingTemplateRequestInput();

        Mockito.when(schedulingTemplateService.updateSchedulingTemplateV2(Mockito.eq(id), Mockito.any())).thenThrow(new ResourceNotFoundExceptionV2("Message1", "Message2"));

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdPut(id, input));
        assertNotNull(expectedException);
        assertEquals(404, expectedException.getHttpStatus().value());
        assertEquals("Resource: Message1 in field: Message2 not found.", expectedException.getErrorMessage());

        Mockito.verify(schedulingTemplateService).updateSchedulingTemplateV2(Mockito.eq(id), Mockito.argThat( x -> assertSchedulingTemplateRequest(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public  void testV2SchedulingTemplatesIdPutNotAcceptable() {
        var id = 3456L;
        var input = randomSchedulingTemplateRequestInput();

        Mockito.when(schedulingTemplateService.updateSchedulingTemplateV2(Mockito.eq(id), Mockito.any())).thenThrow(new NotAcceptableExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotAcceptableException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesIdPut(id, input));
        assertNotNull(expectedException);
        assertEquals(406, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(schedulingTemplateService).updateSchedulingTemplateV2(Mockito.eq(id), Mockito.argThat( x -> assertSchedulingTemplateRequest(x, input)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesPost() {
        var input = randomSchedulingTemplateRequestInput();
        var schedulingTemplate = randomSchedulingTemplate();

        Mockito.when(schedulingTemplateService.createSchedulingTemplateV2(Mockito.any(), Mockito.eq(true))).thenReturn(schedulingTemplate);

        var result = schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesPost(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingTemplate(schedulingTemplate, result.getBody());

        Mockito.verify(schedulingTemplateService).createSchedulingTemplateV2(Mockito.argThat( x -> assertSchedulingTemplateRequest(x, input)), Mockito.eq(true));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesPostOnlyRequiredValues() {
        var input = new SchedulingTemplateRequest().conferencingSysId(45678L).uriPrefix(randomString())
                .uriDomain(randomString()).hostPinRequired(false)
                .guestPinRequired(true)
                .uriNumberRangeLow(65432L).uriNumberRangeHigh(34567L)
                .ivrTheme(randomString()).isDefaultTemplate(true);
        var schedulingTemplate = randomSchedulingTemplate();

        Mockito.when(schedulingTemplateService.createSchedulingTemplateV2(Mockito.any(), Mockito.eq(true))).thenReturn(schedulingTemplate);

        var result = schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesPost(input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertSchedulingTemplate(schedulingTemplate, result.getBody());

        Mockito.verify(schedulingTemplateService).createSchedulingTemplateV2(Mockito.argThat( x -> assertSchedulingTemplateRequest(x, input)), Mockito.eq(true));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesPostPermissionDenied() {
        var input = randomSchedulingTemplateRequestInput();

        Mockito.when(schedulingTemplateService.createSchedulingTemplateV2(Mockito.any(), Mockito.eq(true))).thenThrow(new PermissionDeniedExceptionV2());

        var expectedException = assertThrows(PermissionDeniedException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesPost(input));
        assertNotNull(expectedException);
        assertEquals(403, expectedException.getHttpStatus().value());
        assertNull(expectedException.getErrorMessage());

        Mockito.verify(schedulingTemplateService).createSchedulingTemplateV2(Mockito.argThat( x -> assertSchedulingTemplateRequest(x, input)), Mockito.eq(true));
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2SchedulingTemplatesPostNotAcceptable() {
        var input = randomSchedulingTemplateRequestInput();

        Mockito.when(schedulingTemplateService.createSchedulingTemplateV2(Mockito.any(), Mockito.eq(true))).thenThrow(new NotAcceptableExceptionV2(DetailedError.DetailedErrorCodeEnum._10, "Message"));

        var expectedException = assertThrows(NotAcceptableException.class, () -> schedulingTemplateAdministrationControllerV2.v2SchedulingTemplatesPost(input));
        assertNotNull(expectedException);
        assertEquals(406, expectedException.getHttpStatus().value());
        assertEquals("Message", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._10, expectedException.getDetailedErrorCode());

        Mockito.verify(schedulingTemplateService).createSchedulingTemplateV2(Mockito.argThat( x -> assertSchedulingTemplateRequest(x, input)), Mockito.eq(true));
        verifyNoMoreInteractions();
    }

}
