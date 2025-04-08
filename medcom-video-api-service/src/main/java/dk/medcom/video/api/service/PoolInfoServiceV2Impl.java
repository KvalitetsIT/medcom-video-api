package dk.medcom.video.api.service;

import dk.medcom.video.api.service.model.PoolInfoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dk.medcom.video.api.service.mapper.v2.PoolMapper.dtoToModel;


public class PoolInfoServiceV2Impl implements PoolInfoServiceV2 {
    private final static Logger logger = LoggerFactory.getLogger(PoolInfoServiceV2Impl.class);
    private final PoolInfoService poolInfoService;
    private final String shortLinkBaseUrl;

    public PoolInfoServiceV2Impl(PoolInfoService poolInfoService, String shortLinkBaseUrl) {
        this.poolInfoService = poolInfoService;
        this.shortLinkBaseUrl = shortLinkBaseUrl;
    }

    @Override
    public List<PoolInfoModel> getPoolInfoV2() {
        logger.debug("Get pool info, v2.");
        return poolInfoService.getPoolInfo().stream().map(x -> dtoToModel(x, shortLinkBaseUrl)).toList();
    }
}
