package dk.medcom.video.api.repository;

import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Organisation;
import org.junit.jupiter.api.Test;

import jakarta.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

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
	    
	    Organisation organisation = subjectO.findById(organisationId).orElse(null);
	    
		MeetingUser meetingUser = new MeetingUser();
		meetingUser.setOrganisation(organisation);
		meetingUser.setEmail(email);
		
		// When
		meetingUser = subject.save(meetingUser);
		
		// Then
		assertNotNull(meetingUser);
		assertNotNull(meetingUser.getId());
		assertEquals(email,  meetingUser.getEmail());
		assertEquals(organisation,  meetingUser.getOrganisation());
	}
	
	@Test
	public void testFindMeetingUserWithExistingId() {
		// Given
		Long id = 101L;
		
		// When
		MeetingUser meetingUser = subject.findById(id).orElse(null);
		
		// Then
		assertNotNull(meetingUser);
		assertEquals(id, meetingUser.getId());
		assertEquals("test-org", meetingUser.getOrganisation().getOrganisationId());
		assertEquals("me@me101.dk", meetingUser.getEmail());

	}

	@Test
	public void testFindMeetingUserWithNonExistingId() {
		// Given
		Long id = 3L;
		
		// When
		MeetingUser meetingUser = subject.findById(id).orElse(null);
		
		// Then
		assertNull(meetingUser);
	}

	@Test
	public void testFindMeetingUserWithExistingOrganisationAndEmail() {
		// Given
		Long organisationId = 6L;
		
		String existingEmail = "me@me102.dk";
		
		Organisation organisation = subjectO.findById(organisationId).orElse(null);
		
		// When
		MeetingUser meetingUser = subject.findOneByOrganisationAndEmail(organisation, existingEmail);
		
		// Then
		assertNotNull(meetingUser);
		assertEquals(102L, meetingUser.getId(), 0);
        assertNotNull(organisation);
        assertEquals(organisation.getName(), meetingUser.getOrganisation().getName());
		assertEquals(existingEmail, meetingUser.getEmail());
	}

	@Test
	public void testFindMeetingUserWithNonExistingOrganisationAndEmail() {
		// Given
		Long organisationId = 3L;
		String nonExistingEmail = "xxxxx";
		
		Organisation organisation = subjectO.findById(organisationId).orElse(null);
		
		// When
		MeetingUser meetingUser = subject.findOneByOrganisationAndEmail(organisation, nonExistingEmail);
		
		// Then
		assertNull(meetingUser);
	}


}