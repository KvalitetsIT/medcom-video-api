package dk.medcom.video.api.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Duration;

public class IntegrationWithOrganisationServiceTest {
	private static final Logger mysqlLogger = LoggerFactory.getLogger("mysql");
	private static final Logger videoApiLogger = LoggerFactory.getLogger("video-api");
	private static final Logger mockServerLogger = LoggerFactory.getLogger("mock-server");
	protected static final Logger newmanLogger = LoggerFactory.getLogger("newman");
	protected static final Logger natsLogger = LoggerFactory.getLogger("nats");

	private static final Logger logger = LoggerFactory.getLogger(IntegrationWithOrganisationServiceTest.class);

	protected static Network dockerNetwork;
	protected static GenericContainer resourceContainer;
	protected static GenericContainer videoApi;
	protected static Integer videoApiPort;
	protected static Integer videoAdminApiPort;
	protected static GenericContainer testOrganisationFrontend;
	private static GenericContainer natsService;
	private static String natsPath;
	private static MySQLContainer mysql;
	private static final String DB_USER = "videouser";
	private static final String DB_PASSWORD = "secret1234";

	static {
		dockerNetwork = Network.newNetwork();

		createOrganisationService(dockerNetwork);

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
		mysql = (MySQLContainer) new MySQLContainer("mysql:5.7")
				.withDatabaseName("videodb")
				.withUsername(DB_USER)
				.withPassword(DB_PASSWORD)
				.withNetwork(dockerNetwork)
				.withNetworkAliases("mysql");
		mysql.start();
		attachLogger(mysql, mysqlLogger);

		// Mock server
		MockServerContainer userService = new MockServerContainer()
				.withNetwork(dockerNetwork)
				.withNetworkAliases("userservice");
		userService.start();
		attachLogger(userService, mockServerLogger);
		MockServerClient mockServerClient = new MockServerClient(userService.getContainerIpAddress(), userService.getMappedPort(1080));
		mockServerClient.when(HttpRequest.request().withMethod("GET"), Times.unlimited()).respond(getResponse());

		setupNats();

		// VideoAPI
		videoApi = new GenericContainer<>("kvalitetsit/medcom-video-api:latest")
				.withNetwork(dockerNetwork)
				.withNetworkAliases("videoapi")
				.withEnv("CONTEXT", "/api")
				.withEnv("jdbc_url", "jdbc:mysql://mysql:3306/videodb?useSSL=false&serverTimezone=UTC")
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
				.withEnv("organisation.service.endpoint", "http://organisationfrontend:80/services")
				.withEnv("organisationtree.service.endpoint", "http://localhost:8080/api")
				.withEnv("short.link.base.url", "https://video.link/")
				.withEnv("overflow.pool.organisation.id", "overflow")

				.withEnv("ALLOWED_ORIGINS", "http://allowed:4100,http://allowed:4200")

				.withEnv("audit.nats.url", "nats://nats:4222")
				.withEnv("audit.nats.subject", "natsSubject")
				.withEnv("audit.nats.cluster.id", "test-cluster")
				.withEnv("audit.nats.client.id", "natsClientId")

				.withEnv("events.nats.url", "nats://nats:4222")
				.withEnv("events.nats.cluster.id", "test-cluster")
				.withEnv("events.nats.client.id", "natsClientId")
				.withEnv("events.nats.subject.scheduling-info", "schedulingInfo")

				.withEnv("event.organisation.filter", "some_random_org_that_does_not_exist")

				.withClasspathResourceMapping("docker/logback-test.xml", "/configtemplates/logback.xml", BindMode.READ_ONLY)
				.withExposedPorts(8080, 8081)
				.withStartupTimeout(Duration.ofSeconds(180))
				.waitingFor(Wait.forListeningPort()).withStartupTimeout(Duration.ofSeconds(180));//(Wait.forHttp("/api/actuator/info").forStatusCode(200));
		videoApi.start();
		videoApiPort = videoApi.getMappedPort(8080);
		videoAdminApiPort = videoApi.getMappedPort(8081);
		attachLogger(videoApi, videoApiLogger);
	}

	String getJdbcUrl() {
		return mysql.getJdbcUrl();
	}

	public static void setupNats() {
		var natsContainerName = "nats-streaming";
		var natsContainerVersion = "0.19.0";

		natsService = new GenericContainer<>(natsContainerName + ":" + natsContainerVersion)
				.withNetwork(dockerNetwork)
				.withNetworkAliases("nats")
				.withExposedPorts(4222)
				.withExposedPorts(8222)
				.waitingFor(new LogMessageWaitStrategy().withRegEx(".*Streaming Server is ready.*"))
				;

		natsService.start();
		attachLogger(natsService, natsLogger);

		natsPath = "nats://" + natsService.getContainerIpAddress() + ":" + natsService.getMappedPort(4222);
		var natsHttpPath = "http://" + natsService.getContainerIpAddress() + ":" + natsService.getMappedPort(8222);
		logger.info("NATS path: " + natsPath);
		logger.info("NATS http path: " + natsHttpPath);
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
		WebTarget target =  ClientBuilder.newClient(new ClientConfig(provider))
				.target(UriBuilder.fromUri(String.format("http://%s:%s/manage/", videoApi.getContainerIpAddress(), videoAdminApiPort)));

		return target;
	}

	WebTarget getClient() {
		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		provider.setMapper(objectMapper);
		WebTarget target =  ClientBuilder.newClient(new ClientConfig(provider))
				.target(UriBuilder.fromUri(String.format("http://%s:%s/api/", videoApi.getContainerIpAddress(), videoApiPort)));

		return target;
	}

	private static HttpResponse getResponse() {
		return new HttpResponse().withBody("{\"UserAttributes\": {\"organisation_id\": [\"pool-test-org\"],\"email\":[\"eva@klak.dk\"],\"userrole\":[\"dk:medcom:role:admin\", \"dk:medcom:role:provisioner\"]}}").withHeaders(new Header("Content-Type", "application/json")).withStatusCode(200);
	}

	private static void createOrganisationService(Network n) {
		MySQLContainer organisationMysql = (MySQLContainer) new MySQLContainer("mysql:5.7")
				.withDatabaseName("organisationdb")
				.withUsername("orguser")
				.withPassword("secret1234")
				.withNetwork(n)
				.withNetworkAliases("organisationdb")
				;

		organisationMysql.start();

		GenericContainer organisationContainer = new GenericContainer("kvalitetsit/medcom-vdx-organisation:0.0.3")
				.withNetwork(n)
				.withNetworkAliases("organisationservice")
				.withEnv("jdbc_url", "jdbc:mysql://organisationdb/organisationdb?serverTimezone=UTC")
				.withEnv("jdbc_user", "orguser")
				.withEnv("jdbc_pass", "secret1234")
				.withEnv("usercontext_header_name", "X-Test-Auth")
				.withEnv("userattributes_role_key", "UserRoles")
				.withEnv("userattributes_org_key", "organisation")
				.withEnv("userrole_admin_values", "adminrole")
				.withEnv("userrole_user_values", "userrole1,userrole2")
				.withEnv("userrole_monitor_values", "monitorrole")
				.withEnv("userrole_provisioner_values", "provisionerrole")
				.withEnv("spring.flyway.locations", "classpath:db/migration,filesystem:/app/sql")
				.withClasspathResourceMapping("organisation/V901__organisation_test_data.sql", "/app/sql/V901__organisation_test_data.sql", BindMode.READ_ONLY)
				.withStartupTimeout(Duration.ofSeconds(180))
				.withExposedPorts(8080)
				;

		organisationContainer.start();
		organisationContainer.withLogConsumer(outputFrame -> System.out.println(outputFrame));
		testOrganisationFrontend = new GenericContainer("kvalitetsit/gooioidwsrest:1.1.14")
				.withNetwork(n)
				.withNetworkAliases("organisationfrontend")
				.withCommand("-config", "/caddy/config.json")
				.withClasspathResourceMapping("organisation/caddy.json", "/caddy/config.json", BindMode.READ_ONLY)
				.withExposedPorts(80)
				.waitingFor(Wait.forLogMessage(".*", 1));

		testOrganisationFrontend.start();
	}

	void verifyRowExistsInDatabase(String sql) throws SQLException {
		Connection conn = DriverManager.getConnection(mysql.getJdbcUrl(), DB_USER, DB_PASSWORD);

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(sql);

		if(!rs.next()) {
			st.close();
			throw new RuntimeException("No rows found: " + sql);
		}
		st.close();
	}
}
