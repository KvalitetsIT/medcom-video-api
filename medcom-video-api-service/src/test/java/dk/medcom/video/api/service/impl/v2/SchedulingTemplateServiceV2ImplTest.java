package dk.medcom.video.api.service.impl.v2;

import dk.medcom.video.api.controller.exceptions.NotAcceptableErrors;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.service.SchedulingTemplateService;
import dk.medcom.video.api.service.SchedulingTemplateServiceV2;
import dk.medcom.video.api.service.SchedulingTemplateServiceV2Impl;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.model.DetailedError;

import java.util.List;

import static dk.medcom.video.api.service.impl.v2.HelperMethods.*;
import static org.junit.jupiter.api.Assertions.*;

public class SchedulingTemplateServiceV2ImplTest {
    private SchedulingTemplateServiceV2 schedulingTemplateServiceV2;

    private SchedulingTemplateService schedulingTemplateService;

    @BeforeEach
    public void setup() {
        schedulingTemplateService = Mockito.mock(SchedulingTemplateService.class);

        schedulingTemplateServiceV2 = new SchedulingTemplateServiceV2Impl(schedulingTemplateService);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(schedulingTemplateService);
    }

    @Test
    public void testGetSchedulingTemplatesV2() throws PermissionDeniedException {
        var schedulingTemplates = List.of(randomSchedulingTemplate(), randomSchedulingTemplate());

        Mockito.when(schedulingTemplateService.getSchedulingTemplates()).thenReturn(schedulingTemplates);

        var result = schedulingTemplateServiceV2.getSchedulingTemplatesV2();
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.id().equals(schedulingTemplates.getFirst().getId())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.id().equals(schedulingTemplates.getLast().getId())).findFirst().orElseThrow();

        assertSchedulingTemplate(schedulingTemplates.getFirst(), res1);
        assertSchedulingTemplate(schedulingTemplates.getLast(), res2);

        Mockito.verify(schedulingTemplateService).getSchedulingTemplates();
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingTemplatesV2PermissionDenied() throws PermissionDeniedException {
        Mockito.when(schedulingTemplateService.getSchedulingTemplates()).thenThrow(PermissionDeniedException.class);

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> schedulingTemplateServiceV2.getSchedulingTemplatesV2());
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(schedulingTemplateService).getSchedulingTemplates();
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateSchedulingTemplateV2IncludeOrgTrue() throws NotAcceptableException, PermissionDeniedException {
        var input = randomSchedulingTemplateRequestModel();
        var schedulingTemplate = randomSchedulingTemplate();

        Mockito.when(schedulingTemplateService.createSchedulingTemplate(Mockito.any(), Mockito.eq(true))).thenReturn(schedulingTemplate);

        var result = schedulingTemplateServiceV2.createSchedulingTemplateV2(input, true);
        assertNotNull(result);
        assertSchedulingTemplate(schedulingTemplate, result);

        Mockito.verify(schedulingTemplateService).createSchedulingTemplate(Mockito.argThat(x -> assertSchedulingTemplateRequest(input, x)), Mockito.eq(true));
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateSchedulingTemplateV2IncludeOrgFalse() throws NotAcceptableException, PermissionDeniedException {
        var input = randomSchedulingTemplateRequestModel();
        var schedulingTemplate = randomSchedulingTemplate();

        Mockito.when(schedulingTemplateService.createSchedulingTemplate(Mockito.any(), Mockito.eq(false))).thenReturn(schedulingTemplate);

        var result = schedulingTemplateServiceV2.createSchedulingTemplateV2(input, false);
        assertNotNull(result);
        assertSchedulingTemplate(schedulingTemplate, result);

        Mockito.verify(schedulingTemplateService).createSchedulingTemplate(Mockito.argThat(x -> assertSchedulingTemplateRequest(input, x)), Mockito.eq(false));
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateSchedulingTemplateV2NotAcceptable() throws NotAcceptableException, PermissionDeniedException {
        var input = randomSchedulingTemplateRequestModel();

        Mockito.when(schedulingTemplateService.createSchedulingTemplate(Mockito.any(), Mockito.eq(true))).thenThrow(new NotAcceptableException(NotAcceptableErrors.URI_ASSIGNMENT_FAILED_NOT_POSSIBLE_TO_CREATE_UNIQUE, "message"));

        var expectedException = assertThrows(NotAcceptableExceptionV2.class, () -> schedulingTemplateServiceV2.createSchedulingTemplateV2(input, true));
        assertNotNull(expectedException);
        assertEquals("The Uri assignment failed. It was not possible to create a unique. Consider changing the interval on the template", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._15, expectedException.getDetailedErrorCode());

        Mockito.verify(schedulingTemplateService).createSchedulingTemplate(Mockito.argThat(x -> assertSchedulingTemplateRequest(input, x)), Mockito.eq(true));
        verifyNoMoreInteractions();
    }

    @Test
    public void testCreateSchedulingTemplateV2PermissionDenied() throws NotAcceptableException, PermissionDeniedException {
        var input = randomSchedulingTemplateRequestModel();

        Mockito.when(schedulingTemplateService.createSchedulingTemplate(Mockito.any(), Mockito.eq(true))).thenThrow(new PermissionDeniedException());

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> schedulingTemplateServiceV2.createSchedulingTemplateV2(input, true));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(schedulingTemplateService).createSchedulingTemplate(Mockito.argThat(x -> assertSchedulingTemplateRequest(input, x)), Mockito.eq(true));
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingTemplateFromOrganisationAndIdV2() throws RessourceNotFoundException, PermissionDeniedException {
        var id = 123L;
        var schedulingTemplate = randomSchedulingTemplate();

        Mockito.when(schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(id)).thenReturn(schedulingTemplate);

        var result = schedulingTemplateServiceV2.getSchedulingTemplateFromOrganisationAndIdV2(id);
        assertNotNull(result);
        assertSchedulingTemplate(schedulingTemplate, result);

        Mockito.verify(schedulingTemplateService).getSchedulingTemplateFromOrganisationAndId(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingTemplateFromOrganisationAndIdV2ResourceNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        var id = 123L;

        Mockito.when(schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(id)).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> schedulingTemplateServiceV2.getSchedulingTemplateFromOrganisationAndIdV2(id));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(schedulingTemplateService).getSchedulingTemplateFromOrganisationAndId(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetSchedulingTemplateFromOrganisationAndIdV2PermissionDenied() throws RessourceNotFoundException, PermissionDeniedException {
        var id = 123L;

        Mockito.when(schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(id)).thenThrow(PermissionDeniedException.class);

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> schedulingTemplateServiceV2.getSchedulingTemplateFromOrganisationAndIdV2(id));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(schedulingTemplateService).getSchedulingTemplateFromOrganisationAndId(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateSchedulingTemplateV2() throws RessourceNotFoundException, NotAcceptableException, PermissionDeniedException {
        var id = 123L;
        var input = randomSchedulingTemplateRequestModel();

        var schedulingTemplate = randomSchedulingTemplate();

        Mockito.when(schedulingTemplateService.updateSchedulingTemplate(Mockito.eq(id), Mockito.any())).thenReturn(schedulingTemplate);

        var result = schedulingTemplateServiceV2.updateSchedulingTemplateV2(id, input);
        assertNotNull(result);
        assertSchedulingTemplate(schedulingTemplate, result);

        Mockito.verify(schedulingTemplateService).updateSchedulingTemplate(Mockito.eq(id), Mockito.argThat(x -> assertSchedulingTemplateRequest(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateSchedulingTemplateV2ResourceNotFound() throws RessourceNotFoundException, NotAcceptableException, PermissionDeniedException {
        var id = 123L;
        var input = randomSchedulingTemplateRequestModel();

        Mockito.when(schedulingTemplateService.updateSchedulingTemplate(Mockito.eq(id), Mockito.any())).thenThrow(new RessourceNotFoundException("resource", "field"));

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> schedulingTemplateServiceV2.updateSchedulingTemplateV2(id, input));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(schedulingTemplateService).updateSchedulingTemplate(Mockito.eq(id), Mockito.argThat(x -> assertSchedulingTemplateRequest(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateSchedulingTemplateV2NotAcceptable() throws RessourceNotFoundException, NotAcceptableException, PermissionDeniedException {
        var id = 123L;
        var input = randomSchedulingTemplateRequestModel();

        Mockito.when(schedulingTemplateService.updateSchedulingTemplate(Mockito.eq(id), Mockito.any())).thenThrow(new NotAcceptableException(NotAcceptableErrors.CREATE_OR_UPDATE_POOL_TEMPLATE_FAILED, "message"));

        var expectedException = assertThrows(NotAcceptableExceptionV2.class, () -> schedulingTemplateServiceV2.updateSchedulingTemplateV2(id, input));
        assertNotNull(expectedException);
        assertEquals("Create or update of pool template failed due to only one pool template allowed", expectedException.getDetailedError());
        assertEquals(DetailedError.DetailedErrorCodeEnum._18, expectedException.getDetailedErrorCode());

        Mockito.verify(schedulingTemplateService).updateSchedulingTemplate(Mockito.eq(id), Mockito.argThat(x -> assertSchedulingTemplateRequest(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testUpdateSchedulingTemplateV2PermissionDenied() throws RessourceNotFoundException, NotAcceptableException, PermissionDeniedException {
        var id = 123L;
        var input = randomSchedulingTemplateRequestModel();

        Mockito.when(schedulingTemplateService.updateSchedulingTemplate(Mockito.eq(id), Mockito.any())).thenThrow(PermissionDeniedException.class);

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> schedulingTemplateServiceV2.updateSchedulingTemplateV2(id, input));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(schedulingTemplateService).updateSchedulingTemplate(Mockito.eq(id), Mockito.argThat(x -> assertSchedulingTemplateRequest(input, x)));
        verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteSchedulingTemplateV2() throws RessourceNotFoundException, PermissionDeniedException {
        var id = 123L;
        schedulingTemplateServiceV2.deleteSchedulingTemplateV2(id);

        Mockito.verify(schedulingTemplateService).deleteSchedulingTemplate(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteSchedulingTemplateV2ResourceNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        var id = 123L;
        Mockito.doThrow(new RessourceNotFoundException("resource", "field")).when(schedulingTemplateService).deleteSchedulingTemplate(id);

        var expectedException = assertThrows(ResourceNotFoundExceptionV2.class, () -> schedulingTemplateServiceV2.deleteSchedulingTemplateV2(id));
        assertNotNull(expectedException);
        assertEquals("Resource: resource in field: field not found.", expectedException.getMessage());

        Mockito.verify(schedulingTemplateService).deleteSchedulingTemplate(id);
        verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteSchedulingTemplateV2PermissionDenied() throws RessourceNotFoundException, PermissionDeniedException {
        var id = 123L;

        Mockito.doThrow(PermissionDeniedException.class).when(schedulingTemplateService).deleteSchedulingTemplate(id);

        var expectedException = assertThrows(PermissionDeniedExceptionV2.class, () -> schedulingTemplateServiceV2.deleteSchedulingTemplateV2(id));
        assertNotNull(expectedException);
        assertNull(expectedException.getMessage());

        Mockito.verify(schedulingTemplateService).deleteSchedulingTemplate(id);
        verifyNoMoreInteractions();
    }
}
