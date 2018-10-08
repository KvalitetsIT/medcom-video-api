package dk.medcom.video.api.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;

@RunWith(TestRunner.class)
public class IntegrationTest {

/*	@BeforeClass
	public static void testIntegrationTest() throws UnsupportedOperationException, IOException, InterruptedException {
		
		// Given
		Network n = Network.newNetwork();

		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234").withNetwork(n).withNetworkAliases("mysql");
		mysql.start();


		
		GenericContainer us = new GenericContainer<>("kuk/luk:latest")
		.withNetwork(n)
		.withNetworkAliases("userservice");
		us.start();

		
		GenericContainer videoApi = new GenericContainer<>("kvalitetsit/medcom-video-api-web:latest")
				.withNetwork(n)
				.withNetworkAliases("videoapi")
				.withEnv("CONTEXT", "/api")
				.withEnv("jdbc_url", "jdbc:mysql://mysql:3306/videodb")
				.withEnv("jdbc_user", "videouser")
				.withEnv("jdbc_pass", "secret1234")
				.withEnv("userservice_url", "http://userservice:9200")
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
				.withEnv("mapping.role.provisioner", "dk:medcom:role:provisioner")
				.withEnv("mapping.role.admin", "dk:medcom:role:admin")
				.withEnv("mapping.role.user", "dk:medcom:role:user")
				.withExposedPorts(8080)
				.waitingFor(Wait.forHttp("/api/info").forStatusCode(200));
		videoApi.start();

		
	}
*/
	@Test
	public void test() throws InterruptedException {
		
	}
	
	private static HttpResponse getResponse() {  

		return new HttpResponse().withBody("{\"organisation_id\":\"klak\",\"email\":\"eva@klak.dk\"}").withHeaders(new Header("Content-Type", "application/json")).withStatusCode(200);
	}
}

