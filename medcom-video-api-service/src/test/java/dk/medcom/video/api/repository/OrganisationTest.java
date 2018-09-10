package dk.medcom.video.api.repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dk.medcom.video.api.dao.Organisation;;

public class OrganisationTest extends RepositoryTest {

	@Resource
    private OrganisationRepository subject;
	
	@Autowired
	private DataSource dataSource;

	private static boolean testDataInitialised = false;
	
	
	@Before
	public void setupTestData() throws SQLException {

		if (!testDataInitialised) {
			Statement statement = dataSource.getConnection().createStatement();
			statement.execute("INSERT INTO organisation (id, organisation_id, name) VALUES (1, 'company 1', 'company name 1')");
			statement.execute("INSERT INTO organisation (id, organisation_id, name) VALUES (2, 'company 2', 'company name 2')");
			statement.execute("INSERT INTO organisation (id, organisation_id, name) VALUES (3, 'company 3', 'company name 3')");
			testDataInitialised = true;
		}
	}
	
	@Test
	public void testCreateOrganisation() {
		
		// Given
		String organisationId = "Company 77";
		String name = "Company name 77";
		
		Organisation organisation = new Organisation();
		organisation.setOrganisationId(organisationId);
		organisation.setName(name);

		// When
		organisation = subject.save(organisation);
		
		// Then
		Assert.assertNotNull(organisation);
		Assert.assertNotNull(organisation.getId());
		Assert.assertEquals(organisationId,  organisation.getOrganisationId());
		Assert.assertEquals(name,  organisation.getName());
	}
	
	@Test
	public void testFindAllOrganisations() {
		// Given
		
		// When
		Iterable<Organisation> organisations = subject.findAll();
		
		// Then
		Assert.assertNotNull(organisations);
		int numberOfOrganisations = 0;
		for (Organisation organisation : organisations) {
			Assert.assertNotNull(organisation);
			numberOfOrganisations++;
		}
		Assert.assertEquals(3, numberOfOrganisations);
	}
	
	@Test
	public void testFindOrganisationWithExistingId() {
		// Given
		Long id = new Long(1);
		
		// When
		Organisation organisation = subject.findOne(id);
		
		// Then
		Assert.assertNotNull(organisation);
		Assert.assertEquals(id, organisation.getId());
		Assert.assertEquals("company 1", organisation.getOrganisationId());
		Assert.assertEquals("company name 1", organisation.getName());
	}

	@Test
	public void testFindOrganisationWithNonExistingId() {
		// Given
		Long id = new Long(1999);
		
		// When
		Organisation organisation = subject.findOne(id);
		
		// Then
		Assert.assertNull(organisation);
	}

	@Test
	public void testFindOrganisationByExistingOrganisationId() {
		// Given
		String existingOrg = "Company 1";
		
		// When
		List<Organisation> organisations = subject.findByOrganisationId(existingOrg);
		
		// Then
		Assert.assertNotNull(organisations);
		Assert.assertEquals(1, organisations.size());
	}
	
	@Test
	public void testFindOrganisationByNonExistingOrganisationId() {
		// Given
		String existingOrg = "nonexisting-org";
		
		// When
		List<Organisation> organisations = subject.findByOrganisationId(existingOrg);
		
		// Then
		Assert.assertNotNull(organisations);
		Assert.assertEquals(0, organisations.size());
	}

}