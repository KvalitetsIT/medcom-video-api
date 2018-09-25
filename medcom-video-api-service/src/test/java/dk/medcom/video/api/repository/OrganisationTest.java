package dk.medcom.video.api.repository;

import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.Organisation;;

public class OrganisationTest extends RepositoryTest {

	@Resource
    private OrganisationRepository subject;
	
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
		Assert.assertEquals(4, numberOfOrganisations);
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
		Organisation organisation = subject.findByOrganisationId(existingOrg);
		
		// Then
		Assert.assertNotNull(organisation);
	}
	
	@Test
	public void testFindOrganisationByNonExistingOrganisationId() {
		// Given
		String existingOrg = "nonexisting-org";
		
		// When
		Organisation organisation = subject.findByOrganisationId(existingOrg);
		
		// Then
		Assert.assertNull(organisation);
	}

}