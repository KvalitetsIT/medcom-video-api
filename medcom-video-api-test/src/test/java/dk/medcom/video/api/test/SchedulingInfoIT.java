package dk.medcom.video.api.test;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.api.GuestMicrophone;
import dk.medcom.video.api.api.MeetingDto;
import dk.medcom.video.api.api.SchedulingInfoDto;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.VideoMeetingsApi;
import io.swagger.client.api.VideoSchedulingInformationApi;
import io.swagger.client.model.CreateMeeting;
import io.swagger.client.model.VmrQuality;
import io.swagger.client.model.VmrType;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

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
	public void testGetSchedulingInfoProvisionDefaultValues() {
		// Given
		var createMeeting = new CreateMeetingDto();
		createMeeting.setDescription("This is a description");
		var now = Calendar.getInstance();
		Calendar start = (Calendar) now.clone();
		start.add(Calendar.MINUTE, 3);
		Calendar end = (Calendar) now.clone();
		end.add(Calendar.HOUR, 1);

		createMeeting.setStartTime(start.getTime());
		createMeeting.setEndTime(end.getTime());
		createMeeting.setSubject("This is a subject!");
		createMeeting.setGuestMicrophone(GuestMicrophone.muted);

		var createResponse = getClient()
				.path("meetings")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(createMeeting, MediaType.APPLICATION_JSON_TYPE), MeetingDto.class);

		assertNotNull(createResponse.getUuid());

		//When
		var result = getClient()
				.path("scheduling-info-provision")
				.request()
				.get(String.class);

		//Then
		assertNotNull(result);
		assertTrue(result.contains("\"vmrType\":\"conference\",\"hostView\":\"one_main_seven_pips\",\"guestView\":\"one_main_seven_pips\",\"vmrQuality\":\"sd\",\"enableOverlayText\":true,\"guestsCanPresent\":true,\"forcePresenterIntoMain\":true,\"forceEncryption\":false,\"muteAllGuests\":false"));
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

	@Test
	public void testReserveSchedulingInformation_DefaultValues() throws ApiException {
		var apiClient = new ApiClient()
				.setBasePath(String.format("http://%s:%s/api/", videoApi.getContainerIpAddress(), videoApiPort))
				.setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

		var schedulingInfo = new VideoSchedulingInformationApi(apiClient);

		var result = schedulingInfo.schedulingInfoReserveGet(null,null,null,null,null,null,null,null,null);
		assertNotNull(result);
		assertNotNull(result.getReservationId());

		var reservationId = result.getReservationId();

		result = schedulingInfo.schedulingInfoReserveUuidGet(result.getReservationId());
		assertNotNull(result);
		assertEquals(reservationId, result.getReservationId());
	}

	@Test
	public void testReserveSchedulingInformation() throws ApiException {
		var apiClient = new ApiClient()
				.setBasePath(String.format("http://%s:%s/api/", videoApi.getContainerIpAddress(), videoApiPort))
				.setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

		var schedulingInfo = new VideoSchedulingInformationApi(apiClient);

		var result = schedulingInfo.schedulingInfoReserveGet(VmrType.LECTURE,null,null, VmrQuality.FULL_HD,null,null,null,null,null);
		assertNotNull(result);
		assertNotNull(result.getReservationId());

		var reservationId = result.getReservationId();

		result = schedulingInfo.schedulingInfoReserveUuidGet(result.getReservationId());
		assertNotNull(result);
		assertEquals(reservationId, result.getReservationId());
	}

	@Test
	public void testUseReservedSchedulingInfo() throws ApiException {
		var apiClient = new ApiClient()
				.setBasePath(String.format("http://%s:%s/api/", videoApi.getContainerIpAddress(), videoApiPort))
				.setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

		var videoMeetingApi = new VideoMeetingsApi(apiClient);
		var schedulingInfoApi = new VideoSchedulingInformationApi(apiClient);

		var schedulingInfo = schedulingInfoApi.schedulingInfoReserveGet(null,null,null,null,null,null,null,null,null);
		assertNotNull(schedulingInfo);
		var reservationId = schedulingInfo.getReservationId();

		var createMeeting = new CreateMeeting();
		createMeeting.setDescription("This is a description");
		createMeeting.setStartTime(OffsetDateTime.now());
		createMeeting.setEndTime(OffsetDateTime.now().plusHours(2));
		createMeeting.setSubject("This is a subject!");
		createMeeting.setSchedulingInfoReservationId(schedulingInfo.getReservationId());

		var result = videoMeetingApi.meetingsPost(createMeeting);
		assertNotNull(result);

		schedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(result.getUuid());
		assertNotNull(schedulingInfo);
		assertEquals(reservationId, schedulingInfo.getReservationId());
	}
}
