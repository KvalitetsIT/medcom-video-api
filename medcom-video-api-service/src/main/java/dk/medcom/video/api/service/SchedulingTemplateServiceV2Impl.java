package dk.medcom.video.api.service;

import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.service.exception.ExceptionMapper;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import dk.medcom.video.api.service.mapper.v2.SchedulingTemplateMapper;
import dk.medcom.video.api.service.model.SchedulingTemplateRequestModel;
import dk.medcom.video.api.service.model.SchedulingTemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SchedulingTemplateServiceV2Impl implements SchedulingTemplateServiceV2 {
    private final Logger logger = LoggerFactory.getLogger(SchedulingTemplateServiceV2Impl.class);

    private final SchedulingTemplateService schedulingTemplateService;

    public SchedulingTemplateServiceV2Impl(SchedulingTemplateService schedulingTemplateService) {
        this.schedulingTemplateService = schedulingTemplateService;
    }

    @Override
    public List<SchedulingTemplateModel> getSchedulingTemplatesV2() {
        logger.debug("Get scheduling templates, v2.");
        try {
            return schedulingTemplateService.getSchedulingTemplates().stream().map(SchedulingTemplateModel::from).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public SchedulingTemplateModel createSchedulingTemplateV2(SchedulingTemplateRequestModel createSchedulingTemplate, boolean includeOrganisation) {
        logger.debug("Create scheduling template, v2.");
        try {
            return SchedulingTemplateModel.from(schedulingTemplateService.createSchedulingTemplate(SchedulingTemplateMapper.modelToDto(createSchedulingTemplate), includeOrganisation));
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (NotAcceptableException e) {
            throw new NotAcceptableExceptionV2(ExceptionMapper.fromNotAcceptable(e.getErrorCode()), e.getErrorText());
        }
    }

    @Override
    public SchedulingTemplateModel getSchedulingTemplateFromOrganisationAndIdV2(Long id) {
        logger.debug("Get scheduling template from organisation and id, v2.");
        try {
            return SchedulingTemplateModel.from(schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(id));
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        }    }

    @Override
    public SchedulingTemplateModel updateSchedulingTemplateV2(Long id, SchedulingTemplateRequestModel updateSchedulingTemplate) {
        logger.debug("Update scheduling template, v2.");
        try {
            return SchedulingTemplateModel.from(schedulingTemplateService.updateSchedulingTemplate(id, SchedulingTemplateMapper.modelToDtoUpdate(updateSchedulingTemplate)));
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        } catch (NotAcceptableException e) {
            throw new NotAcceptableExceptionV2(ExceptionMapper.fromNotAcceptable(e.getErrorCode()), e.getErrorText());
        }
    }

    @Override
    public void deleteSchedulingTemplateV2(Long id) {
        logger.debug("Delete scheduling template, v2.");
        try {
            schedulingTemplateService.deleteSchedulingTemplate(id);
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        }
    }
}
