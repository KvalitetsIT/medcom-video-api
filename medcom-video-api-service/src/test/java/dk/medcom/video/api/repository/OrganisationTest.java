package dk.medcom.video.api.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import dk.medcom.video.api.dao.OrganisationRepository;
import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.entity.Organisation;

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
		organisation.setPoolSize(10);
		organisation.setName(name);
		organisation.setGroupId(1);
		organisation.setSmsCallbackUrl("some_url");

		// When
		organisation = subject.save(organisation);
		
		// Then
		Assert.assertNotNull(organisation);
		Assert.assertNotNull(organisation.getId());
		assertEquals(organisationId,  organisation.getOrganisationId());
		assertEquals(name,  organisation.getName());
		assertEquals(name,  organisation.toString());
		assertEquals(10, organisation.getPoolSize().longValue());
		assertEquals(1L, organisation.getGroupId());
		assertEquals("some_url", organisation.getSmsCallbackUrl());
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
		assertEquals(11, numberOfOrganisations);
	}
	
	@Test
	public void testFindOrganisationWithExistingId() {
		// Given
		Long id = new Long(1);
		
		// When
		Organisation organisation = subject.findById(id).orElse(null);
		
		// Then
		Assert.assertNotNull(organisation);
		assertEquals(id, organisation.getId());
		assertEquals("company 1", organisation.getOrganisationId());
		assertEquals("company name 1", organisation.getName());
		assertEquals("SomeSender", organisation.getSmsSenderName());
		Assert.assertNull(organisation.getPoolSize());
	}

	@Test
	public void testFindOrganisationWithNonExistingId() {
		// Given
		Long id = new Long(1999);
		
		// When
		Organisation organisation = subject.findById(id).orElse(null);
		
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

	@Test
	public void testFindOrganizationWithPool() {
		// Given
		String existingOrg = "pool-test-org";

		// When
		Organisation organisation = subject.findByOrganisationId(existingOrg);

		// Then
		Assert.assertNotNull(organisation);
		assertEquals(10, organisation.getPoolSize().longValue());
		assertEquals("pool-test-org", organisation.getOrganisationId());
		assertEquals("company name another-test-org", organisation.getName());
	}

	@Test
	public void testFindAllPoolOrganizations() {
		List<Organisation> organizations = subject.findByPoolSizeNotNull();

		assertNotNull(organizations);
		assertEquals(4, organizations.size());

		var optionalOorganization = organizations.stream().filter(x -> x.getName().equals("company name another-test-org")).findFirst();

		assertTrue(optionalOorganization.isPresent());
		var organization = optionalOorganization.get();
		assertEquals("pool-test-org", organization.getOrganisationId());
		assertEquals(10, organization.getPoolSize().intValue());
	}
}