package dk.medcom.video.api.dao;

import java.util.List;

import dk.medcom.video.api.entity.PoolInfoEntity;

public interface PoolInfoRepository {

	List<PoolInfoEntity> getPoolInfos();
}
