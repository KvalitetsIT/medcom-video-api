package dk.medcom.video.api.test;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.*;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

@Ignore
public class IntegrationTest {
	private static final Logger mariaDbLogger = LoggerFactory.getLogger("MariaDb");
	private static final Logger videoApiLogger = LoggerFactory.getLogger("video-api");
	private static final Logger mockServerLogger = LoggerFactory.getLogger("mock-server");
	private static final Logger newmanLogger = LoggerFactory.getLogger("newman");

	private static Network dockerNetwork;
	private static GenericContainer<?> resourceContainer;
	private static GenericContainer<?> videoApi;
	private static Integer videoApiPort;

	@BeforeClass
	public static void setup() {
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
		var mariadb = new MariaDBContainer<>("mariadb:10.6")
				.withDatabaseName("videodb")
				.withUsername("videouser")
				.withPassword("secret1234")
				.withNetwork(dockerNetwork)
				.withNetworkAliases("mariadb");
		mariadb.start();
		attachLogger(mariadb, mariaDbLogger);

		// Mock server
		MockServerContainer userService = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"))
				.withNetwork(dockerNetwork)
				.withNetworkAliases("userservice");
		userService.start();
		attachLogger(userService, mockServerLogger);
		MockServerClient mockServerClient = new MockServerClient(userService.getHost(), userService.getMappedPort(1080));
		mockServerClient.when(HttpRequest.request().withMethod("GET"), Times.unlimited()).respond(getResponse());

		// VideoAPI
		videoApi = new GenericContainer<>("kvalitetsit/medcom-video-api:latest")
				.withNetwork(dockerNetwork)
				.withNetworkAliases("videoapi")
				.withEnv("CONTEXT", "/api")
				.withEnv("jdbc_url", "jdbc:mariadb://mariadb:3306/videodb?useSSL=false&serverTimezone=UTC")
				.withEnv("jdbc_user", "videouser")
				.withEnv("jdbc_pass", "secret1234")
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
				//.withEnv("LOG_LEVEL", "debug")
				.withEnv("spring.flyway.locations", "classpath:db/migration,filesystem:/app/sql")
				.withClasspathResourceMapping("db/migration/V901__insert _test_data.sql", "/app/sql/V901__insert _test_data.sql", BindMode.READ_ONLY)
				.withEnv("organisation.service.enabled", "false")
				.withEnv("organisation.service.endpoint", "http://localhost:8080")
				.withEnv("short.link.base.url", "https://video.link/")
				.withExposedPorts(8080)
				.waitingFor(Wait.forHttp("/api/actuator/info").forStatusCode(200));
		videoApi.start();
		videoApiPort = videoApi.getMappedPort(8080);
		attachLogger(videoApi, videoApiLogger);
	}

	private static void attachLogger(GenericContainer<?> container, Logger logger) {
		logger.info("Attaching logger to container: " + container.getContainerInfo().getName());
		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
		container.followOutput(logConsumer);
	}

	@Test
	public void verifyTestResults() throws InterruptedException, IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		Thread.sleep(5000);
		TemporaryFolder folder = new TemporaryFolder();
		folder.create();

		GenericContainer<?> newman = new GenericContainer<>("postman/newman_ubuntu1404:4.1.0")
					.withNetwork(dockerNetwork)
					.withVolumesFrom(resourceContainer, BindMode.READ_WRITE)
					.withCommand("run /collections/medcom-video-api.postman_collection.json -r cli,junit --reporter-junit-export /testresult/TEST-dk.medcom.video.api.test.IntegrationTest.xml --global-var host=videoapi --global-var port=8080");

		newman.start();
		attachLogger(newman, newmanLogger);

		long waitTime = 500;
		long loopLimit = 60;

		for(int i = 0; newman.isRunning() && i < loopLimit; i++) {
			System.out.println(i);
			System.out.println("Waiting....");
			Thread.sleep(waitTime);
		}

		resourceContainer.copyFileFromContainer("/testresult/TEST-dk.medcom.video.api.test.IntegrationTest.xml", folder.getRoot().getCanonicalPath() + "/TEST-dk.medcom.video.api.test.IntegrationTest.xml");

		FileInputStream input = new FileInputStream(folder.getRoot().getCanonicalPath() + "/TEST-dk.medcom.video.api.test.IntegrationTest.xml");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(input);
		XPath xPath = XPathFactory.newInstance().newXPath();
		String failureExpression = "/testsuites/testsuite/@failures";
		String errorExpression = "/testsuites/testsuite/@errrors";

		int failures = ((Double) xPath.compile(failureExpression).evaluate(xmlDocument, XPathConstants.NUMBER)).intValue();
		int errors = ((Double) xPath.compile(errorExpression).evaluate(xmlDocument, XPathConstants.NUMBER)).intValue();

		if(errors != 0 || failures != 0) {
			StringBuilder stringBuilder = new StringBuilder();
			Files.readAllLines(Paths.get(folder.getRoot().getCanonicalPath() + "/TEST-dk.medcom.video.api.test.IntegrationTest.xml")).forEach(x -> stringBuilder.append(x).append(System.lineSeparator()));
			System.out.println(stringBuilder);
		}

		assertEquals(0, failures);
		assertEquals(0, errors);
	}

	@Test(expected = ForbiddenException.class)
	public void testCanNotReadOtherOrganisation()  {
		String result = getClient()
				.path("meetings")
				.path("7cc82183-0d47-439a-a00c-38f7a5a01fc1")
				.request()
				.get(String.class);
	}

	@Test
	public void testCanReadMeeting() {
		String result = getClient()
				.path("meetings")
				.path("7cc82183-0d47-439a-a00c-38f7a5a01fc2")
				.request()
				.get(String.class);
	}

	WebTarget getClient() {

		return ClientBuilder.newClient()
				.target(UriBuilder.fromUri(String.format("http://%s:%s/api/", videoApi.getHost(), videoApiPort)));
	}

	private static HttpResponse getResponse() {
		return new HttpResponse().withBody("{\"UserAttributes\": {\"organisation_id\": [\"pool-test-org\"],\"email\":[\"eva@klak.dk\"],\"userrole\":[\"dk:medcom:role:admin\"]}}").withHeaders(new Header("Content-Type", "application/json")).withStatusCode(200);
	}
}

