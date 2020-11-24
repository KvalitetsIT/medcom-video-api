package dk.medcom.video.api.test;

import dk.medcom.video.api.dto.SchedulingInfoDto;
import io.swagger.client.ApiException;
import io.swagger.client.model.CreateMeeting;
import io.swagger.client.model.MeetingType;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

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
	public void testGetSchedulingInfoProvision() throws ApiException {
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

	private CreateMeeting createMeeting(String externalId) {
		var createMeeting = new CreateMeeting();
		createMeeting.setDescription("This is a description");
		var now = Calendar.getInstance();
		var inTwoHours = createDate(now, 2);

		createMeeting.setStartTime(OffsetDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault()));
		createMeeting.setEndTime(OffsetDateTime.ofInstant(inTwoHours.toInstant(), ZoneId.systemDefault()));
		createMeeting.setSubject("This is a subject!");
		createMeeting.setExternalId(externalId);
		createMeeting.setMeetingType(MeetingType.POOL);
		createMeeting.setSchedulingTemplateId(1);

		return createMeeting;
	}

	private Date createDate(Calendar calendar, int hoursToAdd) {
		Calendar cal = (Calendar) calendar.clone();
		cal.add(Calendar.HOUR, hoursToAdd);

		return cal.getTime();
	}
}
