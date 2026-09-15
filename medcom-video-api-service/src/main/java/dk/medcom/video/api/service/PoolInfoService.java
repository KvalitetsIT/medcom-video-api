package dk.medcom.video.api.service;

import dk.medcom.video.api.api.PoolInfoDto;

import java.util.List;

public interface PoolInfoService {
    List<PoolInfoDto> getPoolInfo();
}
