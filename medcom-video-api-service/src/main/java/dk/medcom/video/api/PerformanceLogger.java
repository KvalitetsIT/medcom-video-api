package dk.medcom.video.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceLogger {
    private static final Logger logger = LoggerFactory.getLogger("PERFORMANCE");
    private String operation;
    private final long start;

    public PerformanceLogger(String operation) {
        this.operation = operation;
        start = System.currentTimeMillis();
    }

    public void logTimeSinceCreation() {
        logger.info("{} took {} ms.", operation, System.currentTimeMillis()-start);
    }

    public void reset(String operation) {
        this.operation = operation;
    }
}
