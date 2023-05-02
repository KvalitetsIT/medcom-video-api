package dk.medcom.video.api.repository;


import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.dao.entity.PoolInfoEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoolInfoRepositoryTest extends RepositoryTest {

	@Resource
    private PoolInfoRepository subject;

	@Test
	public void testSchedulingInfo() {
		// Given
		final String POOL_TEST_ORG = "pool-test-org";
		final String POOL_TEST_ORG2 = "pool-test-org2";
		
		// When
		List<PoolInfoEntity> poolInfos = subject.getPoolInfos();
		
		// Then
		Assert.assertNotNull(poolInfos);
		Assert.assertEquals(4, poolInfos.size());
		
		Map<String, PoolInfoEntity> resultMap = new HashMap<>();
		for (PoolInfoEntity poolInfo : poolInfos) {
			resultMap.put(poolInfo.getOrganisationCode(), poolInfo);
		}
		
		Assert.assertTrue(resultMap.containsKey(POOL_TEST_ORG));
		Assert.assertTrue(resultMap.containsKey(POOL_TEST_ORG2));
		
		Assert.assertEquals(10, resultMap.get(POOL_TEST_ORG).getWantedPoolSize());
		Assert.assertEquals(30, resultMap.get(POOL_TEST_ORG2).getWantedPoolSize());
		
		Assert.assertEquals(2, resultMap.get(POOL_TEST_ORG).getAvailablePoolSize());
		Assert.assertEquals(0, resultMap.get(POOL_TEST_ORG2).getAvailablePoolSize());

		Assert.assertNotNull(resultMap.get(POOL_TEST_ORG).getOrganisationName());
		Assert.assertNotNull(resultMap.get(POOL_TEST_ORG2).getOrganisationName());
	}
}