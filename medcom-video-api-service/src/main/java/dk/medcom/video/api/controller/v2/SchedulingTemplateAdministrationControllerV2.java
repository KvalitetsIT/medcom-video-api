package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.controller.v2.exception.NotAcceptableException;
import dk.medcom.video.api.controller.v2.exception.ResourceNotFoundException;
import dk.medcom.video.api.controller.v2.exception.InternalServerErrorException;
import dk.medcom.video.api.controller.v2.exception.PermissionDeniedException;
import dk.medcom.video.api.controller.v2.mapper.SchedulingTemplateMapper;
import dk.medcom.video.api.interceptor.Oauth;
import dk.medcom.video.api.service.SchedulingTemplateServiceV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import org.openapitools.api.SchedulingTemplateAdministrationV2Api;
import org.openapitools.model.SchedulingTemplate;
import org.openapitools.model.SchedulingTemplateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SchedulingTemplateAdministrationControllerV2 implements SchedulingTemplateAdministrationV2Api {
    private static final Logger logger = LoggerFactory.getLogger(SchedulingTemplateAdministrationControllerV2.class);

    private final String anyScope = "hasAnyAuthority('SCOPE_meeting-user','SCOPE_meeting-admin','SCOPE_meeting-provisioner','SCOPE_meeting-provisioner-user','SCOPE_meeting-planner','SCOPE_undefined')";
    private final String adminScope = "hasAuthority('SCOPE_meeting-admin')";

    private final SchedulingTemplateServiceV2 schedulingTemplateService;

    public SchedulingTemplateAdministrationControllerV2(SchedulingTemplateServiceV2 schedulingTemplateService) {
        this.schedulingTemplateService = schedulingTemplateService;
    }

    @Oauth
    @Override
    @PreAuthorize(anyScope)
    public ResponseEntity<List<SchedulingTemplate>> v2SchedulingTemplatesGet() {
        logger.debug("Enter GET scheduling templates, v2.");
        try {
            var schedulingTemplates = schedulingTemplateService.getSchedulingTemplatesV2();

            return ResponseEntity.ok(SchedulingTemplateMapper.internalToExternal(schedulingTemplates));
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(adminScope)
    public ResponseEntity<Void> v2SchedulingTemplatesIdDelete(Long id) {
        logger.debug("Enter DELETE scheduling template with id: {}, v2.", id);
        try {
            schedulingTemplateService.deleteSchedulingTemplateV2(id);

            return ResponseEntity.noContent().build();
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(anyScope)
    public ResponseEntity<SchedulingTemplate> v2SchedulingTemplatesIdGet(Long id) {
        logger.debug("Enter GET scheduling templates with id: {}, v2.", id);
        try {
            var schedulingTemplate = schedulingTemplateService.getSchedulingTemplateFromOrganisationAndIdV2(id);

            return ResponseEntity.ok(SchedulingTemplateMapper.internalToExternal(schedulingTemplate));
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(adminScope)
    public ResponseEntity<SchedulingTemplate> v2SchedulingTemplatesIdPut(Long id, SchedulingTemplateRequest updateSchedulingTemplate) {
        logger.debug("Enter PUT scheduling template with id: {}, v2.", id);
        try {
            var schedulingTemplate = schedulingTemplateService.updateSchedulingTemplateV2(id, SchedulingTemplateMapper.externalToInternal(updateSchedulingTemplate));

            return ResponseEntity.ok(SchedulingTemplateMapper.internalToExternal(schedulingTemplate));
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (NotAcceptableExceptionV2 e) {
            throw new NotAcceptableException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(adminScope)
    public ResponseEntity<SchedulingTemplate> v2SchedulingTemplatesPost(SchedulingTemplateRequest createSchedulingTemplate) {
        logger.debug("Enter POST scheduling template, v2.");
        try {
            var schedulingTemplate = schedulingTemplateService.createSchedulingTemplateV2(SchedulingTemplateMapper.externalToInternal(createSchedulingTemplate), true);

            return ResponseEntity.ok(SchedulingTemplateMapper.internalToExternal(schedulingTemplate));
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (NotAcceptableExceptionV2 e) {
            throw new NotAcceptableException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }
}
