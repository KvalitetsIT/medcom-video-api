package dk.medcom.video.api.test;

import dk.medcom.video.api.dto.SchedulingInfoDto;
import org.junit.Test;

import static org.junit.Assert.*;

public class SchedulingInfoIT extends IntegrationWithOrganisationServiceTest {

	@Test
	public void testCanReadSchedulingInfo() {
		var result = getClient()
				.path("scheduling-info")
				.path("7cc82183-0d47-439a-a00c-38f7a5a01fc3")
				.request()
				.get(SchedulingInfoDto.class);

		assertNotNull(result);
		assertNotNull(result.meetingDetails.getShortId());
		assertEquals("https://video.link/" + result.meetingDetails.getShortId(), result.getShortLink());
		assertEquals(result.getShortLink(), result.getShortlink());
		assertEquals(result.meetingDetails.getShortLink(), result.meetingDetails.getShortlink());
	}

	@Test
	public void testGetSchedulingInfoProvision() {
		var result = getClient()
				.path("scheduling-info-provision")
				.request()
				.get(String.class);

		assertNotNull(result);
		assertTrue(result.contains("AWAITS_PROVISION"));
		assertFalse(result.contains("PROVISIONED_OK"));
	}

	@Test
	public void testGetSchedulingInfoDeProvision() {
		var result = getClient()
				.path("scheduling-info-deprovision")
				.request()
				.get(String.class);

		assertNotNull(result);
		assertFalse(result.contains("AWAITS_PROVISION"));
		assertTrue(result.contains("PROVISIONED_OK"));
	}
}
