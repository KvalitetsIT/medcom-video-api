package dk.medcom.video.api;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.google.common.net.MediaType;
import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.organisation.model.OrganisationTree;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import io.nats.client.JetStreamApiException;
import io.nats.client.Nats;
import io.nats.client.api.StreamConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Consumer;

@EnableAutoConfiguration
@Configuration
@ComponentScan({ "dk.medcom.video.api.test", "dk.medcom.video.api.configuration","dk.medcom.vdx.organisation"})
public class TestApplication extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(TestApplication.class);
    private static GenericContainer testOrganisationFrontend;
    private static GenericContainer<?> jetStreamService;
    private static final String natsSubjectSchedulingInfo = "schedulingInfo";
    private static final String natsSubjectAudit = "natsSubject";
    private static String jetStreamPath;
    private final DataSource dataSource;

	public static void main(String[] args) throws JetStreamApiException, IOException, InterruptedException {
        Network n = Network.newNetwork();
	    createOrganisationService(n);

        setupJetStream(n);

        MariaDBContainer mariadb = (MariaDBContainer) new MariaDBContainer("mariadb:10.6")
                .withDatabaseName("videodb")
                .withUsername("videouser")
                .withPassword("secret1234")
                .withNetwork(n);

        mariadb.start();
        String jdbcUrl = mariadb.getJdbcUrl();
        System.setProperty("jdbc.url", jdbcUrl + "?useSSL=false");
        System.setProperty("organisation.service.endpoint", String.format("http://localhost:%s/services/", testOrganisationFrontend.getMappedPort(1080)));
        System.setProperty("short.link.base.url", "http://shortlink");
        System.setProperty("overflow.pool.organisation.id", "overflow");
        System.setProperty("organisationtree.service.endpoint", "http://localhost:" + testOrganisationFrontend.getMappedPort(1080));

        System.setProperty("userservice.token.attribute.auto.create.organisation", "auto-create-parent");

        System.setProperty("audit.nats.url", jetStreamPath);
        System.setProperty("audit.nats.subject", natsSubjectAudit);
//        System.setProperty("audit.nats.disabled", "true");

        System.setProperty("events.nats.subject.scheduling-info", natsSubjectSchedulingInfo);

        int phpMyAdminPort = 8123;
        int phpMyAdminContainerPort = 80;
        Consumer<CreateContainerCmd> cmd = e -> e.withPortBindings(new PortBinding(Ports.Binding.bindPort(phpMyAdminPort), new ExposedPort(phpMyAdminContainerPort)));

        System.out.println("------------------------");
        System.out.println(mysql.getNetworkAliases().get(0));

        HashMap<String, String> environmentMap = new HashMap<>();
        environmentMap.put("PMA_HOST", (String) mysql.getNetworkAliases().get(0));
        environmentMap.put("PMA_USER", "videouser");
        environmentMap.put("PMA_PASSWORD", "secret1234");
        GenericContainer phpMyAdmin = new GenericContainer<>("phpmyadmin/phpmyadmin:latest").
                withEnv(environmentMap).
                withNetwork(n).
                withCreateContainerCmdModifier(cmd);
        phpMyAdmin.start();

        System.setProperty("SERVER_SERVLET_CONTEXT_PATH", "/videoapi");

        System.setProperty("ALLOWED_ORIGINS", "http://allowed");

//        System.setProperty("event.organisation.filter", "");
        System.setProperty("pool.fill.organisation.user", "some@user");
        System.setProperty("pool.fill.organisation", "kvak");
        System.setProperty("pool.fill.interval", "PT5S");
        System.setProperty("pool.fill.disabled", "true");

        SpringApplication.run(TestApplication.class, args);
	}

    private static void setupJetStream(Network n) throws JetStreamApiException, IOException, InterruptedException {
        var natsContainerName = "nats";
        var natsContainerVersion = "2.9-alpine";

        jetStreamService = new GenericContainer<>(natsContainerName + ":" + natsContainerVersion);

        jetStreamService.withNetwork(n)
                .withNetworkAliases("nats")
                .withExposedPorts(4222, 8222)
                .withCommand("-js")
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Server is ready.*"));


        jetStreamService.start();

        jetStreamPath = "nats://" + jetStreamService.getContainerIpAddress() + ":" + jetStreamService.getMappedPort(4222);
        var natsHttpPath = "http://" + jetStreamService.getContainerIpAddress() + ":" + jetStreamService.getMappedPort(8222);
        logger.info("NATS path: " + jetStreamPath);
        logger.info("NATS http path: " + natsHttpPath);

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

    private static void createOrganisationService(Network n) {
        // Organisation mock server
        var organisationService = new MockServerContainer().
                withNetwork(n).
                withNetworkAliases("organisation");
        organisationService.start();
        testOrganisationFrontend = organisationService;
//        attachLogger(organisationService, organisationLogger);
        var mockServerClient = new MockServerClient(organisationService.getContainerIpAddress(), organisationService.getMappedPort(1080));
        mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisationtree").withQueryStringParameter("organisationCode", "pool-test-org")).respond(organisationTreeServiceResponse());
        mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisation").withQueryStringParameter("organisationCode", "pool-test-org")).respond(organisationServiceResponse("pool-test-org"));
        mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisation").withQueryStringParameter("organisationCode", "company 1")).respond(organisationServiceResponse("company 1"));
        mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisation").withQueryStringParameter("organisationCode", "company 3")).respond(organisationServiceResponse("company 1"));
    }

    public TestApplication(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void setSchedulingTemplateTestData() throws SQLException  {
		dataSource.getConnection().createStatement();
	}

    private static HttpResponse organisationTreeServiceResponse() {
        OrganisationTree t = new OrganisationTree();
        t.setPoolSize(10);
        t.setCode("pool-test-org");
        t.setName("company name another-test-org");
        t.setChildren(null);

        return HttpResponse.response().withHeaders(new Header("content-type", "application/json")).withBody(JsonBody.json(t, MediaType.JSON_UTF_8));
    }

    private static HttpResponse organisationServiceResponse(String code) {
        Organisation t = new Organisation();
        t.setPoolSize(10);
        t.setCode(code);

        return HttpResponse.response().withHeaders(new Header("content-type", "application/json")).withBody(JsonBody.json(t, MediaType.JSON_UTF_8));
    }
}
