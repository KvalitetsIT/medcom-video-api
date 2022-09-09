package dk.medcom.video.api.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class PrometeusIT extends IntegrationWithOrganisationServiceTest {
	@Test
	public void testCanReadPrometheusEndpoint() {
		var result = getAdminClient().path("actuator").path("prometheus")
				.request()
				.get(String.class);

		assertNotNull(result);
	}

	@Test
	public void testPrometheusConnection(){
		var result = getAdminClient().path("actuator").path("prometheus")
				.request().get(String.class);
		assertNotNull(result);
		assertTrue(result.contains("application_information"));
	}

}
