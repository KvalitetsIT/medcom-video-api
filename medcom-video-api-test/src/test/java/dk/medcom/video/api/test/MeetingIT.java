package dk.medcom.video.api.test;

import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.GuestMicrophone;
import dk.medcom.video.api.dto.MeetingDto;
import dk.medcom.video.api.dto.OrganisationDto;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.VideoMeetingsApi;
import io.swagger.client.model.CreateMeeting;
import io.swagger.client.model.Meeting;
import io.swagger.client.model.UpdateMeeting;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class MeetingIT extends IntegrationWithOrganisationServiceTest {
	private VideoMeetingsApi videoMeetings;

	@Before
	public void setupApiClient() {
		var apiClient = new ApiClient()
				.setBasePath(String.format("http://%s:%s/api/", videoApi.getContainerIpAddress(), videoApiPort))
				.setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));

		videoMeetings = new VideoMeetingsApi(apiClient);
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
		String result = getClient()
				.path("meetings")
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
	public void testCanCreateUpdateAndReadMeeting() throws ApiException {
		var createMeeting = createMeeting(UUID.randomUUID().toString());
		createMeeting.setLabels(new ArrayList<String>());
		createMeeting.getLabels().add("Label One");
		createMeeting.getLabels().add("Label Two");

		var createdMeeting = videoMeetings.meetingsPost(createMeeting);
		assertNotNull(createdMeeting);
		assertEquals(createMeeting.getExternalId(), createdMeeting.getExternalId());
		assertEquals(2, createdMeeting.getLabels().size());
		assertTrue(createMeeting.getLabels().containsAll(createdMeeting.getLabels()));

		var updateMeeting = new UpdateMeeting();
		updateMeeting.setSubject("SUBJECT");
		updateMeeting.setStartTime(OffsetDateTime.now());
		updateMeeting.setEndTime(OffsetDateTime.now().plusHours(1));
		updateMeeting.setLabels(new ArrayList());
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
	public void testUniqueOrganisationExternalId() throws ApiException {
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
	public void testReadOrganisation() {
		var response = getClient()
				.path("services").path("organisation").path("test-org")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get(OrganisationDto.class);

		assertNotNull(response);
		assertEquals("test-org", response.getCode());
		assertEquals("company name test-org", response.getName());
	}

	private Date createDate(Calendar calendar, int hoursToAdd) {
		Calendar cal = (Calendar) calendar.clone();
		cal.add(Calendar.HOUR, hoursToAdd);

		return cal.getTime();
	}
}
