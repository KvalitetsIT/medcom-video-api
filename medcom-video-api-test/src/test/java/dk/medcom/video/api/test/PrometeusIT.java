package dk.medcom.video.api.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class PrometeusIT extends IntegrationWithOrganisationServiceTest {

	
	
	@Test
	public void testCanReadPrometheusEndpoint() {
		var result = getAdminClient().path("actuator").path("prometheus")
				.request()
				.get(String.class);

		assertNotNull(result);
	}

}
