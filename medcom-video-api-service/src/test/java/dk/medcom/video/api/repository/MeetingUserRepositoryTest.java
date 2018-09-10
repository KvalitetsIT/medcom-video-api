package dk.medcom.video.api.repository;

import java.sql.SQLException;
import java.sql.Statement;
//import java.util.List;
//import java.util.UUID;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

//import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.dao.MeetingUser;

public class MeetingUserRepositoryTest extends RepositoryTest{
	
	
	@Resource
    private MeetingUserRepository subject;

	@Autowired
	private DataSource dataSource;

	private static boolean testDataInitialised = false;
	
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
		//Assert.assertEquals(uuid,  meeting.getUuid());
	}
	
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