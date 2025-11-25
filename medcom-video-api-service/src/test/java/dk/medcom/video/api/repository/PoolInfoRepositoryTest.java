package dk.medcom.video.api.repository;


import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.dao.entity.PoolInfoEntity;
import org.junit.jupiter.api.Test;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
		assertNotNull(poolInfos);
		assertEquals(4, poolInfos.size());
		
		Map<String, PoolInfoEntity> resultMap = new HashMap<>();
		for (PoolInfoEntity poolInfo : poolInfos) {
			resultMap.put(poolInfo.getOrganisationCode(), poolInfo);
		}
		
		assertTrue(resultMap.containsKey(POOL_TEST_ORG));
		assertTrue(resultMap.containsKey(POOL_TEST_ORG2));
		
		assertEquals(10, resultMap.get(POOL_TEST_ORG).getWantedPoolSize());
		assertEquals(30, resultMap.get(POOL_TEST_ORG2).getWantedPoolSize());
		
		assertEquals(2, resultMap.get(POOL_TEST_ORG).getAvailablePoolSize());
		assertEquals(0, resultMap.get(POOL_TEST_ORG2).getAvailablePoolSize());

		assertNotNull(resultMap.get(POOL_TEST_ORG).getOrganisationName());
		assertNotNull(resultMap.get(POOL_TEST_ORG2).getOrganisationName());
	}
}