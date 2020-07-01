package dk.medcom.video.api.repository;

import java.util.List;

import dk.medcom.video.api.entity.PoolInfoEntity;

public interface PoolInfoRepository {

	List<PoolInfoEntity> getPoolInfos();
}
