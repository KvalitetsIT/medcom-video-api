package dk.medcom.video.api.repository;

import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.entity.MeetingUser;
import org.junit.jupiter.api.Test;

import jakarta.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

public class MeetingUserRepositoryTest extends RepositoryTest{

	@Resource
    private MeetingUserRepository subject;

	@Test
	public void testCreateMeetingUser() {

		// Given
	    String organisationCode = "company 1";
	    String email = "you@you.dk";

		MeetingUser meetingUser = new MeetingUser();
		meetingUser.setOrganisationCode(organisationCode);
		meetingUser.setEmail(email);

		// When
		meetingUser = subject.save(meetingUser);

		// Then
		assertNotNull(meetingUser);
		assertNotNull(meetingUser.getId());
		assertEquals(email,  meetingUser.getEmail());
		assertEquals(organisationCode,  meetingUser.getOrganisationCode());
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
		assertEquals("test-org", meetingUser.getOrganisationCode());
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
		String organisationCode = "another-test-org";

		String existingEmail = "me@me102.dk";

		// When
		MeetingUser meetingUser = subject.findOneByOrganisationCodeAndEmail(organisationCode, existingEmail);

		// Then
		assertNotNull(meetingUser);
		assertEquals(102L, meetingUser.getId(), 0);
        assertEquals(organisationCode, meetingUser.getOrganisationCode());
		assertEquals(existingEmail, meetingUser.getEmail());
	}

	@Test
	public void testFindMeetingUserWithNonExistingOrganisationAndEmail() {
		// Given
		String organisationCode = "company 3";
		String nonExistingEmail = "xxxxx";

		// When
		MeetingUser meetingUser = subject.findOneByOrganisationCodeAndEmail(organisationCode, nonExistingEmail);

		// Then
		assertNull(meetingUser);
	}


}
