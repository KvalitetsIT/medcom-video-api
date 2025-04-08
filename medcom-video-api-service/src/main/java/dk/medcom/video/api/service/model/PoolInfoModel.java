package dk.medcom.video.api.service.model;

import java.util.List;

public record PoolInfoModel(String organisationId,
                            int desiredPoolSize,
                            int availablePoolSize,
                            SchedulingTemplateModel schedulingTemplate,
                            List<SchedulingInfoModel> schedulingInfoList) {
}
