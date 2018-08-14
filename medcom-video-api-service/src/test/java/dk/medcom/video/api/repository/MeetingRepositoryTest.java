package dk.medcom.video.api.repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testcontainers.containers.MySQLContainer;

import dk.medcom.video.api.configuration.DatabaseConfiguration;
import dk.medcom.video.api.configuration.TestConfiguration;
import dk.medcom.video.api.dao.Meeting;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("test.properties")
@ContextConfiguration(
  classes = { TestConfiguration.class, DatabaseConfiguration.class }, 
  loader = AnnotationConfigContextLoader.class)
@Transactional
public class MeetingRepositoryTest {

	@ClassRule
	public static MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234");

	@Resource
    private MeetingRepository subject;

	@Autowired
	private DataSource dataSource;

	private static boolean testDataInitialised = false;
	
	@BeforeClass
	public static void setupMySqlJdbcUrl() {
		String jdbcUrl = mysql.getJdbcUrl();
		System.setProperty("jdbc.url", jdbcUrl);
	}
	
	@Before
	public void setupTestData() throws SQLException {
		
		if (!testDataInitialised) {
			Statement statement = dataSource.getConnection().createStatement();
			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id) VALUES (1, uuid(), 'TestMeeting-xyz', 'test-org')");
			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id) VALUES (2, uuid(), 'MyMeeting', 'another-test-org')");
			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id) VALUES (3, '7cc82183-0d47-439a-a00c-38f7a5a01fce', 'TestMeeting-123', 'test-org')");
			testDataInitialised = true;
		}
	}
	
	@Test
	public void testCreateMeeting() {
		
		// Given
		String uuid = UUID.randomUUID().toString();
		Meeting meeting = new Meeting();
		meeting.setSubject("Test meeting");
		meeting.setUuid(uuid);
		meeting.setOrganisationId("Den Sjove Afdeling A/S");
		
		// When
		meeting = subject.save(meeting);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertNotNull(meeting.getId());
		Assert.assertEquals(uuid,  meeting.getUuid());
	}
	
	@Test
	public void testFindAllMeetings() {
		// Given
		
		// When
		Iterable<Meeting> meetings = subject.findAll();
		
		// Then
		Assert.assertNotNull(meetings);
		int numberOfMeetings = 0;
		for (Meeting meeting : meetings) {
			Assert.assertNotNull(meeting);
			numberOfMeetings++;
		}
		Assert.assertEquals(3, numberOfMeetings);
	}
	
	@Test
	public void testFindMeetingWithExistingId() {
		// Given
		Long id = new Long(1);
		
		// When
		Meeting meeting = subject.findOne(id);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(id, meeting.getId());
		Assert.assertEquals("TestMeeting-xyz", meeting.getSubject());
		Assert.assertEquals("test-org", meeting.getOrganisationId());
	}

	@Test
	public void testFindMeetingWithNonExistingId() {
		// Given
		Long id = new Long(1999);
		
		// When
		Meeting meeting = subject.findOne(id);
		
		// Then
		Assert.assertNull(meeting);
	}

	@Test
	public void testFindMeetingWithExistingUuid() {
		// Given
		String exitstingUUid = "7cc82183-0d47-439a-a00c-38f7a5a01fce";
		
		// When
		Meeting meeting = subject.findOneByUuid(exitstingUUid);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(new Long(3), meeting.getId());
		Assert.assertEquals(exitstingUUid, meeting.getUuid());
		Assert.assertEquals("TestMeeting-123", meeting.getSubject());
		Assert.assertEquals("test-org", meeting.getOrganisationId());
	}

	@Test
	public void testFindMeetingWithNonExistingUuid() {
		// Given
		String nonExitstingUUid = "xxxxx";
		
		// When
		Meeting meeting = subject.findOneByUuid(nonExitstingUUid);
		
		// Then
		Assert.assertNull(meeting);
	}

	@Test
	public void testFindMeetingByExistingOrganisationId() {
		// Given
		String existingOrg = "test-org";
		
		// When
		List<Meeting> meetings = subject.findByOrganisationId(existingOrg);
		
		// Then
		Assert.assertNotNull(meetings);
		Assert.assertEquals(2, meetings.size());
	}
	
	@Test
	public void testFindMeetingByNonExistingOrganisationId() {
		// Given
		String existingOrg = "nonexisting-org";
		
		// When
		List<Meeting> meetings = subject.findByOrganisationId(existingOrg);
		
		// Then
		Assert.assertNotNull(meetings);
		Assert.assertEquals(0, meetings.size());
	}

}
