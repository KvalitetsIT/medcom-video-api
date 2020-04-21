package dk.medcom.video.api.repository;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingLabel;
import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.dao.Organisation;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.*;

import static org.junit.Assert.*;

public class MeetingRepositoryTest extends RepositoryTest {

	@Resource
    private MeetingRepository subject;
	
	@Resource
    private MeetingUserRepository subjectMU;
	
	@Resource
    private OrganisationRepository subjectO;
	
	@Test
	public void testCreateMeeting() {
		
		// Given
		String uuid = UUID.randomUUID().toString();
		Long meetingUserId = 101L;
		Long organisationId = 5L;
		String projectCode = "PROJECT1";
		
		Meeting meeting = new Meeting();
		meeting.setSubject("Test meeting");
		meeting.setUuid(uuid);
		
		Organisation organisation = subjectO.findById(organisationId).orElse(null);
		meeting.setOrganisation(organisation);
		
		MeetingUser meetingUser = subjectMU.findById(meetingUserId).orElse(null);
	    meeting.setMeetingUser(meetingUser);
	    
	    meeting.setOrganizedByUser(meetingUser);
	    meeting.setUpdatedByUser(meetingUser);
	    
	    Calendar calendarStart = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,13,15, 0);
	    meeting.setStartTime(calendarStart.getTime());
	    
	    Calendar calendarEnd = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,13,30, 0);
	    meeting.setEndTime(calendarEnd.getTime());
	    meeting.setDescription("Lang beskrivelse af, hvad der foregår");
	    meeting.setProjectCode(projectCode);
	    
	    Calendar calendarCreate = new GregorianCalendar(2018, Calendar.SEPTEMBER, 1,13,30, 0);
	    meeting.setCreatedTime(calendarCreate.getTime());
	    meeting.setUpdatedTime(calendarCreate.getTime());

	    HashSet<MeetingLabel> meetingLabels = new HashSet<>();
	    MeetingLabel label = new MeetingLabel();
	    label.setLabel("first label");
	    meetingLabels.add(label);

	    label = new MeetingLabel();
	    label.setLabel("second label");
	    meetingLabels.add(label);
	    meeting.setMeetingLabels(meetingLabels);

		// When
		meeting = subject.save(meeting);
		
		// Then
		assertNotNull(meeting);
		assertNotNull(meeting.getId());
		assertEquals(uuid,  meeting.getUuid());
		assertEquals(meetingUserId, meeting.getMeetingUser().getId());
		assertEquals(meeting.getMeetingUser(), meeting.getOrganizedByUser());
		assertEquals(meetingUserId, meeting.getUpdatedByUser().getId());
		assertEquals(organisationId, meeting.getOrganisation().getId());
		assertEquals(calendarStart.getTime(), meeting.getStartTime());
		assertEquals(calendarEnd.getTime(), meeting.getEndTime());
		assertEquals(projectCode, meeting.getProjectCode());
		assertEquals(calendarCreate.getTime(), meeting.getCreatedTime());
		assertEquals(calendarCreate.getTime(), meeting.getUpdatedTime());
	}
	
	@Test
	public void testFindAllMeetings() {
		// Given
		
		// When
		Iterable<Meeting> meetings = subject.findAll();
		
		// Then
		assertNotNull(meetings);
		int numberOfMeetings = 0;
		for (Meeting meeting : meetings) {
			assertNotNull(meeting);
			numberOfMeetings++;
		}
		assertEquals(7, numberOfMeetings);
	}
	
	@Test
	public void testFindMeetingWithExistingId() {
		// Given
		Long id = 1L;
		Long organisationId = 5L;
		Organisation organisation = subjectO.findById(organisationId).orElse(null);
		
		// When
		Meeting meeting = subject.findById(id).orElse(null);
		
		// Then
		assertNotNull(meeting);
		assertEquals(id, meeting.getId());
		assertEquals("TestMeeting-xyz", meeting.getSubject());
		assertEquals(organisation.getOrganisationId(), meeting.getOrganisation().getOrganisationId());
		assertEquals("Mødebeskrivelse 1", meeting.getDescription());

	}

	@Test
	public void testFindMeetingWithNonExistingId() {
		// Given
		Long id = 1999L;
		
		// When
		Meeting meeting = subject.findById(id).orElse(null);
		
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
		assertNotNull(meeting);
		assertEquals(new Long(3), meeting.getId());
		assertEquals(exitstingUUid, meeting.getUuid());
		assertEquals("TestMeeting-123", meeting.getSubject());
		assertEquals("test-org", meeting.getOrganisation().getOrganisationId());
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
		Long organisationId = 5L;
		Calendar calendarFrom = new GregorianCalendar(2018, Calendar.FEBRUARY, 1, 1, 1, 1);
		Calendar calendarTo = new GregorianCalendar(2018, Calendar.DECEMBER,31,23,59, 0);
		
		Organisation organisation = subjectO.findById(organisationId).orElse(null);
	    
		
		// When
		List<Meeting> meetings = subject.findByOrganisationAndStartTimeBetween(organisation, calendarFrom.getTime(), calendarTo.getTime());
		
		// Then
		assertNotNull(meetings);
		assertEquals(4, meetings.size());
	}
	
	@Test
	public void testFindMeetingByNonExistingOrganisation() {
		// Given
		Long organisationId = 3L;
		Calendar calendarFrom = new GregorianCalendar(2018, Calendar.FEBRUARY, 1, 1, 1, 1);
		Calendar calendarTo = new GregorianCalendar(2018, Calendar.DECEMBER,31,23,59, 0);
		
		Organisation organisation = subjectO.findById(organisationId).orElse(null);
		
		// When
		List<Meeting> meetings = subject.findByOrganisationAndStartTimeBetween(organisation, calendarFrom.getTime(), calendarTo.getTime());
		
		// Then
		assertNotNull(meetings);
		assertEquals(0, meetings.size());
	}
	
	@Test
	public void testGetMeetingUserOnExistingMeeting() {
		
		// Given
		Long meetingId = 1L;
		Long meetingUserId = 101L;
			
		// When
		Meeting meeting = subject.findById(meetingId).orElse(null);
			
		// Then
		assertNotNull(meeting);
		assertEquals(meetingUserId, meeting.getMeetingUser().getId());

	}

	@Test
	public void testSetMeetingUserOnExistingMeeting() {
		
		// Given
		Long meetingId = 1L;
		Long meetingUserId = 103L;
			
		// When
		Meeting meeting = subject.findById(meetingId).orElse(null);
	    MeetingUser meetingUser = subjectMU.findById(meetingUserId).orElse(null);
	    meeting.setMeetingUser(meetingUser);	    
			
		// Then
		assertNotNull(meeting);
		assertNotNull(meetingUser);
		assertEquals(meetingUserId, meeting.getMeetingUser().getId());
	
	}
	@Test
	public void testGetOrganizerUserOnExistingMeeting() {
		
		// Given
		Long meetingId = 1L;
		Long organizedByUserId = 101L;
			
		// When
		Meeting meeting = subject.findById(meetingId).orElse(null);
			
		// Then
		assertNotNull(meeting);
		assertEquals(organizedByUserId, meeting.getOrganizedByUser().getId());

	}

	@Test
	public void testSetOrganinizerUserOnExistingMeeting() {
		
		// Given
		Long meetingId = 1L;
		Long organizedByUserId = 105L;
			
		// When
		Meeting meeting = subject.findById(meetingId).orElse(null);
	    MeetingUser meetingUser = subjectMU.findById(organizedByUserId).orElse(null);
	    meeting.setOrganizedByUser(meetingUser);	    
			
		// Then
		assertNotNull(meeting);
		assertNotNull(meetingUser);
		assertEquals(organizedByUserId, meeting.getOrganizedByUser().getId());
	
	}

	@Test
	public void testGetUpdatedUserOnExistingMeeting() {
		
		// Given
		Long meetingId = 1L;
		Long updatedByUserId = 101L;
			
		// When
		Meeting meeting = subject.findById(meetingId).orElse(null);
			
		// Then
		assertNotNull(meeting);
		assertEquals(updatedByUserId, meeting.getUpdatedByUser().getId());

	}

	@Test
	public void testSetUpdatedUserOnExistingMeeting() {
		
		// Given
		Long meetingId = 1L;
		Long updatedByUserId = 105L;
			
		// When
		Meeting meeting = subject.findById(meetingId).orElse(null);
	    MeetingUser meetingUser = subjectMU.findById(updatedByUserId).orElse(null);
	    meeting.setUpdatedByUser(meetingUser);	    
			
		// Then
		assertNotNull(meeting);
		assertNotNull(meetingUser);
		assertEquals(updatedByUserId, meeting.getUpdatedByUser().getId());
	
	}

	@Test
	public void testGetByOrganizedByAndSubject() {
		MeetingUser organizedBy = subjectMU.findById(102L).orElse(null);
		String meetingSubject = "MyMeeting4";

		List<Meeting> result = subject.findByOrganizedByAndSubject(organizedBy, meetingSubject);

		assertEquals(1, result.size());

		Meeting meeting = result.get(0);
		assertEquals(4, meeting.getId().longValue());
		assertEquals(meetingSubject, meeting.getSubject());
		assertEquals(organizedBy.getId(), meeting.getOrganizedByUser().getId());
	}

	@Test
	public void testGetByOrganisationByAndSubject() {
		Organisation organisation = subjectO.findById(6L).orElse(null);
		String meetingSubject = "MyMeeting4";

		List<Meeting> result = subject.findByOrganisationAndSubject(organisation, meetingSubject);

		assertEquals(1, result.size());

		Meeting meeting = result.get(0);
		assertEquals(4, meeting.getId().longValue());
		assertEquals(meetingSubject, meeting.getSubject());
		assertEquals(organisation.getId(), meeting.getOrganisation().getId());
	}

	@Test
	public void testGetByOrganisationAndOrganizedBy() {
		Organisation organisation = subjectO.findById(5L).orElse(null);
		MeetingUser organizedBy = subjectMU.findOneByOrganisationAndEmail(organisation, "me@me105organizer.dk");

		List<Meeting> result = subject.findByOrganisationAndOrganizedBy(organisation, organizedBy);

		assertEquals(1, result.size());

		Meeting meeting = result.get(0);
		assertEquals(5, meeting.getId().longValue());
		assertEquals("TestMeeting-xyz5", meeting.getSubject());
		assertEquals(organisation.getId(), meeting.getOrganisation().getId());
		assertEquals(organizedBy.getEmail(), meeting.getOrganizedByUser().getEmail());
	}

	@Test
	public void testGetByOrganizedBy() {
		Organisation organisation = subjectO.findById(5L).orElse(null);
		MeetingUser organizedBy = subjectMU.findOneByOrganisationAndEmail(organisation, "me@me105organizer.dk");

		List<Meeting> result = subject.findByOrganizedBy(organizedBy);

		assertEquals(1, result.size());

		Meeting meeting = result.get(0);
		assertEquals(5, meeting.getId().longValue());
		assertEquals("TestMeeting-xyz5", meeting.getSubject());
		assertEquals(organisation.getId(), meeting.getOrganisation().getId());
		assertEquals(organizedBy.getEmail(), meeting.getOrganizedByUser().getEmail());
	}

	@Test
	public void testGetByUriWithDomainAndOrganisation() {
		Organisation organisation = subjectO.findById(5L).orElse(null);
		String uriWithDomain  = "1236@test.dk";

		List<Meeting> result = subject.findByUriWithDomainAndOrganisation(organisation, uriWithDomain);

		assertEquals(1, result.size());

		Meeting meeting = result.get(0);
		assertEquals(6, meeting.getId().longValue());
		assertEquals("TestMeeting-xyz6", meeting.getSubject());
		assertEquals(organisation.getId(), meeting.getOrganisation().getId());
	}

	@Test
	public void testGetByUriWithDomainAndOrganizedBy() {
		Organisation organisation = subjectO.findById(5L).orElse(null);
		String uriWithDomain  = "1236@test.dk";

		List<Meeting> result = subject.findByUriWithDomainAndOrganizedBy(subjectMU.findById(101L).orElse(null), uriWithDomain);

		assertEquals(1, result.size());

		Meeting meeting = result.get(0);
		assertEquals(6, meeting.getId().longValue());
		assertEquals("TestMeeting-xyz6", meeting.getSubject());
		assertEquals(organisation.getId(), meeting.getOrganisation().getId());
	}

	@Test
	public void testGetByLabelAndOrganisation() {
		Organisation organisation = subjectO.findById(5L).orElse(null);
		String label  = "second label";

		List<Meeting> result = subject.findByLabelAndOrganisation(organisation, label);

		assertEquals(1, result.size());

		Meeting meeting = result.get(0);
		assertEquals(7, meeting.getId().longValue());
		assertEquals("TestMeeting-xyz7", meeting.getSubject());
		assertEquals(organisation.getId(), meeting.getOrganisation().getId());
	}

	@Test
	public void testGetByLabelAndOrganizedBy() {
		Organisation organisation = subjectO.findById(5L).orElse(null);
		String label  = "second label";

		List<Meeting> result = subject.findByLabelAndOrganizedBy(subjectMU.findById(101L).orElse(null), label);

		assertEquals(1, result.size());

		Meeting meeting = result.get(0);
		assertEquals(7, meeting.getId().longValue());
		assertEquals("TestMeeting-xyz7", meeting.getSubject());
		assertEquals(organisation.getId(), meeting.getOrganisation().getId());
	}

	@Test
	public void testFindByOrganisationAndSubjectLike() {
		Organisation organisation = subjectO.findById(5L).orElse(null);
		String label  = "%Meeting-xyz%";
		String description = "%beskrivelse%";

		List<Meeting> result = subject.findByOrganisationAndSubjectLikeOrDescriptionLike(organisation, label, description);

		assertEquals(5, result.size());
		result.forEach(x ->	assertTrue(x.getSubject().contains("Meeting-xyz") || x.getDescription().contains("beskrivelse")));
	}

	@Test
	public void testFindOneByOrganisationAndEmail() {
		Organisation organisation = subjectO.findById(5L).orElse(null);
		MeetingUser organizedBy = subjectMU.findOneByOrganisationAndEmail(organisation, "me@me101.dk");
		String label  = "%Meeting-xyz%";
		String description = "%beskrivelse%";

		List<Meeting> result = subject.findByOrganizedByAndSubjectLikeOrDescriptionLike(organizedBy, label, description);

		assertEquals(4, result.size());
		result.forEach(x ->	assertTrue(x.getSubject().contains("Meeting-xyz") || x.getDescription().contains("beskrivelse")));
	}
}