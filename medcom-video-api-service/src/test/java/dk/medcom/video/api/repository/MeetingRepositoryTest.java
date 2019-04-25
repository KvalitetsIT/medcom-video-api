package dk.medcom.video.api.repository;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.dao.Organisation;

public class MeetingRepositoryTest extends RepositoryTest{

	@Resource
    private MeetingRepository subject;
	
	@Resource
    private MeetingUserRepository subjectMU;
	
	@Resource
    private SchedulingInfoRepository subjectSI;
	
	@Resource
    private OrganisationRepository subjectO;
	
	@Test
	public void testCreateMeeting() {
		
		// Given
		String uuid = UUID.randomUUID().toString();
		Long meetingUserId = new Long(101);
		Long organisationId = new Long(5);
		String projectCode = "PROJECT1";
		
		Meeting meeting = new Meeting();
		meeting.setSubject("Test meeting");
		meeting.setUuid(uuid);
		
		Organisation organisation = subjectO.findOne(organisationId);
		meeting.setOrganisation(organisation);
		
		MeetingUser meetingUser = subjectMU.findOne(meetingUserId);
	    meeting.setMeetingUser(meetingUser);
	    
	    meeting.setOrganizedByUser(meetingUser);
	    meeting.setUpdatedByUser(meetingUser);
	    
	    Calendar calendarStart = new GregorianCalendar(2018,10,01,13,15,00);
	    meeting.setStartTime(calendarStart.getTime());
	    
	    Calendar calendarEnd = new GregorianCalendar(2018,10,01,13,30,00);
	    meeting.setEndTime(calendarEnd.getTime());
	    meeting.setDescription("Lang beskrivelse af, hvad der foregår");
	    meeting.setProjectCode(projectCode);
	    
	    Calendar calendarCreate = new GregorianCalendar(2018,8,01,13,30,00);
	    meeting.setCreatedTime(calendarCreate.getTime());
	    meeting.setUpdatedTime(calendarCreate.getTime());
	    
		// When
		meeting = subject.save(meeting);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertNotNull(meeting.getId());
		Assert.assertEquals(uuid,  meeting.getUuid());
		Assert.assertEquals(meetingUserId, meeting.getMeetingUser().getId());
		Assert.assertEquals(meeting.getMeetingUser(), meeting.getOrganizedByUser());
		Assert.assertEquals(meetingUserId, meeting.getUpdatedByUser().getId());
		Assert.assertEquals(organisationId, meeting.getOrganisation().getId());
		Assert.assertEquals(calendarStart.getTime(), meeting.getStartTime());
		Assert.assertEquals(calendarEnd.getTime(), meeting.getEndTime());
		Assert.assertEquals(projectCode, meeting.getProjectCode());
		Assert.assertEquals(calendarCreate.getTime(), meeting.getCreatedTime());
		Assert.assertEquals(calendarCreate.getTime(), meeting.getUpdatedTime());
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
		Assert.assertEquals(6, numberOfMeetings);
	}
	
	@Test
	public void testFindMeetingWithExistingId() {
		// Given
		Long id = new Long(1);
		Long organisationId = new Long(5);
		Organisation organisation = subjectO.findOne(organisationId);
		
		// When
		Meeting meeting = subject.findOne(id);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(id, meeting.getId());
		Assert.assertEquals("TestMeeting-xyz", meeting.getSubject());
		Assert.assertEquals(organisation.getOrganisationId(), meeting.getOrganisation().getOrganisationId());
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
		Assert.assertEquals("test-org", meeting.getOrganisation().getOrganisationId());
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
	public void testFindMeetingByExistingOrganisation() {
		// Given
		Long organisationId = new Long(5);
		Calendar calendarFrom = new GregorianCalendar(2018,01,01,01,01,01);
		Calendar calendarTo = new GregorianCalendar(2018,31,12,23,59,00);
		
		Organisation organisation = subjectO.findOne(organisationId);
	    
		
		// When
		List<Meeting> meetings = subject.findByOrganisationAndStartTimeBetween(organisation, calendarFrom.getTime(), calendarTo.getTime());
		
		// Then
		Assert.assertNotNull(meetings);
		Assert.assertEquals(4, meetings.size());
	}
	
	@Test
	public void testFindMeetingByNonExistingOrganisation() {
		// Given
		Long organisationId = new Long(3);
		Calendar calendarFrom = new GregorianCalendar(2018,01,01,01,01,01);
		Calendar calendarTo = new GregorianCalendar(2018,31,12,23,59,00);
		
		Organisation organisation = subjectO.findOne(organisationId);
		
		// When
		List<Meeting> meetings = subject.findByOrganisationAndStartTimeBetween(organisation, calendarFrom.getTime(), calendarTo.getTime());
		
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
	@Test
	public void testGetOrganizerUserOnExistingMeeting() {
		
		// Given
		Long meetingId = new Long(1);
		Long organizedByUserId = new Long(101);
			
		// When
		Meeting meeting = subject.findOne(meetingId);
			
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(organizedByUserId, meeting.getOrganizedByUser().getId());

	}

	@Test
	public void testSetOrganinizerUserOnExistingMeeting() {
		
		// Given
		Long meetingId = new Long(1);
		Long organizedByUserId = new Long(105);
			
		// When
		Meeting meeting = subject.findOne(meetingId);
	    MeetingUser meetingUser = subjectMU.findOne(organizedByUserId);
	    meeting.setOrganizedByUser(meetingUser);	    
			
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(organizedByUserId, meeting.getOrganizedByUser().getId());
	
	}

	@Test
	public void testGetUpdatedUserOnExistingMeeting() {
		
		// Given
		Long meetingId = new Long(1);
		Long updatedByUserId = new Long(101);
			
		// When
		Meeting meeting = subject.findOne(meetingId);
			
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(updatedByUserId, meeting.getUpdatedByUser().getId());

	}

	@Test
	public void testSetUpdatedUserOnExistingMeeting() {
		
		// Given
		Long meetingId = new Long(1);
		Long updatedByUserId = new Long(105);
			
		// When
		Meeting meeting = subject.findOne(meetingId);
	    MeetingUser meetingUser = subjectMU.findOne(updatedByUserId);
	    meeting.setUpdatedByUser(meetingUser);	    
			
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(updatedByUserId, meeting.getUpdatedByUser().getId());
	
	}
	
}