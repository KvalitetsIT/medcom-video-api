package dk.medcom.video.api.test;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.api.GuestMicrophone;
import dk.medcom.video.api.api.MeetingDto;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.InfoApi;
import io.swagger.client.api.OrganisationApi;
import io.swagger.client.api.VideoMeetingsApi;
import io.swagger.client.api.VideoSchedulingInformationApi;
import io.swagger.client.model.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.*;

public class MeetingIT extends IntegrationWithOrganisationServiceTest {
	private VideoMeetingsApi videoMeetings;
	private OrganisationApi organisationApi;
	private VideoSchedulingInformationApi schedulingInfoApi;
	private InfoApi infoApi;

	@Before
	public void setupApiClient() {
		var apiClient = new ApiClient()
				.setBasePath(String.format("http://%s:%s/api", videoApi.getContainerIpAddress(), videoApiPort))
				.setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

		videoMeetings = new VideoMeetingsApi(apiClient);

		infoApi = new InfoApi(apiClient);

		schedulingInfoApi = new VideoSchedulingInformationApi(apiClient);

		var organisationApiClient = new ApiClient()
				.setBasePath(String.format("http://%s:%s/api/", videoApi.getContainerIpAddress(), videoApiPort))
				.setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));
		organisationApi = new OrganisationApi(apiClient);
	}

	@Test
	@Ignore //Can't GET /manage/info
	public void verifyTestResults() throws InterruptedException, IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		Thread.sleep(5000);
		TemporaryFolder folder = new TemporaryFolder();
		folder.create();

		GenericContainer newman = new GenericContainer<>("postman/newman_ubuntu1404:4.1.0")
					.withNetwork(dockerNetwork)
					.withVolumesFrom(resourceContainer, BindMode.READ_WRITE)
					.withCommand("run /collections/medcom-video-api.postman_collection.json -r cli,junit --reporter-junit-export /testresult/TEST-dk.medcom.video.api.test.IntegrationTest.xml --global-var host=videoapi --global-var port=8081");

		newman.start();
		attachLogger(newman, newmanLogger);

		long waitTime = 500;
		long loopLimit = 60;

		for(int i = 0; newman.isRunning() && i < loopLimit; i++) {
			System.out.println(i);
			System.out.println("Waiting....");
			Thread.sleep(waitTime);
		}

		resourceContainer.copyFileFromContainer("/testresult/TEST-dk.medcom.video.api.test.IntegrationTest.xml", folder.getRoot().getCanonicalPath() + "/TEST-dk.medcom.video.api.test.IntegrationTest.xml");

		FileInputStream input = new FileInputStream(folder.getRoot().getCanonicalPath() + "/TEST-dk.medcom.video.api.test.IntegrationTest.xml");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(input);
		XPath xPath = XPathFactory.newInstance().newXPath();
		String failureExpression = "/testsuites/testsuite/@failures";
		String errorExpression = "/testsuites/testsuite/@errrors";

		int failures = ((Double) xPath.compile(failureExpression).evaluate(xmlDocument, XPathConstants.NUMBER)).intValue();
		int errors = ((Double) xPath.compile(errorExpression).evaluate(xmlDocument, XPathConstants.NUMBER)).intValue();

		if(errors != 0 || failures != 0) {
			StringBuilder stringBuilder = new StringBuilder();
			Files.readAllLines(Paths.get(folder.getRoot().getCanonicalPath() + "/TEST-dk.medcom.video.api.test.IntegrationTest.xml")).forEach(x -> stringBuilder.append(x).append(System.lineSeparator()));
			System.out.println(stringBuilder);
		}

		assertEquals(0, failures);
		assertEquals(0, errors);
	}

	@Test(expected = ForbiddenException.class)
	public void testCanNotReadOtherOrganisation()  {
		getClient().path("meetings")
				.path("7cc82183-0d47-439a-a00c-38f7a5a01fc1")
				.request()
				.get(String.class);
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

		var createdMeeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(createdMeeting);
		assertEquals(createMeeting.getExternalId(), createdMeeting.getExternalId());
		assertEquals(2, createdMeeting.getLabels().size());
		assertTrue(createMeeting.getLabels().containsAll(createdMeeting.getLabels()));

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

		var updatedMeeting = videoMeetings.meetingsUuidPut(updateMeeting, createdMeeting.getUuid());
		assertNotNull(updatedMeeting);
		assertEquals(createMeeting.getExternalId(), updatedMeeting.getExternalId());
		assertEquals(1, updatedMeeting.getLabels().size());
		assertTrue(updateMeeting.getLabels().containsAll(updatedMeeting.getLabels()));

		var readMeeting = videoMeetings.meetingsUuidGet(createdMeeting.getUuid());
		assertNotNull(readMeeting);
		assertEquals(createMeeting.getExternalId(), readMeeting.getExternalId());
		assertEquals(1, readMeeting.getLabels().size());
		assertTrue(updateMeeting.getLabels().containsAll(readMeeting.getLabels()));
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
		createMeeting.setGuestMicrophone(CreateMeeting.GuestMicrophoneEnum.MUTED);

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertEquals(Meeting.GuestMicrophoneEnum.MUTED, meeting.getGuestMicrophone());
	}

	@Test
	public void testCanCreateWithMicNotSet() throws ApiException {
		var createMeeting = createMeeting("another_external_id2");

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertNull(meeting.getGuestMicrophone());
	}

	@Test
	public void testCanCreateWithGuestPinRequiredNotSet() throws ApiException {
		var createMeeting = createMeeting("another_external_id4");

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertFalse(meeting.isGuestPinRequired());
	}

	@Test
	public void testCanCreateWithGuestPinRequiredSetFalse() throws ApiException {
		var createMeeting = createMeeting("another_external_id5");
		createMeeting.setGuestPinRequired(false);

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertFalse(meeting.isGuestPinRequired());
	}

	@Test
	public void testCanCreateWithGuestPinRequiredSetTrue() throws ApiException {
		var createMeeting = createMeeting("another_external_id6");
		createMeeting.setGuestPinRequired(true);

		var meeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(meeting);
		assertEquals(createMeeting.getExternalId(), meeting.getExternalId());
		assertTrue(meeting.isGuestPinRequired());
	}

	@Test
	public void testUniqueOrganisationExternalId() {
		var createMeeting = createMeeting("external_id");

		try {
			videoMeetings.meetingsPostWithHttpInfo(createMeeting);
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
		var response = videoMeetings.meetingsUuidPatch(request, UUID.fromString(createResponse.getUuid()));
		var updatedSchedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(UUID.fromString(createResponse.getUuid()));

		// Then
		assertNotNull(response);

		var getResponse = videoMeetings.meetingsUuidGet(UUID.fromString(createResponse.getUuid()));

		assertNotNull(getResponse);
		assertNotEquals(createMeeting.getDescription(), getResponse.getDescription());
		assertNotEquals(request.getEndTime(), getResponse.getEndTime());
		assertEquals(Meeting.GuestMicrophoneEnum.MUTED, getResponse.getGuestMicrophone());
		assertEquals(true, getResponse.isGuestPinRequired());
		assertEquals(originalSchedulingInfo.getHostPin(), updatedSchedulingInfo.getHostPin());
		assertEquals(originalSchedulingInfo.getGuestPin(), updatedSchedulingInfo.getGuestPin());

		// When - update pin codes part
		request = new PatchMeeting();
		request.setHostPin(4321);
		request.setGuestPin(1234);
		response = videoMeetings.meetingsUuidPatch(request, UUID.fromString(createResponse.getUuid()));

		// Then
		assertNotNull(response);

		getResponse = videoMeetings.meetingsUuidGet(UUID.fromString(createResponse.getUuid()));
		updatedSchedulingInfo = schedulingInfoApi.schedulingInfoUuidGet(UUID.fromString(createResponse.getUuid()));

		assertNotNull(getResponse);
		assertEquals(request.getHostPin(), updatedSchedulingInfo.getHostPin());
		assertEquals(request.getGuestPin(), updatedSchedulingInfo.getGuestPin());
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
		var request = HttpRequest.newBuilder(URI.create(String.format("http://%s:%s/api/meetings", videoApi.getContainerIpAddress(), videoApiPort)))
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
		var request = HttpRequest.newBuilder(URI.create(String.format("http://%s:%s/api/meetings", videoApi.getContainerIpAddress(), videoApiPort)))
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

	private Date createDate(Calendar calendar, int hoursToAdd) {
		Calendar cal = (Calendar) calendar.clone();
		cal.add(Calendar.HOUR, hoursToAdd);

		return cal.getTime();
	}
}
