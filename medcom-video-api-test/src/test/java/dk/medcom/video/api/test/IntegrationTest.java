//package dk.medcom.video.api.test;
//
//import java.io.File;
//import java.time.Duration;
//
//import org.junit.ClassRule;
//import org.junit.Test;
//import org.testcontainers.containers.DockerComposeContainer;
//import org.testcontainers.containers.wait.strategy.Wait;
//
//public class IntegrationTest {
//
//	@ClassRule
//	public static DockerComposeContainer testEnvironment = new DockerComposeContainer(new File("src/test/resources/docker/test-compose.yml")).
//		withExposedService("videoapi-service", 8080, 
//			Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(200)));
//
//	@Test
//	public void testIntegrationTest() {
//		int servicePort = testEnvironment.getServicePort("videoapi-service", 8080);
//		String serviceUrl = "localhost:"+servicePort;
//
//		//docker run --network host -e "service_url=localhost:393"  -v /home/eva/ffproject/medcom-video-api/medcom-video-api-test/src/test/resources/docker/collections:/etc/postman  -t postman/newman_ubuntu1404:3.4.2  -c /etc/postman/medcom-video-api.postman_collection.json
//		System.out.println("Kuk");
////		DockerComposeContainer newman = new DockerComposeContainer(new File("src/test/resources/docker/runtest-compose.yml")).;
//		
//	}
//}
