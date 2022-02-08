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
	public void testCanReadAppMetricEndpoint() {
		var result1 = getAdminClient().path("actuator").path("appmetrics")
				.request()
				.get(String.class);
		var result2 = getAdminClient().path("actuator").path("appmetrics")
				.request()
				.get(String.class);

		assertNotNull(result1);
		assertNotNull(result2);
		assertEquals(result1, result2);
	}

	@Test
	public void testPrometheusConnection(){
		var result = getAdminClient().path("actuator").path("prometheus")
				.request().get(String.class);
		assertNotNull(result);
		assertTrue(result.contains("application_information"));
	}

}
