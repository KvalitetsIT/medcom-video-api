package dk.medcom.video.api.controller.v2.mapper;

import dk.medcom.video.api.service.model.PoolInfoModel;
import org.openapitools.model.PoolInfo;

import java.util.List;

public class PoolMapper {

    public static List<PoolInfo> internalToExternal(List<PoolInfoModel> input) {
        return input.stream().map(PoolMapper::internalToExternal).toList();
    }

    private static PoolInfo internalToExternal(PoolInfoModel input) {
        return new PoolInfo()
                .organisationId(input.organisationId())
                .desiredPoolSize(input.desiredPoolSize())
                .availablePoolSize(input.availablePoolSize())
                .schedulingTemplate(SchedulingTemplateMapper.internalToExternal(input.schedulingTemplate()))
                .schedulingInfoList(VideoSchedulingMapper.internalToExternal(input.schedulingInfoList()));
    }
}
