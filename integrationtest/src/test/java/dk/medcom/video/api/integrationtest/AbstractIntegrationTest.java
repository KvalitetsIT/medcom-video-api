package dk.medcom.video.api.integrationtest;

import io.nats.client.JetStreamApiException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.sql.*;

public abstract class AbstractIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    public static GenericContainer<?> videoApi;
    private static String apiBasePath;
    private static String jdbcUrl;
    private static String keycloakUrl;
    private static ServiceStarter serviceStarter;


    @AfterAll
    static void afterAll() {
        if(videoApi != null) {
            videoApi.getDockerClient().stopContainerCmd(videoApi.getContainerId()).exec();
        }
    }

    @BeforeAll
    static void beforeAll() throws JetStreamApiException, IOException, InterruptedException {
        setup();
    }

    private static void setup() throws JetStreamApiException, IOException, InterruptedException {
        var runInDocker = Boolean.getBoolean("runInDocker");
        logger.info("Running integration test in docker container: {}", runInDocker);

        serviceStarter = new ServiceStarter();
        if(runInDocker) {
            videoApi = serviceStarter.startServicesInDocker();
            apiBasePath = "http://" + videoApi.getHost() + ":" + videoApi.getMappedPort(8080) + "/api";
        } else if (serviceStarter.isFirstStart()) {
            serviceStarter.startServices();
            apiBasePath = "http://localhost:8080";
        }
        jdbcUrl = serviceStarter.getJdbcUrl();
        keycloakUrl = serviceStarter.getKeycloakUrl();
    }

    protected String getApiBasePath() {
        return apiBasePath;
    }

    protected String getJdbcUrl() {
        return jdbcUrl;
    }

    protected String getKeycloakUrl() {
        return keycloakUrl;
    }

    protected void verifyRowExistsInDatabase(String sql) throws SQLException {
        serviceStarter.verifyRowExistsInDatabase(sql);
    }

}
