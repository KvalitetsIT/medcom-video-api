package dk.medcom.video.api.configuration;

import dk.medcom.video.api.service.PoolHistoryService;
import dk.medcom.video.api.service.PoolService;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT1M", defaultLockAtLeastFor = "PT1M")
public class ScheduledTaskConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskConfiguration.class);

    @Autowired
    private PoolHistoryService poolHistoryService;

    @Autowired
    private PoolService poolService;

    @Value("${pool.fill.disabled:false}")
    private boolean poolFillDisabled;

    @SchedulerLock(name = "history")
    @Scheduled(fixedDelayString = "PT1M" )
    public void calculateHistoryService() {
        LockAssert.assertLocked();

        poolHistoryService.calculateHistory();
    }

    @SchedulerLock(name = "fillPools", lockAtLeastFor = "PT20S")
    @Scheduled(fixedDelayString = "${pool.fill.interval}")
    public void fillPools() {
        var start = System.currentTimeMillis();
        logger.debug("fillPools before assertLocked.");
        LockAssert.assertLocked();
        logger.debug("fillPools after assertLocked.");

        if(!poolFillDisabled) {
            poolService.fillAndDeletePools();
        }

        logger.debug("fillPools took {} ms.", System.currentTimeMillis() - start);
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build()
        );
    }
}
