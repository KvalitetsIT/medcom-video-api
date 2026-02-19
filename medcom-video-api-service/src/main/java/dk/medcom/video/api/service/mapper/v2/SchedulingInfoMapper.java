package dk.medcom.video.api.service.mapper.v2;

import dk.medcom.video.api.api.CreateSchedulingInfoDto;
import dk.medcom.video.api.api.UpdateSchedulingInfoDto;
import dk.medcom.video.api.service.model.CreateSchedulingInfoModel;
import dk.medcom.video.api.service.model.UpdateSchedulingInfoModel;

public class SchedulingInfoMapper {

    public static CreateSchedulingInfoDto modelToDto(CreateSchedulingInfoModel input) {
        var output = new CreateSchedulingInfoDto();
        output.setOrganizationId(input.organizationId());
        output.setSchedulingTemplateId(input.schedulingTemplateId());

        return output;
    }

    public static UpdateSchedulingInfoDto modelToDto(UpdateSchedulingInfoModel input) {
        var output = new UpdateSchedulingInfoDto();
        output.setProvisionStatus(EnumMapper.modelToEntity(input.provisionStatus()));
        output.setProvisionStatusDescription(input.provisionStatusDescription());
        output.setProvisionVmrId(input.provisionVmrId());

        return output;
    }
}
