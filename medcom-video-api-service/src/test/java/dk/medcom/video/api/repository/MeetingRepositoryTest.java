//TODO Lene: TASK: abstract repo test med mysql db oprettelse for sig
package dk.medcom.video.api.repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingUser;

public class MeetingRepositoryTest extends RepositoryTest{

	@Resource
    private MeetingRepository subject;
	
	@Resource
    private MeetingUserRepository subject2;

	@Autowired
	private DataSource dataSource;

	private static boolean testDataInitialised = false;
	
	@Before
	public void setupTestData() throws SQLException {

		if (!testDataInitialised) {
			Statement statement = dataSource.getConnection().createStatement();
			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (101,  'test-org', 'me@me101.dk')");
			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (102,  'another-test-org', 'me@me102.dk')");
			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (103,  'test-org', 'me@me103.dk')");
			
			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (1, uuid(), 'TestMeeting-xyz', 'test-org', 101, '2018-10-02 15:00:00', '2018-10-02 16:00:00', 'Mødebeskrivelse 1')");
			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (2, uuid(), 'MyMeeting', 'another-test-org', 102, '2018-11-02 15:00:00', '2018-11-02 16:00:00', 'Mødebeskrivelse 2')");
			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (3, '7cc82183-0d47-439a-a00c-38f7a5a01fce', 'TestMeeting-123', 'test-org', 101,  '2018-12-02 15:00:00', '2018-12-02 16:00:00', 'Mødebeskrivelse 3')");
			testDataInitialised = true;
		}
	}

	@Test
	public void testCreateMeeting() {
		
		// Given
		String uuid = UUID.randomUUID().toString();
		Long meetingUserId = new Long(101);
		
		Meeting meeting = new Meeting();
		meeting.setSubject("Test meeting");
		meeting.setUuid(uuid);
		meeting.setOrganisationId("Den Sjove Afdeling A/S");
		
		MeetingUser meetingUser = subject2.findOne(meetingUserId);
	    meeting.setMeetingUser(meetingUser);
	    
	    Calendar calendar = new GregorianCalendar(2018,10,01,13,15,00);
	    meeting.setStartTime(calendar.getTime());
	    
	    calendar.set(Calendar.HOUR_OF_DAY, 14);
	    meeting.setEndTime(calendar.getTime());
	    meeting.setDescription("Lang beskrivelse af, hvad der foregår");
	    		
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

		
		//TODO Lene: tjek datoer er som forventet.
		//'2018-10-02 15:00:00', '2018-10-02 16:00:00' 
		
		Assert.assertEquals("Mødebeskrivelse 1", meeting.getDescription());

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
	
	@Test
	public void testGetMeetingUserOnExistingMeeting() {
		
		// Given
		Long meetingId = new Long(1);
		Long meetingUserId = new Long(101);
			
		// When
		Meeting meeting = subject.findOne(meetingId);
			
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(meetingUserId, meeting.getMeetingUser().getId());

	}

	@Test
	public void testSetMeetingUserOnExistingMeeting() {
		
		// Given
		Long meetingId = new Long(1);
		Long meetingUserId = new Long(103);
			
		// When
		Meeting meeting = subject.findOne(meetingId);
	    MeetingUser meetingUser = subject2.findOne(meetingUserId);
	    meeting.setMeetingUser(meetingUser);	    
			
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(meetingUserId, meeting.getMeetingUser().getId());
	
	}

}