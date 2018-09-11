package dk.medcom.video.api.repository;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

//import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.dao.MeetingUser;

public class MeetingUserRepositoryTest extends RepositoryTest{
	
	@Resource
    private MeetingUserRepository subject;
	
	@Test
	public void testCreateMeetingUser() {
		
		// Given
	    String organisationId = "you firma 1";
	    String email = "you@you.dk";
		MeetingUser meetingUser = new MeetingUser();
		meetingUser.setOrganisationId(organisationId);
		meetingUser.setEmail(email);
		
		// When
		meetingUser = subject.save(meetingUser);
		
		// Then
		Assert.assertNotNull(meetingUser);
		Assert.assertNotNull(meetingUser.getId());
		Assert.assertEquals(email,  meetingUser.getEmail());
		Assert.assertEquals(organisationId,  meetingUser.getOrganisationId());
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
		Assert.assertEquals("test-org", meetingUser.getOrganisationId());
		Assert.assertEquals("me@me101.dk", meetingUser.getEmail());

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
		String existingEmail = "me@me102.dk";
		
		// When
		MeetingUser meetingUser = subject.findOneByOrganisationIdAndEmail(existingOrganisation, existingEmail);
		
		// Then
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(new Long(102), meetingUser.getId());
		Assert.assertEquals(existingOrganisation, meetingUser.getOrganisationId());
		Assert.assertEquals(existingEmail, meetingUser.getEmail());
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


}