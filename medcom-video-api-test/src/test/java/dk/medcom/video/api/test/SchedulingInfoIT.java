package dk.medcom.video.api.test;

import dk.medcom.video.api.dto.SchedulingInfoDto;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
}
