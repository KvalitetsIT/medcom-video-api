package dk.medcom.video.api.repository;

import java.sql.SQLException;
import java.sql.Statement;

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
			statement.execute("INSERT INTO meetings (id, subject) VALUES (1, 'TestMeeting-xyz')");
			testDataInitialised = true;
		}
	}
	
	@Test
	public void testCreateMeeting() {
		
		// Given
		Meeting meeting = new Meeting();
		meeting.setSubject("Test meeting");
		
		// When
		meeting = subject.save(meeting);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertNotNull(meeting.getId());
	}
	
	@Test
	public void testFindAllMeetings() {
		// Given
		
		// When
		Iterable<Meeting> meetings = subject.findAll();
		
		// Then
		Assert.assertNotNull(meetings);
		int numberOfMeetings = 0;
		Meeting lastMeeting = null;
		for (Meeting meeting : meetings) {
			Assert.assertNotNull(meeting);
			lastMeeting = meeting;
			numberOfMeetings++;
		}
		Assert.assertEquals(1, numberOfMeetings);
		Assert.assertEquals("TestMeeting-xyz", lastMeeting.getSubject());
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
	}
}
