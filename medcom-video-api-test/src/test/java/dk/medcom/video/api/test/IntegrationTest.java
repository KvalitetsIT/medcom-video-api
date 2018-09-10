package dk.medcom.video.api.test;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.client.server.MockServerClient;
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


public class IntegrationTest {

	@Test
	public void testIntegrationTest() {
		
		// Given
		Network n = Network.newNetwork();

		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234").withNetwork(n).withNetworkAliases("mysql");
		mysql.start();

		MockServerContainer userService = new MockServerContainer().withNetwork(n).withNetworkAliases("userservice");
		userService.start();
		MockServerClient mockServerClient = userService.getClient();
		mockServerClient.when(Mockito.any(HttpRequest.class)).respond(getResponse());

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
				.waitingFor(Wait.forHttp("/api/info").forStatusCode(200));
		videoApi.start();

		
		// When
		GenericContainer newman = new GenericContainer<>("postman/newman_ubuntu1404:3.4.2")
				.withNetwork(n)
				.withClasspathResourceMapping("docker/collections/testmedcom.postman_environment.json", "/etc/postman/testmedcom.postman_environment.json", BindMode.READ_ONLY)
				.withClasspathResourceMapping("docker/collections/medcom-video-api.postman_collection.json", "/etc/postman/medcom-video-api.postman_collection.json", BindMode.READ_ONLY)
				.withCommand("-c /etc/postman/medcom-video-api.postman_collection.json -e /etc/postman/testmedcom.postman_environment.json");
		newman.start();
		
		ToStringConsumer toStringConsumer = new ToStringConsumer();
		newman.followOutput(toStringConsumer, OutputType.STDOUT);
				
		
		// Then
		System.out.println(toStringConsumer.toUtf8String());
	}

	private HttpResponse getResponse() {  

		return new HttpResponse().withBody("{\"organisation_id\":\"klak\",\"email\":\"eva@klak.dk\"}").withHeaders(new Header("Content-Type", "application/json"));
	}
}
