package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.PoolHistoryDao;
import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.dao.entity.PoolHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public class PoolHistoryServiceImpl implements PoolHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(PoolHistoryServiceImpl.class);

    private final PoolInfoRepository poolInfoRepository;
    private final PoolHistoryDao poolHistoryDao;

    public PoolHistoryServiceImpl(PoolInfoRepository poolInfoRepository, PoolHistoryDao poolHistoryDao) {
        this.poolInfoRepository = poolInfoRepository;
        this.poolHistoryDao = poolHistoryDao;
    }

    @Override
    @Transactional
    public void calculateHistory() {
        var start = System.currentTimeMillis();
        logger.debug("Starting to calculate pool history.");
        var poolInfo = poolInfoRepository.getPoolInfos();
        logger.info("Found data for {} pools. Updating pool history.", poolInfo.size());
        poolInfo.forEach(x -> {
            var now = Instant.now();
            var poolHistory = new PoolHistory();
            poolHistory.setOrganisationCode(x.getOrganisationCode());
            poolHistory.setDesiredPoolSize(x.getWantedPoolSize());
            poolHistory.setAvailablePoolRooms(x.getAvailablePoolSize());
            poolHistory.setStatusTime(now);
            poolHistory.setCreatedTime(now);

            poolHistoryDao.create(poolHistory);
            logger.debug("Updated pool history for {} with {} desired rooms and {} available rooms.", poolHistory.getOrganisationCode(), poolHistory.getDesiredPoolSize(), poolHistory.getAvailablePoolRooms());
        });

        logger.debug("Ended calculating pool history. Took {} ms.", System.currentTimeMillis() - start);
    }
}
