package dk.medcom.video.api.service.mapper.v2;

import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.service.model.PoolInfoModel;
import dk.medcom.video.api.service.model.SchedulingInfoModel;

public class PoolMapper {
    public static PoolInfoModel dtoToModel(PoolInfoDto pool, String shortLinkBaseUrl) {
        return new PoolInfoModel(pool.getOrganizationId(),
                pool.getDesiredPoolSize(),
                pool.getAvailablePoolSize(),
                SchedulingTemplateMapper.dtoToModel(pool.getSchedulingTemplate()),
                pool.getSchedulingInfoList().stream().map(x -> SchedulingInfoModel.from(x, shortLinkBaseUrl)).toList());
    }
}
