package dk.medcom.video.api.service;

import dk.medcom.video.api.api.CreateSchedulingTemplateDto;
import dk.medcom.video.api.api.UpdateSchedulingTemplateDto;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;

import java.util.List;

public interface SchedulingTemplateService {
    List<SchedulingTemplate> getSchedulingTemplates() throws PermissionDeniedException;

    SchedulingTemplate createSchedulingTemplate(CreateSchedulingTemplateDto createSchedulingTemplateDto, boolean b) throws PermissionDeniedException, NotAcceptableException;

    SchedulingTemplate getSchedulingTemplateFromOrganisationAndId(Long id) throws PermissionDeniedException, RessourceNotFoundException;

    SchedulingTemplate updateSchedulingTemplate(Long id, UpdateSchedulingTemplateDto updateSchedulingTemplateDto) throws PermissionDeniedException, RessourceNotFoundException, NotAcceptableException;

    void deleteSchedulingTemplate(Long id) throws PermissionDeniedException, RessourceNotFoundException;

    SchedulingTemplate getSchedulingTemplateInOrganisationTree() throws PermissionDeniedException, NotAcceptableException;
}
