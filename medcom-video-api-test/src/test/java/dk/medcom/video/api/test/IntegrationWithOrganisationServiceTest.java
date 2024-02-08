package dk.medcom.video.api.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.net.MediaType;
import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.organisation.model.OrganisationTree;
import io.nats.client.JetStreamApiException;
import io.nats.client.Nats;
import io.nats.client.api.StreamConfiguration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.*;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Duration;

public class IntegrationWithOrganisationServiceTest {
	private static final Logger mariadbLogger = LoggerFactory.getLogger("mariadb");
	private static final Logger videoApiLogger = LoggerFactory.getLogger("video-api");
	private static final Logger mockServerLogger = LoggerFactory.getLogger("mock-server");
	protected static final Logger newmanLogger = LoggerFactory.getLogger("newman");
	private static final Logger organisationLogger = LoggerFactory.getLogger("organisation");
	private static final Logger jetStreamLogger = LoggerFactory.getLogger("jetstream");
	private static final Logger logger = LoggerFactory.getLogger(IntegrationWithOrganisationServiceTest.class);

	protected static Network dockerNetwork;
	protected static GenericContainer<?> resourceContainer;
	protected static GenericContainer<?> videoApi;
	protected static Integer videoApiPort;
	protected static Integer videoAdminApiPort;
	protected static GenericContainer testOrganisationFrontend;
	private static GenericContainer natsService;
	private static String natsPath;
	private static MariaDBContainer mariadb;
	private static GenericContainer<?> jetStreamService;
	private static String jetStreamPath;
	private static final String natsSubjectSchedulingInfo = "schedulingInfo";
	private static final String natsSubjectAudit = "natsSubject";
	private static final MySQLContainer<?> mysql;

	private static final String DB_USER = "videouser";
	private static final String DB_PASSWORD = "secret1234";

	static {
		dockerNetwork = Network.newNetwork();

        resourceContainer = new GenericContainer<>(new ImageFromDockerfile()
                .withFileFromClasspath("/collections/medcom-video-api.postman_collection.json", "docker/collections/medcom-video-api.postman_collection.json")
                .withFileFromClasspath("/loop.sh", "loop.sh")
                .withDockerfileFromBuilder( builder -> builder.from("bash")
                        .add("/collections/medcom-video-api.postman_collection.json", "/collections/medcom-video-api.postman_collection.json")
                        .add("/loop.sh", "/loop.sh")
                        .volume("/collections")
                        .volume("/testresult")
                        .cmd("sh", "/loop.sh")
                        .build()));

        resourceContainer.start();
        System.out.println("Created: " + resourceContainer.isCreated());

		// SQL server for Video API.
		mariadb = (MariaDBContainer) new MariaDBContainer("mariadb:10.6")
				.withDatabaseName("videodb")
				.withUsername(DB_USER)
				.withPassword(DB_PASSWORD)
				.withNetwork(dockerNetwork)
				.withNetworkAliases("mariadb");
		mariadb.start();
		attachLogger(mariadb, mariadbLogger);

		// Mock server
		MockServerContainer userService = new MockServerContainer()
				.withNetwork(dockerNetwork)
				.withNetworkAliases("userservice");
		userService.start();
		attachLogger(userService, mockServerLogger);
		MockServerClient mockServerClient = new MockServerClient(userService.getContainerIpAddress(), userService.getMappedPort(1080));
		mockServerClient.when(HttpRequest.request().withMethod("GET"), Times.unlimited()).respond(getResponse());

		// Organisation mock server
		var organisationService = new MockServerContainer().
				withNetwork(dockerNetwork).
				withNetworkAliases("organisation");
		organisationService.start();
		attachLogger(organisationService, organisationLogger);
		mockServerClient = new MockServerClient(organisationService.getContainerIpAddress(), organisationService.getMappedPort(1080));
		mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisationtree").withQueryStringParameter("organisationCode", "pool-test-org")).respond(organisationTreeServiceResponse());
		mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisation").withQueryStringParameter("organisationCode", "pool-test-org")).respond(organisationServiceResponse("pool-test-org"));
		mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisation").withQueryStringParameter("organisationCode", "company 1")).respond(organisationServiceResponse("company 1"));
		mockServerClient.when(HttpRequest.request().withMethod("GET").withPath("/services/organisation").withQueryStringParameter("organisationCode", "company 3")).respond(organisationServiceResponse("company 1"));

		try {
			setupJetStream();
		} catch (JetStreamApiException | IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}

		// VideoAPI
		videoApi = new GenericContainer<>("kvalitetsit/medcom-video-api:latest")
				.withNetwork(dockerNetwork)
				.withNetworkAliases("videoapi")
				.withEnv("CONTEXT", "/api")
				.withEnv("jdbc_url", "jdbc:mariadb://mariadb:3306/videodb?useSSL=false&serverTimezone=UTC")
				.withEnv("jdbc_user", DB_USER)
				.withEnv("jdbc_pass", DB_PASSWORD)
				.withEnv("userservice_url", "http://userservice:1080")
				.withEnv("userservice_token_attribute_organisation", "organisation_id")
				.withEnv("userservice_token_attribute_username", "username")
				.withEnv("userservice.token.attribute.email", "email")
				.withEnv("userservice.token.attribute.userrole", "userrole")
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
				.withClasspathResourceMapping("db/migration/V901__insert _test_data.sql", "/app/sql/V901__insert _test_data.sql", BindMode.READ_ONLY)
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

				.withEnv("event.organisation.filter", "some_random_org_that_does_not_exist,new provisioner company")
				.withEnv("pool.fill.organisation.user", "some@email")
				.withEnv("pool.fill.organisation", "some_org")
				.withEnv("pool.fill.interval", "PT1M")

				.withClasspathResourceMapping("docker/logback-test.xml", "/configtemplates/logback.xml", BindMode.READ_ONLY)
				.withExposedPorts(8080, 8081)
				.withStartupTimeout(Duration.ofSeconds(180))
				.waitingFor(Wait.forListeningPort()).withStartupTimeout(Duration.ofSeconds(180));//(Wait.forHttp("/api/actuator/info").forStatusCode(200));
		videoApi.start();
		videoApiPort = videoApi.getMappedPort(8080);
		videoAdminApiPort = videoApi.getMappedPort(8081);
		attachLogger(videoApi, videoApiLogger);
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

	String getJdbcUrl() {
		return mariadb.getJdbcUrl();
	}

	public static void setupJetStream() throws JetStreamApiException, IOException, InterruptedException {
		var natsContainerName = "nats";
		var natsContainerVersion = "2.9-alpine";

		jetStreamService = new GenericContainer<>(natsContainerName + ":" + natsContainerVersion);

		jetStreamService.withNetwork(dockerNetwork)
				.withNetworkAliases("nats")
				.withExposedPorts(4222, 8222)
				.withCommand("-js")
				.waitingFor(new LogMessageWaitStrategy().withRegEx(".*Server is ready.*"));

		jetStreamService.start();
		attachLogger(jetStreamService, jetStreamLogger);

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

	protected static void attachLogger(GenericContainer container, Logger logger) {
		logger.info("Attaching logger to container: " + container.getContainerInfo().getName());

		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
		container.followOutput(logConsumer);
	}

	WebTarget getAdminClient() {
		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		provider.setMapper(objectMapper);

		return ClientBuilder.newClient(new ClientConfig(provider))
				.target(UriBuilder.fromUri(String.format("http://%s:%s/manage", videoApi.getContainerIpAddress(), videoAdminApiPort)));
	}

	WebTarget getClient() {
		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		provider.setMapper(objectMapper);

		return ClientBuilder.newClient(new ClientConfig(provider))
				.target(UriBuilder.fromUri(String.format("http://%s:%s/api", videoApi.getContainerIpAddress(), videoApiPort)));
	}

	private static HttpResponse getResponse() {
		return new HttpResponse().withBody("{\"UserAttributes\": {\"organisation_id\": [\"pool-test-org\"],\"email\":[\"eva@klak.dk\"],\"userrole\":[\"dk:medcom:role:admin\", \"dk:medcom:role:provisioner\"]}}").withHeaders(new Header("Content-Type", "application/json")).withStatusCode(200);
	}

	void verifyRowExistsInDatabase(String sql) throws SQLException {
		Connection conn = DriverManager.getConnection(mariadb.getJdbcUrl(), DB_USER, DB_PASSWORD);

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(sql);

		if(!rs.next()) {
			st.close();
			throw new RuntimeException("No rows found: " + sql);
		}
		st.close();
	}
}
