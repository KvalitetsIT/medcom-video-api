package dk.medcom.video.api.repository;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

//import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.dao.Organisation;

public class MeetingUserRepositoryTest extends RepositoryTest{
	
	@Resource
    private MeetingUserRepository subject;
	
	@Resource
    private OrganisationRepository subjectO;
	
	@Test
	public void testCreateMeetingUser() {
		
		// Given
	    Long organisationId = 1L;
	    String email = "you@you.dk";
	    
	    Organisation organisation = subjectO.findOne(organisationId);
	    
		MeetingUser meetingUser = new MeetingUser();
		meetingUser.setOrganisation(organisation);
		meetingUser.setEmail(email);
		
		// When
		meetingUser = subject.save(meetingUser);
		
		// Then
		Assert.assertNotNull(meetingUser);
		Assert.assertNotNull(meetingUser.getId());
		Assert.assertEquals(email,  meetingUser.getEmail());
		Assert.assertEquals(organisation,  meetingUser.getOrganisation());
	}
	
	@Test
	public void testFindMeetingUserWithExistingId() {
		// Given
		Long id = new Long(101);
		
		// When
		MeetingUser meetingUser = subject.findOne(id);
		
		// Then
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(id, meetingUser.getId());
		Assert.assertEquals("test-org", meetingUser.getOrganisation().getOrganisationId());
		Assert.assertEquals("me@me101.dk", meetingUser.getEmail());

	}

	@Test
	public void testFindMeetingUserWithNonExistingId() {
		// Given
		Long id = new Long(3);
		
		// When
		MeetingUser meetingUser = subject.findOne(id);
		
		// Then
		Assert.assertNull(meetingUser);
	}

	@Test
	public void testFindMeetingUserWithExistingOrganisationAndEmail() {
		// Given
		Long organisationId = new Long(6);
		
		String existingEmail = "me@me102.dk";
		
		Organisation organisation = subjectO.findOne(organisationId);
		
		// When
		MeetingUser meetingUser = subject.findOneByOrganisationAndEmail(organisation, existingEmail);
		
		// Then
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(new Long(102), meetingUser.getId());
		Assert.assertEquals(organisation.getName(), meetingUser.getOrganisation().getName());
		Assert.assertEquals(existingEmail, meetingUser.getEmail());
	}

	@Test
	public void testFindMeetingUserWithNonExistingOrganisationAndEmail() {
		// Given
		Long organisationId = new Long(3);
		String nonExistingEmail = "xxxxx";
		
		Organisation organisation = subjectO.findOne(organisationId);
		
		// When
		MeetingUser meetingUser = subject.findOneByOrganisationAndEmail(organisation, nonExistingEmail);
		
		// Then
		Assert.assertNull(meetingUser);
	}


}