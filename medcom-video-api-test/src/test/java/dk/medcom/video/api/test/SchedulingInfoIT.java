package dk.medcom.video.api.test;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.dao.entity.GuestMicrophone;
import dk.medcom.video.api.api.MeetingDto;
import dk.medcom.video.api.api.SchedulingInfoDto;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.VideoMeetingsApi;
import org.openapitools.client.api.VideoSchedulingInformationApi;
import org.openapitools.client.model.*;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulingInfoIT extends IntegrationWithOrganisationServiceTest {
	private final VideoSchedulingInformationApi schedulingInfoApi;

	public SchedulingInfoIT() {
		var apiClient = new ApiClient()
				.setBasePath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort))
				.setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

		schedulingInfoApi = new VideoSchedulingInformationApi(apiClient);
	}

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
		assertEquals("custom_portal_guest", result.getCustomPortalGuest());
		assertEquals("custom_portal_host", result.getCustomPortalHost());
		assertEquals("return_url", result.getReturnUrl());
		assertEquals(DirectMedia.NEVER.toString(), result.getDirectMedia().toString());
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

		assertTrue(result.contains("\"uriWithDomain\":\"1231@test.dk\""));
		assertTrue(result.contains("\"uriWithDomain\":\"1241@test.dk\""));
		assertFalse(result.contains("\"uriWithDomain\":\"1242@test.dk\""));
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

		assertTrue(result.contains("\"uriWithDomain\":\"1239@test.dk\""));
		assertTrue(result.contains("\"uriWithDomain\":\"1243@test.dk\""));
		assertFalse(result.contains("\"uriWithDomain\":\"1244@test.dk\""));
	}

	@Test
	public void testDeprovisionSchedulingInfo() throws ApiException, SQLException {
		// Create scheduling info.
		CreateSchedulingInfo createSchedulingInfo = new CreateSchedulingInfo();
		createSchedulingInfo.setOrganizationId("company 3");
		createSchedulingInfo.setSchedulingTemplateId(4L);

		var createdSchedulingInfo = schedulingInfoApi.schedulingInfoPost(createSchedulingInfo);
		verifyRowExistsInDatabase("select * from scheduling_info where uri_domain = 'test.dk' and uri_without_domain is not null and uuid = '" + createdSchedulingInfo.getUuid() + "'");

		// Deprovision(update) scheduling info
		UpdateSchedulingInfo updateSchedulingInfo = new UpdateSchedulingInfo();
		updateSchedulingInfo.setProvisionStatus(ProvisionStatus.DEPROVISION_OK);
		updateSchedulingInfo.setProvisionStatusDescription("DET GIK GODT");
		updateSchedulingInfo.setProvisionVmrId(UUID.randomUUID().toString());

		var updatedSchedulingInfo = schedulingInfoApi.schedulingInfoUuidPut(createdSchedulingInfo.getUuid(), updateSchedulingInfo);

		verifyRowExistsInDatabase("select * from scheduling_info where uri_domain is null and uri_without_domain is null and uuid = '" + updatedSchedulingInfo.getUuid() + "'");
	}

	@Test
	public void testReserveSchedulingInformation_DefaultValues() throws ApiException {
		var apiClient = new ApiClient()
				.setBasePath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort))
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
				.setBasePath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort))
				.setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

		var schedulingInfo = new VideoSchedulingInformationApi(apiClient);

		var result = schedulingInfo.schedulingInfoReserveGet(VmrType.LECTURE,null,null, VmrQuality.FULLHD,null,null,null,null,null);
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
				.setBasePath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort))
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

	@Test
	public void createSchedulingInfo() throws ApiException {
		CreateSchedulingInfo createSchedulingInfo = new CreateSchedulingInfo();
		createSchedulingInfo.setSchedulingTemplateId(1L);
		createSchedulingInfo.setOrganizationId("company 1");
		var createdSchedulingInfo = schedulingInfoApi.schedulingInfoPost(createSchedulingInfo);

		assertNotNull(createdSchedulingInfo);
		assertEquals("custom_portal_guest", createdSchedulingInfo.getCustomPortalGuest());
		assertEquals("custom_portal_host", createdSchedulingInfo.getCustomPortalHost());
		assertEquals("return_url", createdSchedulingInfo.getReturnUrl());

		var readSchedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(createdSchedulingInfo.getUuid());
		assertNotNull(readSchedulingInfo);
		assertEquals("custom_portal_guest", createdSchedulingInfo.getCustomPortalGuest());
		assertEquals("custom_portal_host", createdSchedulingInfo.getCustomPortalHost());
		assertEquals("return_url", createdSchedulingInfo.getReturnUrl());
	}
}
