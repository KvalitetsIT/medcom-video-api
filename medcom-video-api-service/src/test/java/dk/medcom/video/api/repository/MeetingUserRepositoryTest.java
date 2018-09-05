package dk.medcom.video.api.repository;

import java.sql.SQLException;
import java.sql.Statement;
//import java.util.List;
//import java.util.UUID;

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
//import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.dao.MeetingUser;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("test.properties")
@ContextConfiguration(
  classes = { TestConfiguration.class, DatabaseConfiguration.class }, 
  loader = AnnotationConfigContextLoader.class)
@Transactional
public class MeetingUserRepositoryTest {
	
	
	@ClassRule
	public static MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234");

	@Resource
    private MeetingUserRepository subject;

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
			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (1,  'test-org', 'me@me.dk')");
			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (2,  'another-test-org', 'me@me2.dk')");
			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (3,  'test-org', 'me@me3.dk')");
			testDataInitialised = true;
		}
	}
	
	@Test
	public void testCreateMeetingUser() {
		
		// Given
		//    String uuid = UUID.randomUUID().toString();
	    String organisationId = "you firma 1";
	    String email = "you@you.dk";
		MeetingUser meetingUser = new MeetingUser();
		//   meeting.setUuid(uuid);
		meetingUser.setOrganisationId(organisationId);
		meetingUser.setEmail(email);
		
		// When
		meetingUser = subject.save(meetingUser);
		
		// Then
		Assert.assertNotNull(meetingUser);
		Assert.assertNotNull(meetingUser.getId());
		Assert.assertEquals(email,  meetingUser.getEmail());
		Assert.assertEquals(organisationId,  meetingUser.getOrganisationId());
		//Assert.assertEquals(uuid,  meeting.getUuid());
	}
	
//	@Test
//	public void testFindAllMeetings() {
//		// Given
//		
//		// When
//		Iterable<Meeting> meetings = subject.findAll();
//		
//		// Then
//		Assert.assertNotNull(meetings);
//		int numberOfMeetings = 0;
//		for (Meeting meeting : meetings) {
//			Assert.assertNotNull(meeting);
//			numberOfMeetings++;
//		}
//		Assert.assertEquals(3, numberOfMeetings);
//	}


	@Test
	public void testFindMeetingUserWithExistingId() {
		// Given
		Long id = new Long(1);
		
		// When
		MeetingUser meetingUser = subject.findOne(id);
		
		// Then
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(id, meetingUser.getId());
		Assert.assertEquals("test-org", meetingUser.getOrganisationId());
		Assert.assertEquals("me@me.dk", meetingUser.getEmail());

	}

	@Test
	public void testFindMeetingUserWithNonExistingId() {
		// Given
		Long id = new Long(1999);
		
		// When
		MeetingUser meetingUser = subject.findOne(id);
		
		// Then
		Assert.assertNull(meetingUser);
	}

	@Test
	public void testFindMeetingUserWithExistingOrganisationIdAndEmail() {
		// Given
		String existingOrganisation = "another-test-org";
		String existingEmail = "me@me2.dk";
		
		// When
		MeetingUser meetingUser = subject.findOneByOrganisationIdAndEmail(existingOrganisation, existingEmail);
		
		// Then
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(new Long(2), meetingUser.getId());
		Assert.assertEquals(existingOrganisation, meetingUser.getOrganisationId());
		Assert.assertEquals(existingEmail, meetingUser.getEmail());
		//Assert.assertEquals("TestMeeting-123", meeting.getSubject());
		//Assert.assertEquals("test-org", meeting.getOrganisationId());
	}

	@Test
	public void testFindMeetingUserWithNonExistingOrganisationIdAndEmail() {
		// Given
		String nonExistingOrganisationId = "xxxxx";
		String nonExistingEmail = "xxxxx";
		
		// When
		MeetingUser meetingUser = subject.findOneByOrganisationIdAndEmail(nonExistingOrganisationId, nonExistingEmail);
		
		// Then
		Assert.assertNull(meetingUser);
	}

//	@Test
//	public void testFindMeetingByExistingOrganisationId() {
//		// Given
//		String existingOrg = "test-org";
//		
//		// When
//		List<Meeting> meetings = subject.findByOrganisationId(existingOrg);
//		
//		// Then
//		Assert.assertNotNull(meetings);
//		Assert.assertEquals(2, meetings.size());
//	}
	
//	@Test
//	public void testFindMeetingByNonExistingOrganisationId() {
//		// Given
//		String existingOrg = "nonexisting-org";
//		
//		// When
//		List<Meeting> meetings = subject.findByOrganisationId(existingOrg);
//		
//		// Then
//		Assert.assertNotNull(meetings);
//		Assert.assertEquals(0, meetings.size());
//	}

}
