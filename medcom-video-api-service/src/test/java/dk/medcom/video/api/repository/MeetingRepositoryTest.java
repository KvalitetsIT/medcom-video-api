package dk.medcom.video.api.repository;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingUser;

public class MeetingRepositoryTest extends RepositoryTest{

	@Resource
    private MeetingRepository subject;
	
	@Resource
    private MeetingUserRepository subjectMU;
	
	@Resource
    private SchedulingInfoRepository subjectSI;
	
	@Test
	public void testCreateMeeting() {
		
		// Given
		String uuid = UUID.randomUUID().toString();
		Long meetingUserId = new Long(101);
		
		Meeting meeting = new Meeting();
		meeting.setSubject("Test meeting");
		meeting.setUuid(uuid);
		meeting.setOrganisationId("Den Sjove Afdeling A/S");
		
		MeetingUser meetingUser = subjectMU.findOne(meetingUserId);
	    meeting.setMeetingUser(meetingUser);
	    
	    Calendar calendarStart = new GregorianCalendar(2018,10,01,13,15,00);
	    meeting.setStartTime(calendarStart.getTime());
	    
	    Calendar calendarEnd = new GregorianCalendar(2018,10,01,13,30,00);
	    meeting.setEndTime(calendarEnd.getTime());
	    meeting.setDescription("Lang beskrivelse af, hvad der foregår");
	    		
		// When
		meeting = subject.save(meeting);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertNotNull(meeting.getId());
		Assert.assertEquals(uuid,  meeting.getUuid());
		Assert.assertEquals(meetingUserId, meeting.getMeetingUser().getId());
		Assert.assertEquals(calendarStart.getTime(), meeting.getStartTime());
		Assert.assertEquals(calendarEnd.getTime(), meeting.getEndTime());
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
		Assert.assertEquals(4, numberOfMeetings);
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
		//TODO: Check dates are as expected: '2018-10-02 15:00:00', '2018-10-02 16:00:00' 
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
		Calendar calendarFrom = new GregorianCalendar(2018,01,01,01,01,01);
		Calendar calendarTo = new GregorianCalendar(2018,31,12,23,59,00);
	    
		
		// When
		List<Meeting> meetings = subject.findByOrganisationIdAndStartTimeBetween(existingOrg, calendarFrom.getTime(), calendarTo.getTime());
		
		// Then
		Assert.assertNotNull(meetings);
		Assert.assertEquals(2, meetings.size());
	}
	
	@Test
	public void testFindMeetingByNonExistingOrganisationId() {
		// Given
		String existingOrg = "nonexisting-org";
		Calendar calendarFrom = new GregorianCalendar(2018,01,01,01,01,01);
		Calendar calendarTo = new GregorianCalendar(2018,31,12,23,59,00);
		
		// When
		List<Meeting> meetings = subject.findByOrganisationIdAndStartTimeBetween(existingOrg, calendarFrom.getTime(), calendarTo.getTime());
		
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
	    MeetingUser meetingUser = subjectMU.findOne(meetingUserId);
	    meeting.setMeetingUser(meetingUser);	    
			
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(meetingUserId, meeting.getMeetingUser().getId());
	
	}


}