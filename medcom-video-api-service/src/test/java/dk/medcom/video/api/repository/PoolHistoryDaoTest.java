package dk.medcom.video.api.repository;


import dk.medcom.video.api.dao.PoolHistoryDao;
import dk.medcom.video.api.dao.entity.PoolHistory;
import org.junit.jupiter.api.Test;

import jakarta.annotation.Resource;
import java.time.Instant;

public class PoolHistoryDaoTest extends RepositoryTest {

	@Resource
    private PoolHistoryDao subject;

	@Test
	public void testCreate() {
		// Given
		var poolHistory = new PoolHistory();
		poolHistory.setOrganisationCode(Instant.now().toEpochMilli() + "extend this string!".repeat(10));
		poolHistory.setDesiredPoolSize(10);
		poolHistory.setAvailablePoolRooms(20);
		poolHistory.setStatusTime(Instant.now());
		poolHistory.setCreatedTime(Instant.now());

		// When
		subject.create(poolHistory); // Ensures that SQL does not throw an exception.
	}
}