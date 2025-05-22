package dk.medcom.video.api.service;

import dk.medcom.video.api.service.model.SchedulingTemplateRequestModel;
import dk.medcom.video.api.service.model.SchedulingTemplateModel;

import java.util.List;

public interface SchedulingTemplateServiceV2 {
    List<SchedulingTemplateModel> getSchedulingTemplatesV2();

    SchedulingTemplateModel createSchedulingTemplateV2(SchedulingTemplateRequestModel createSchedulingTemplate, boolean includeOrganisation);

    SchedulingTemplateModel getSchedulingTemplateFromOrganisationAndIdV2(Long id);

    SchedulingTemplateModel updateSchedulingTemplateV2(Long id, SchedulingTemplateRequestModel updateSchedulingTemplate);

    void deleteSchedulingTemplateV2(Long id);
}
