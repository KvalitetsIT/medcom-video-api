package dk.medcom.video.api.test;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.dao.entity.GuestMicrophone;
import dk.medcom.video.api.api.MeetingDto;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.UriBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.InfoApi;
import org.openapitools.client.api.VideoMeetingsApi;
import org.openapitools.client.api.VideoSchedulingInformationApi;
import org.openapitools.client.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class MeetingIT extends IntegrationWithOrganisationServiceTest {
	private VideoMeetingsApi videoMeetings;
	private VideoSchedulingInformationApi schedulingInfoApi;
	private InfoApi infoApi;

	@BeforeEach
	public void setupApiClient() {
		var apiClient = new ApiClient()
				.setBasePath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort))
				.setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

		videoMeetings = new VideoMeetingsApi(apiClient);

		infoApi = new InfoApi(apiClient);

		schedulingInfoApi = new VideoSchedulingInformationApi(apiClient);
	}

	@Test
	public void testCanNotReadOtherOrganisation()  {
		assertThrows(ForbiddenException.class, () -> getClient().path("meetings")
				.path("7cc82183-0d47-439a-a00c-38f7a5a01fc1")
				.request()
				.get(String.class));
	}

	@Test
	public void testCanReadMeeting() {
		var result = getClient()
				.path("meetings")
				.path("7cc82183-0d47-439a-a00c-38f7a5a01fc2")
				.request()
				.get(MeetingDto.class);

		assertNotNull(result);
		assertEquals(12, result.getShortId().length());
		assertEquals("https://video.link/" + result.getShortId(), result.getShortLink());
		assertEquals("external_id", result.getExternalId());
	}

	@Test
	public void testCanReadMeetingWithTrailingSlash() {
		var result = getClient()
				.path("meetings")
				.path("7cc82183-0d47-439a-a00c-38f7a5a01fc2/")
				.request()
				.get(MeetingDto.class);

		assertNotNull(result);
		assertEquals(12, result.getShortId().length());
		assertEquals("https://video.link/" + result.getShortId(), result.getShortLink());
		assertEquals("external_id", result.getExternalId());
	}

	@Test
	public void testUriWithDomain() throws ApiException {
		var createMeeting = createMeeting(UUID.randomUUID().toString());
		var createdMeeting = videoMeetings.meetingsPost(createMeeting);
		var schedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(createdMeeting.getUuid());
		// 1236@test.dk 1238
		var result = videoMeetings.meetingsFindByUriWithDomainGet(schedulingInfo.getUriWithDomain());

		assertNotNull(result);
		assertEquals(createdMeeting.getUuid(), result.getUuid());
		assertNull(schedulingInfo.getCustomPortalGuest());
		assertNull(schedulingInfo.getCustomPortalHost());
		assertNull(schedulingInfo.getReturnUrl());
	}

	@Test
	public void testUriWithoutDomain() throws ApiException {
		var createMeeting = createMeeting(UUID.randomUUID().toString());
		var createdMeeting = videoMeetings.meetingsPost(createMeeting);
		var schedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(createdMeeting.getUuid());
		var result = videoMeetings.meetingsFindByUriWithoutDomainGet(schedulingInfo.getUriWithoutDomain());

		assertNotNull(result);
		assertEquals(createdMeeting.getUuid(), result.getUuid());
	}

	@Test
	public void testCanCreatePoolMeeting() throws ApiException {
		var createMeeting = createMeeting(UUID.randomUUID().toString());
		createMeeting.meetingType(MeetingType.POOL);
		var result = videoMeetings.meetingsPost(createMeeting);

		assertNotNull(result);
	}

	@Test
	public void testCanCreateUpdateAndReadMeeting() throws ApiException {
		var createMeeting = createMeeting(UUID.randomUUID().toString());
		createMeeting.setLabels(new ArrayList<>());
		createMeeting.getLabels().add("Label One");
		createMeeting.getLabels().add("Label Two");
		createMeeting.setUriWithoutDomain("12345");
		createMeeting.setGuestPin(4321);
		createMeeting.setHostPin(1234);
		createMeeting.setAdditionalInformation(new ArrayList<>());
		createMeeting.getAdditionalInformation().add(createAdditionalInformationType("key one", "value one"));
		createMeeting.getAdditionalInformation().add(createAdditionalInformationType("key two", "value two"));

		var createdMeeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(createdMeeting);
		assertEquals(createMeeting.getExternalId(), createdMeeting.getExternalId());
		assertEquals(2, createdMeeting.getLabels().size());
		assertTrue(createMeeting.getLabels().containsAll(createdMeeting.getLabels()));
		assertTrue(createMeeting.getAdditionalInformation().containsAll(createdMeeting.getAdditionalInformation()));

		var schedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(createdMeeting.getUuid());
		assertNotNull(schedulingInfo);
		assertEquals(createMeeting.getUriWithoutDomain(), schedulingInfo.getUriWithoutDomain());
		assertTrue(schedulingInfo.getUriWithDomain().startsWith(createMeeting.getUriWithoutDomain()));
		assertEquals(createMeeting.getHostPin().longValue(), schedulingInfo.getHostPin().longValue());
		assertEquals(createMeeting.getGuestPin().longValue(), schedulingInfo.getGuestPin().longValue());

		var updateMeeting = new UpdateMeeting();
		updateMeeting.setSubject("SUBJECT");
		updateMeeting.setStartTime(OffsetDateTime.now());
		updateMeeting.setEndTime(OffsetDateTime.now().plusHours(1));
		updateMeeting.setLabels(new ArrayList<>());
		updateMeeting.getLabels().add("Another Label");

		var updatedMeeting = videoMeetings.meetingsUuidPut(createdMeeting.getUuid(), updateMeeting);
		assertNotNull(updatedMeeting);
		assertEquals(createMeeting.getExternalId(), updatedMeeting.getExternalId());
		assertEquals(1, updatedMeeting.getLabels().size());
		assertTrue(updateMeeting.getLabels().containsAll(updatedMeeting.getLabels()));
		assertTrue(updatedMeeting.getAdditionalInformation().isEmpty());

		var readMeeting = videoMeetings.meetingsUuidGet(createdMeeting.getUuid());
		assertNotNull(readMeeting);
		assertEquals(createMeeting.getExternalId(), readMeeting.getExternalId());
		assertEquals(1, readMeeting.getLabels().size());
		assertTrue(updateMeeting.getLabels().containsAll(readMeeting.getLabels()));
		assertTrue(readMeeting.getAdditionalInformation().isEmpty());
	}

	@Test
	public void testCanCreateExternalId() throws ApiException {
		var createMeeting = createMeeting("another_external_id");

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
	}

	@Test
	public void testCanCreateWithMicIsMuted() throws ApiException {
		var createMeeting = createMeeting("another_external_id3");
		createMeeting.setGuestMicrophone(org.openapitools.client.model.GuestMicrophone.MUTED);

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertEquals(org.openapitools.client.model.GuestMicrophone.MUTED, meeting.getGuestMicrophone());
	}

	@Test
	public void testCanCreateWithMicNotSet() throws ApiException {
		var createMeeting = createMeeting("another_external_id2");

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertEquals(org.openapitools.client.model.GuestMicrophone.ON, meeting.getGuestMicrophone());
	}

	@Test
	public void testCanCreateWithGuestPinRequiredNotSet() throws ApiException {
		var createMeeting = createMeeting("another_external_id4");

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertFalse(meeting.getGuestPinRequired());
	}

	@Test
	public void testCanCreateWithGuestPinRequiredSetFalse() throws ApiException {
		var createMeeting = createMeeting("another_external_id5");
		createMeeting.setGuestPinRequired(false);

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertFalse(meeting.getGuestPinRequired());
	}

	@Test
	public void testCanCreateWithGuestPinRequiredSetTrue() throws ApiException {
		var createMeeting = createMeeting("another_external_id6");
		createMeeting.setGuestPinRequired(true);

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertTrue(meeting.getGuestPinRequired());
	}

	@Test
	public void testUniqueOrganisationExternalId() {
		var createMeeting = createMeeting("external_id");

		try {
			videoMeetings.meetingsPost(createMeeting);
			fail();
		}
		catch(ApiException e) {
			assertEquals(400, e.getCode());
			assertTrue(e.getResponseBody().contains("\"errorCode\":12"));
			assertTrue(e.getResponseBody().contains("\"errorText\":\"ExternalId not unique within organisation.\""));
		}
	}

	@Test
	public void testGuestMicIsMuted() {
		var result = getClient()
				.path("meetings")
				.path("7cc82183-0d47-439a-a00c-38f7a5a01fc2")
				.request()
				.get(MeetingDto.class);

		assertNotNull(result);
		assertEquals("external_id", result.getExternalId());
		assertEquals(GuestMicrophone.muted, result.getGuestMicrophone());
	}

	@Test
	public void testGuestMicIsNull() {
		var result = getClient()
				.path("meetings")
				.path("7cc82183-0d47-439a-a00c-38f7a5a01fc3")
				.request()
				.get(MeetingDto.class);

		assertNotNull(result);
		assertEquals("external_id_2", result.getExternalId());
		assertNull(result.getGuestMicrophone());
	}

	private CreateMeeting createMeeting(String externalId) {
		var createMeeting = new CreateMeeting();
		createMeeting.setDescription("This is a description");
		var now = Calendar.getInstance();
		var inOneHour = createDate(now, 1);
		var inTwoHours = createDate(now, 2);

		createMeeting.setStartTime(OffsetDateTime.ofInstant(inOneHour.toInstant(), ZoneId.systemDefault()));
		createMeeting.setEndTime(OffsetDateTime.ofInstant(inTwoHours.toInstant(), ZoneId.systemDefault()));
		createMeeting.setSubject("This is a subject!");
		createMeeting.setExternalId(externalId);

		return createMeeting;
	}

	private AdditionalInformationType createAdditionalInformationType(String key, String value) {
		var additionalInfo = new AdditionalInformationType();
		additionalInfo.setKey(key);
		additionalInfo.setValue(value);

		return additionalInfo;
	}

	@Test
	public void testCanCreateMeetingAndSearchByShortId() {
		var createMeeting = new CreateMeetingDto();
		createMeeting.setDescription("This is a description");
		var now = Calendar.getInstance();
		var inOneHour = createDate(now, 1);
		var inTwoHours = createDate(now, 2);

		createMeeting.setStartTime(inOneHour);
		createMeeting.setEndTime(inTwoHours);
		createMeeting.setSubject("This is a subject!");

		var response = getClient()
				.path("meetings")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(createMeeting, MediaType.APPLICATION_JSON_TYPE), MeetingDto.class);

		assertNotNull(response.getUuid());

		var searchResponse = getClient()
				.path("meetings")
				.queryParam("short-id", response.getShortId()) // short id
				.request()
				.get(MeetingDto.class);

		assertNotNull(searchResponse);
		assertEquals(response.getUuid(), searchResponse.getUuid());
//		assertEquals("https://video.link/" + searchResponse.getShortId(), searchResponse.getShortLink());
	}

	@Test
	public void testPatchUpdate() throws ApiException {
		// Given
		var createMeeting = new CreateMeetingDto();
		createMeeting.setDescription("This is a description");
		var now = Calendar.getInstance();
		var inOneHour = createDate(now, 1);
		var inTwoHours = createDate(now, 2);

		createMeeting.setStartTime(inOneHour);
		createMeeting.setEndTime(inTwoHours);
		createMeeting.setSubject("This is a subject!");
		createMeeting.setGuestMicrophone(GuestMicrophone.muted);
		createMeeting.setAdditionalInformation(List.of(new dk.medcom.video.api.api.AdditionalInformationType("key", "value")));

		var createResponse = getClient()
				.path("meetings")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(createMeeting, MediaType.APPLICATION_JSON_TYPE), MeetingDto.class);

		assertNotNull(createResponse.getUuid());

		var originalSchedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(UUID.fromString(createResponse.getUuid()));

		// When
		var request = new PatchMeeting();
		request.setDescription("SOME DESCRIPTION");
		request.setGuestPinRequired(true);
		request.setAdditionalInformation(List.of(createAdditionalInformationType("new key", "new value"), createAdditionalInformationType("another key", "another value")));
		var response = videoMeetings.meetingsUuidPatch(UUID.fromString(createResponse.getUuid()), request);
		var updatedSchedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(UUID.fromString(createResponse.getUuid()));

		// Then
		assertNotNull(response);

		var getResponse = videoMeetings.meetingsUuidGet(UUID.fromString(createResponse.getUuid()));

		assertNotNull(getResponse);
		assertNotEquals(createMeeting.getDescription(), getResponse.getDescription());
		assertNotEquals(request.getEndTime(), getResponse.getEndTime());
		assertEquals(org.openapitools.client.model.GuestMicrophone.MUTED, getResponse.getGuestMicrophone());
		assertEquals(true, getResponse.getGuestPinRequired());
		assertEquals(2, getResponse.getAdditionalInformation().size());
		assertTrue(getResponse.getAdditionalInformation().containsAll(request.getAdditionalInformation()));
		assertEquals(originalSchedulingInfo.getHostPin(), updatedSchedulingInfo.getHostPin());
		assertEquals(originalSchedulingInfo.getGuestPin(), updatedSchedulingInfo.getGuestPin());

		// When - update pin codes part
		request = new PatchMeeting();
		request.setHostPin(4321);
		request.setGuestPin(1234);
		response = videoMeetings.meetingsUuidPatch(UUID.fromString(createResponse.getUuid()), request);

		// Then
		assertNotNull(response);
		assertEquals(2, response.getAdditionalInformation().size());

		getResponse = videoMeetings.meetingsUuidGet(UUID.fromString(createResponse.getUuid()));
		updatedSchedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(UUID.fromString(createResponse.getUuid()));

		assertNotNull(getResponse);
		assertEquals(request.getHostPin(), updatedSchedulingInfo.getHostPin());
		assertEquals(request.getGuestPin(), updatedSchedulingInfo.getGuestPin());
		assertEquals(2, getResponse.getAdditionalInformation().size());
	}

	@Test
	public void testNotAcceptableException() {

		try {
			videoMeetings.meetingsUuidDelete(UUID.fromString("7cc82183-0d47-439a-a00c-38f7a5a01fc5"));
			fail();
		}
		catch(ApiException e) {
			assertEquals(406, e.getCode());
			assertTrue(e.getResponseBody().contains("\"errorCode\":11"));
			assertTrue(e.getResponseBody().contains("\"errorText\":\"Meeting must have status AWAITS_PROVISION (0) in order to be deleted\""));
		}
	}

	@Test
	public void testCorsAllowed() throws IOException, InterruptedException {
		var request = HttpRequest.newBuilder(URI.create(String.format("http://%s:%s/api/meetings", videoApi.getHost(), videoApiPort)))
				.method("OPTIONS", HttpRequest.BodyPublishers.noBody())
				.header("Origin", "http://allowed:4100")
				.header("Access-Control-Request-Method", "POST")
				.build();

		var client = HttpClient.newBuilder().build();
		var response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
	}

	@Test
	public void testCorsDenied() throws IOException, InterruptedException {
		var request = HttpRequest.newBuilder(URI.create(String.format("http://%s:%s/api/meetings", videoApi.getHost(), videoApiPort)))
				.method("OPTIONS", HttpRequest.BodyPublishers.noBody())
				.header("Origin", "http://denied:4200")
				.header("Access-Control-Request-Method", "POST")
				.build();

		var client = HttpClient.newBuilder().build();
		var response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(403, response.statusCode());
	}

	@Test
	public void testCanReadInfo() {
		try {
			var result = infoApi.infoGet();

			assertNotNull(result);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testViewIsHealthy() throws SQLException {
		// Only test that we can select from the view and it contains data.
		try(var connection = DriverManager.getConnection(getJdbcUrl(), "videouser", "secret1234");
			var result = connection.createStatement().executeQuery("select * from view_pool_history")) {
			assertTrue(result.next());
		}
	}

    @Test
    void testTimestampFormat() throws JSONException {
        // POST
        var inputPost = """
                {
                  "subject": "Test timestamp format.",
                  "startTime": "2225-11-02T15:00:00+02:00",
                  "endTime": "2225-11-02T15:00:00+02:00"
                }""";

        String postResult;
        try(var client = ClientBuilder.newClient()) {
            postResult = client.target(UriBuilder.fromPath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort)))
                    .path("meetings")
                    .request()
                    .post(Entity.json(inputPost), String.class);
        }
        var postResultJson = new JSONObject(postResult);

        assertTrue(postResult.contains("\"startTime\":\"2225-11-02T13:00:00 +0000\""));
        assertTrue(postResult.contains("\"endTime\":\"2225-11-02T13:00:00 +0000\""));
        assertThat(postResultJson.getString("createdTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2} \\+0000$");

        // PUT
        var inputPut = """
                {
                  "subject": "Test timestamp format.",
                  "startTime": "2025-10-02T15:00:00 +02:00",
                  "endTime": "2025-10-02T17:00:00 +02:00"
                }""";

        String putResult;
        try(var client = ClientBuilder.newClient()) {
            putResult = client.target(UriBuilder.fromPath(String.format("http://%s:%s/api", videoApi.getHost(), videoApiPort)))
                    .path("meetings")
                    .path(postResultJson.getString("uuid"))
                    .request()
                    .put(Entity.json(inputPut), String.class);
        }
        var putResultJson = new JSONObject(putResult);

        assertTrue(putResult.contains("\"startTime\":\"2025-10-02T13:00:00 +0000\""));
        assertTrue(putResult.contains("\"endTime\":\"2025-10-02T15:00:00 +0000\""));
        assertThat(putResultJson.getString("createdTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2} \\+0000$");
        assertThat(putResultJson.getString("updatedTime")).matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2} \\+0000$");
    }

	private Date createDate(Calendar calendar, int hoursToAdd) {
		Calendar cal = (Calendar) calendar.clone();
		cal.add(Calendar.HOUR, hoursToAdd);

		return cal.getTime();
	}
}
