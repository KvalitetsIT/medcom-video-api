package dk.medcom.video.api.integrationtest;

import dk.medcom.video.api.Application;
import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.organisation.model.OrganisationTree;
import io.nats.client.JetStreamApiException;
import io.nats.client.Nats;
import io.nats.client.api.StreamConfiguration;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.*;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.util.List;

public class ServiceStarter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarter.class);
    private static final Logger mariadbLogger = LoggerFactory.getLogger("mariadb");
    private static final Logger videoApiLogger = LoggerFactory.getLogger("video-api");
    private static final Logger organisationLogger = LoggerFactory.getLogger("organisation");
    private static final Logger jetStreamLogger = LoggerFactory.getLogger("jetstream");
    private static final Logger keycloakLogger = LoggerFactory.getLogger("keycloak");

    protected static Network dockerNetwork;
    private static String jetStreamPath;
    private static final String natsSubjectSchedulingInfo = "schedulingInfo";
    private static final String natsSubjectAudit = "natsSubject";
    private static String jdbcUrl;
    private static final String jdbcUser = "videouser";
    private static final String jdbcPass = "secret1234";
    private static String organisationPath;
    private static String keycloakUrl;

    private static boolean firstStart = true;

    public void startServices() throws JetStreamApiException, IOException, InterruptedException {
        firstStart = false;
        dockerNetwork = Network.newNetwork();

        setupDatabaseContainer();
        setupMockOrganisationService();
        setupJetStream();
        setupKeycloak();

        System.setProperty("jdbc.url", jdbcUrl);
        System.setProperty("jdbc.user", jdbcUser);
        System.setProperty("jdbc.pass", jdbcPass);

        System.setProperty("userservice.url", "");
        System.setProperty("userservice.token.attribute.organisation", "organisation_id");
        System.setProperty("userservice.token.attribute.username", "username");
        System.setProperty("userservice.token.attribute.email", "email");
        System.setProperty("userservice.token.attribute.userrole", "userrole");
        System.setProperty("userservice.token.attribute.auto.create.organisation", "parent_org");

        System.setProperty("scheduling.template.default.conferencing.sys.id", "22");
        System.setProperty("scheduling.template.default.uri.prefix", "abc");
        System.setProperty("scheduling.template.default.uri.domain", "test.dk");
        System.setProperty("scheduling.template.default.host.pin.required", "true");
        System.setProperty("scheduling.template.default.host.pin.range.low", "1000");
        System.setProperty("scheduling.template.default.host.pin.range.high", "9999");
        System.setProperty("scheduling.template.default.guest.pin.required", "true");
        System.setProperty("scheduling.template.default.guest.pin.range.low", "1000");
        System.setProperty("scheduling.template.default.guest.pin.range.high", "9999");
        System.setProperty("scheduling.template.default.vmravailable.before", "15");
        System.setProperty("scheduling.template.default.max.participants", "10");
        System.setProperty("scheduling.template.default.end.meeting.on.end.time", "true");
        System.setProperty("scheduling.template.default.uri.number.range.low", "1000");
        System.setProperty("scheduling.template.default.uri.number.range.high", "9999");
        System.setProperty("scheduling.template.default.ivr.theme", "10");

        System.setProperty("scheduling.info.citizen.portal", "https://portal.vconf.dk");

        System.setProperty("mapping.role.provisioner", "dk:medcom:role:provisioner");
        System.setProperty("mapping.role.admin", "dk:medcom:role:admin");
        System.setProperty("mapping.role.user", "dk:medcom:role:user");
        System.setProperty("mapping.role.meeting_planner", "dk:medcom:role:meeting_planner");

        System.setProperty("LOG_LEVEL", "DEBUG");

        System.setProperty("organisation.service.enabled", "true");
        System.setProperty("organisation.service.endpoint", organisationPath + "/services");
        System.setProperty("organisationtree.service.endpoint", organisationPath);

        System.setProperty("short.link.base.url", "https://video.link/");

        System.setProperty("overflow.pool.organisation.id", "overflow");

        System.setProperty("ALLOWED_ORIGINS", "http://allowed:4100,http://allowed:4200");

        System.setProperty("audit.nats.url", jetStreamPath);
        System.setProperty("audit.nats.subject", natsSubjectAudit);
        System.setProperty("events.nats.subject.scheduling-info", "schedulingInfo");
        System.setProperty("event.organisation.filter", "some_random_org_that_does_not_exist,new-provisioner-org");
        System.setProperty("pool.fill.organisation.user", "some@email");
        System.setProperty("pool.fill.organisation", "some_org");
        System.setProperty("pool.fill.interval", "PT1M");

        System.setProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri", keycloakUrl);
        //System.setProperty("logging.level.org.springframework.security", "TRACE");

        SpringApplication.run(Application.class);
    }

    public GenericContainer<?> startServicesInDocker() throws JetStreamApiException, IOException, InterruptedException {
        if (firstStart) {
            firstStart = false;
            dockerNetwork = Network.newNetwork();

            setupDatabaseContainer();
            setupMockOrganisationService();
            setupJetStream();
            setupKeycloak();
        }

        GenericContainer<?> service;
        // Start VideoAPI
        service = new GenericContainer<>("kvalitetsit/medcom-video-api:latest")
                .withNetwork(dockerNetwork)
                .withNetworkAliases("videoapi")
                .withEnv("CONTEXT", "/api")
                .withEnv("jdbc_url", "jdbc:mariadb://mariadb:3306/videodb?useSSL=false&serverTimezone=UTC")
                .withEnv("jdbc_user", jdbcUser)
                .withEnv("jdbc_pass", jdbcPass)

                .withEnv("userservice_url", "")
                .withEnv("userservice_token_attribute_organisation", "organisation_id")
                .withEnv("userservice_token_attribute_username", "username")
                .withEnv("userservice.token.attribute.email", "email")
                .withEnv("userservice.token.attribute.userrole", "userrole")
                .withEnv("userservice.token.attribute.auto.create.organisation", "parent_org")

                .withEnv("scheduling.template.default.conferencing.sys.id", "22")
                .withEnv("scheduling.template.default.uri.prefix", "abc")
                .withEnv("scheduling.template.default.uri.domain", "test.dk")
                .withEnv("scheduling.template.default.host.pin.required", "true")
                .withEnv("scheduling.template.default.host.pin.range.low", "1000")
                .withEnv("scheduling.template.default.host.pin.range.high", "9999")
                .withEnv("scheduling.template.default.guest.pin.required", "true")
                .withEnv("scheduling.template.default.guest.pin.range.low", "1000")
                .withEnv("scheduling.template.default.guest.pin.range.high", "9999")
                .withEnv("scheduling.template.default.vmravailable.before", "15")
                .withEnv("scheduling.template.default.max.participants", "10")
                .withEnv("scheduling.template.default.end.meeting.on.end.time", "true")
                .withEnv("scheduling.template.default.uri.number.range.low", "1000")
                .withEnv("scheduling.template.default.uri.number.range.high", "9999")
                .withEnv("scheduling.template.default.ivr.theme", "10")

                .withEnv("scheduling.info.citizen.portal", "https://portal.vconf.dk")

                .withEnv("mapping.role.provisioner", "dk:medcom:role:provisioner")
                .withEnv("mapping.role.admin", "dk:medcom:role:admin")
                .withEnv("mapping.role.user", "dk:medcom:role:user")
                .withEnv("mapping.role.meeting_planner", "dk:medcom:role:meeting_planner")

                .withEnv("LOG_LEVEL", "debug")
                .withEnv("spring.flyway.locations", "classpath:db/migration,filesystem:/app/sql")
                .withClasspathResourceMapping("db/migration/V901__insert_test_data.sql", "/app/sql/V901__insert_test_data.sql", BindMode.READ_ONLY)
                .withClasspathResourceMapping("db/migration/V902__create_view.sql", "/app/sql/V902__create_view.sql", BindMode.READ_ONLY)

                .withEnv("organisation.service.enabled", "true")
                .withEnv("organisation.service.endpoint", "http://organisation:1080/services")
                .withEnv("organisationtree.service.endpoint", "http://organisation:1080")
                .withEnv("short.link.base.url", "https://video.link/")
                .withEnv("overflow.pool.organisation.id", "overflow")

                .withEnv("ALLOWED_ORIGINS", "http://allowed:4100,http://allowed:4200")

                .withEnv("audit.nats.url", "nats://nats:4222")
                .withEnv("audit.nats.subject", natsSubjectAudit)

                .withEnv("events.nats.subject.scheduling-info", "schedulingInfo")

                .withEnv("event.organisation.filter", "some_random_org_that_does_not_exist,new-provisioner-org")
                .withEnv("pool.fill.organisation.user", "some@email")
                .withEnv("pool.fill.organisation", "some_org")
                .withEnv("pool.fill.interval", "PT1M")

                .withEnv("spring.security.oauth2.resourceserver.jwt.issuer-uri", keycloakUrl)
                .withEnv("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", "http://keycloak:9000/realms/keycloaktest/protocol/openid-connect/certs")
                //.withEnv("logging.level.org.springframework.security", "TRACE")

                .withExposedPorts(8080, 8081)
                .withStartupTimeout(Duration.ofSeconds(180))
                .waitingFor(Wait.forHttp("/manage/actuator/health").forPort(8081).forStatusCode(200).withStartupTimeout(Duration.ofSeconds(180)));
        service.start();
        attachLogger(service, videoApiLogger);

        return service;
    }

    private void setupDatabaseContainer() {
        // SQL server for Video API.
        var mariadb = new MariaDBContainer<>("mariadb:10.6")
                .withDatabaseName("videodb")
                .withUsername(jdbcUser)
                .withPassword(jdbcPass)
                .withNetwork(dockerNetwork)
                .withNetworkAliases("mariadb");
        mariadb.start();
        jdbcUrl = mariadb.getJdbcUrl();
        attachLogger(mariadb, mariadbLogger);
    }

    private void setupMockOrganisationService() {
        // Organisation mock server
        var organisationService = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0")).
                withNetwork(dockerNetwork).
                withNetworkAliases("organisation");
        organisationService.start();

        var mockServerClient = new MockServerClient(organisationService.getHost(), organisationService.getMappedPort(1080));
        mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisationtree").withQueryStringParameter("organisationCode", "user-org-pool")).respond(organisationTreeServiceResponse());
        mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisation").withQueryStringParameter("organisationCode", "user-org-pool")).respond(organisationServiceResponse());
        mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisation")).respond(organisationServiceListResponse());

        organisationPath = "http://localhost:" + organisationService.getMappedPort(1080);
        attachLogger(organisationService, organisationLogger);
    }

    private static HttpResponse organisationTreeServiceResponse() {
        OrganisationTree t = new OrganisationTree();
        t.setPoolSize(10);
        t.setCode("user-org-pool");
        t.setName("default user org");
        t.setChildren(null);

        return HttpResponse.response().withHeaders(new Header("content-type", "application/json")).withBody(JsonBody.json(t, MediaType.JSON_UTF_8));
    }

    private static HttpResponse organisationServiceResponse() {
        Organisation t = new Organisation();
        t.setPoolSize(10);
        t.setCode("user-org-pool");

        return HttpResponse.response().withHeaders(new Header("content-type", "application/json")).withBody(JsonBody.json(t, MediaType.JSON_UTF_8));
    }

    private static HttpResponse organisationServiceListResponse() {
        var userOrg = new Organisation();
        userOrg.setPoolSize(10);
        userOrg.setCode("user-org-pool");

        var newProvisionerOrg = new Organisation();
        newProvisionerOrg.setPoolSize(9);
        newProvisionerOrg.setCode("new-provisioner-org");

        return HttpResponse.response().withHeaders(new Header("content-type", "application/json")).withBody(JsonBody.json(List.of(userOrg, newProvisionerOrg), MediaType.JSON_UTF_8));
    }

    public static void setupJetStream() throws JetStreamApiException, IOException, InterruptedException {
        var natsContainerName = "nats";
        var natsContainerVersion = "2.9-alpine";

        GenericContainer<?> jetStreamService = new GenericContainer<>(natsContainerName + ":" + natsContainerVersion);

        jetStreamService.withNetwork(dockerNetwork)
                .withNetworkAliases("nats")
                .withExposedPorts(4222, 8222)
                .withCommand("-js")
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Server is ready.*"));

        jetStreamService.start();
        attachLogger(jetStreamService, jetStreamLogger);

        jetStreamPath = "nats://" + jetStreamService.getHost() + ":" + jetStreamService.getMappedPort(4222);
        var natsHttpPath = "http://" + jetStreamService.getHost() + ":" + jetStreamService.getMappedPort(8222);
        logger.info("NATS path: {}", jetStreamPath);
        logger.info("NATS http path: {}", natsHttpPath);

        addStream(natsSubjectSchedulingInfo);
        addStream(natsSubjectAudit);
    }

    private static void addStream(String subject) throws IOException, InterruptedException, JetStreamApiException {
        try(var natsConnection = Nats.connect(jetStreamPath)) {
            var streamConfiguration = StreamConfiguration.builder()
                    .addSubjects(subject)
                    .name(subject)
                    .build();

            natsConnection.jetStreamManagement().addStream(streamConfiguration);
        }
    }

    private void setupKeycloak() {
        var keycloakContainer = new GenericContainer<>("quay.io/keycloak/keycloak:26.0")
                .withClasspathResourceMapping("keycloaktest-realm.json", "/opt/keycloak/data/import/keycloaktest-realm.json", BindMode.READ_ONLY)
                .withCommand("start-dev", "--import-realm")
                .withEnv("KEYCLOAK_LOGLEVEL", "DEBUG")
                .withEnv("KC_HTTP_PORT", "9000")
                .withEnv("KC_BOOTSTRAP_ADMIN_USERNAME", "admin")
                .withEnv("KC_BOOTSTRAP_ADMIN_PASSWORD", "Test1234")
                .withNetwork(dockerNetwork)
                .withNetworkAliases("keycloak")
                .withExposedPorts(9000);

        keycloakContainer.start();
        keycloakUrl = "http://" + keycloakContainer.getHost() + ":" + keycloakContainer.getMappedPort(9000) + "/realms/keycloaktest";
        attachLogger(keycloakContainer, keycloakLogger);
    }

    String getKeycloakUrl() {
        return keycloakUrl;
    }

    private static void attachLogger(GenericContainer<?> container, Logger logger) {
        logger.info("Attaching logger to container: {}", container.getContainerInfo().getName());
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public boolean isFirstStart() {
        return firstStart;
    }

    void verifyRowExistsInDatabase(String sql) throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass);

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(sql);

        if(!rs.next()) {
            st.close();
            throw new RuntimeException("No rows found: " + sql);
        }
        st.close();
    }
}
