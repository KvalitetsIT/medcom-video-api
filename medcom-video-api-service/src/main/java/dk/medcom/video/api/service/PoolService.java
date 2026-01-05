package dk.medcom.video.api.service;

import dk.medcom.video.api.api.PoolInfoDto;

public interface PoolService {
    void fillOrDeletePool(PoolInfoDto poolInfo);
}
