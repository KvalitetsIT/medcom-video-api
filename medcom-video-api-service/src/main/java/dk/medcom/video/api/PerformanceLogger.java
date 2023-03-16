package dk.medcom.video.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PerformanceLogger {
    private static final Logger logger = LoggerFactory.getLogger("PERFORMANCE");
    private String operation;
    private final long start;
    private final String instance;

    public PerformanceLogger(String instance, String operation) {
        this.operation = operation;
        this.instance = instance;
        start = System.currentTimeMillis();
    }

    public PerformanceLogger(String operation) {
        this(UUID.randomUUID().toString(), operation);
    }

    public void logTimeSinceCreation() {
        logger.info("[{}]{} took {} ms.", instance, operation, System.currentTimeMillis()-start);
    }

    public void reset(String operation) {
        this.operation = operation;
    }
}
