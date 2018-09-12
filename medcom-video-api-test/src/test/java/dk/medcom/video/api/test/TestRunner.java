package dk.medcom.video.api.test;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame.OutputType;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class TestRunner extends Runner {

	private Class testClass;
	
	public TestRunner(Class testClass) {
		super();
		this.testClass = testClass;
	}

	@Override
	public Description getDescription() {
		return Description
				.createTestDescription(testClass, "My runner description");
	}

	@Override
	public void run(RunNotifier notifier) {
		Network n = setup();
		
		GenericContainer newman = new GenericContainer<>("postman/newman_ubuntu1404:4.1.0")
				.withNetwork(n)
				.withFileSystemBind("/home/eva/ffproject/medcom-video-api/medcom-video-api-test/src/test/resources/output", "/testresult", BindMode.READ_WRITE)
				.withClasspathResourceMapping("docker/collections/medcom-video-api.postman_collection.json", "/etc/postman/test_collection.json", BindMode.READ_ONLY)
		.withCommand("run /etc/postman/test_collection.json -r junit --reporter-junit-export /testresult/junit-result3.xml --global-var host=videoapi:8080");
		newman.start();
		// TODO Auto-generated method stub

		ToStringConsumer toStringConsumer = new ToStringConsumer();
		newman.followOutput(toStringConsumer, OutputType.STDOUT);
		// Then
		System.out.println("*****************"+toStringConsumer.toUtf8String());

//		docker run --network f32fb72b08e7 -v /home/eva/ffproject/medcom-video-api/medcom-video-api-test/src/test/resources/docker/collections:/etc/postman -v /home/eva/ffproject/medcom-video-api/medcom-video-api-test/src/test/resources/docker/bla:/bla -t postman/newman_ubuntu1404:4.1.0 run /etc/postman/medcom-video-api.postman_collection.json --global-var service_url=videoapi:8080  -r junit --reporter-junit-export /bla/blabla.xml
	}


	public Network setup() {
		Network n = Network.newNetwork();

		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234").withNetwork(n).withNetworkAliases("mysql");
		mysql.start();

		MockServerContainer userService = new MockServerContainer().withNetwork(n).withNetworkAliases("userservice");
		userService.start();
		MockServerClient mockServerClient = userService.getClient();
		mockServerClient.when(HttpRequest.request().withMethod("GET"), Times.unlimited()).respond(getResponse());

		GenericContainer videoApi = new GenericContainer<>("kvalitetsit/medcom-video-api-web:latest")
				.withNetwork(n)
				.withNetworkAliases("videoapi")
				.withEnv("CONTEXT", "/api")
				.withEnv("jdbc_url", "jdbc:mysql://mysql:3306/videodb")
				.withEnv("jdbc_user", "videouser")
				.withEnv("jdbc_pass", "secret1234")
				.withEnv("userservice_url", "http://userservice")
				.withEnv("userservice_token_attribute_organisation", "organisation_id")
				.withEnv("userservice_token_attribute_username", "username")
				.withEnv("userservice.token.attribute.email", "email")
				.withExposedPorts(8080)
				.waitingFor(Wait.forHttp("/api/info").forStatusCode(200));
		videoApi.start();

		return n;
	}

	
	private static HttpResponse getResponse() {  
		return new HttpResponse().withBody("{\"organisation_id\":\"klak\",\"email\":\"eva@klak.dk\"}").withHeaders(new Header("Content-Type", "application/json")).withStatusCode(200);
	}
}
