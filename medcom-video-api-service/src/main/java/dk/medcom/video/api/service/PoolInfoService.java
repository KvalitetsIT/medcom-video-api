package dk.medcom.video.api.service;

import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.entity.PoolInfoEntity;

import java.util.List;

public interface PoolInfoService {
    List<PoolInfoEntity> getAllPoolInfo();

    List<PoolInfoDto> getPoolInfo();
}
