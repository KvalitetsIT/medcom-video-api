package dk.medcom.video.api.repository;

import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class SchedulingTemplateRepositoryTest extends RepositoryTest{

	@Resource
    private SchedulingTemplateRepository subject;
	
	@Resource
    private OrganisationRepository subjectO;
	
	@Test
	public void testCreateSchedulingTemplate() {
		
		// Given
		Long organisationId = 1L; 
		Long conferencingSysId = 7L; 
		String uriPrefix = "abcd";
		String uriDomain = "test7.dk"; 
		boolean hostPinRequired = true; 
		Long hostPinRangeLow = 7L;
		Long hostPinRangeHigh = 97L;
		boolean guestPinRequired = false; 
		Long guestPinRangeLow = 107L;
		Long guestPinRangeHigh = 997L;
		int vMRAvailableBefore = 10; 
		int maxParticipants = 17;
		boolean endMeetingOnEndTime = true;
		Long uriNumberRangeLow = 1007L;
		Long uriNumberRangeHigh = 9997L;
		String ivrTheme = "/api/admin/configuration/v1/ivr_theme/10/";
		
		Organisation organisation = subjectO.findById(organisationId).orElse(null);
		
		SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
		schedulingTemplate.setOrganisation(organisation);
		schedulingTemplate.setConferencingSysId(conferencingSysId);
		schedulingTemplate.setUriPrefix(uriPrefix);
		schedulingTemplate.setUriDomain(uriDomain);
		schedulingTemplate.setHostPinRequired(hostPinRequired);
		schedulingTemplate.setHostPinRangeLow(hostPinRangeLow);
		schedulingTemplate.setHostPinRangeHigh(hostPinRangeHigh);
		schedulingTemplate.setGuestPinRequired(guestPinRequired);
		schedulingTemplate.setGuestPinRangeLow(guestPinRangeLow);
		schedulingTemplate.setGuestPinRangeHigh(guestPinRangeHigh);
		schedulingTemplate.setVMRAvailableBefore(vMRAvailableBefore);
		schedulingTemplate.setMaxParticipants(maxParticipants);
		schedulingTemplate.setEndMeetingOnEndTime(endMeetingOnEndTime);
		schedulingTemplate.setUriNumberRangeLow(uriNumberRangeLow);
		schedulingTemplate.setUriNumberRangeHigh(uriNumberRangeHigh);
		schedulingTemplate.setIvrTheme(ivrTheme);
		
		// When
		schedulingTemplate = subject.save(schedulingTemplate);
		
		// Then
		assertNotNull(schedulingTemplate);
		assertNotNull(schedulingTemplate.getId());
		Assert.assertEquals(organisation, schedulingTemplate.getOrganisation());
		Assert.assertEquals(conferencingSysId, schedulingTemplate.getConferencingSysId());
		Assert.assertEquals(uriPrefix, schedulingTemplate.getUriPrefix());
		Assert.assertEquals(uriDomain, schedulingTemplate.getUriDomain());
		Assert.assertEquals(hostPinRequired, schedulingTemplate.getHostPinRequired());
		Assert.assertEquals(hostPinRangeLow, schedulingTemplate.getHostPinRangeLow());
		Assert.assertEquals(hostPinRangeHigh, schedulingTemplate.getHostPinRangeHigh());
		Assert.assertEquals(guestPinRequired, schedulingTemplate.getGuestPinRequired());
		Assert.assertEquals(guestPinRangeLow, schedulingTemplate.getGuestPinRangeLow());
		Assert.assertEquals(guestPinRangeHigh, schedulingTemplate.getGuestPinRangeHigh());
		Assert.assertEquals(vMRAvailableBefore, schedulingTemplate.getVMRAvailableBefore());
		Assert.assertEquals(maxParticipants, schedulingTemplate.getMaxParticipants());
		Assert.assertEquals(endMeetingOnEndTime, schedulingTemplate.getEndMeetingOnEndTime());
		Assert.assertEquals(uriNumberRangeLow, schedulingTemplate.getUriNumberRangeLow());
		Assert.assertEquals(uriNumberRangeHigh, schedulingTemplate.getUriNumberRangeHigh());
		Assert.assertEquals(ivrTheme, schedulingTemplate.getIvrTheme());
				
	}

	@Test
	public void testFindAllSchedulingTemplate() {
		// Given
		
		// When
		Iterable<SchedulingTemplate> schedulingTemplates = subject.findAll();
		
		// Then
		assertNotNull(schedulingTemplates);
		int numberOfSchedulingTemplates = 0;
		for (SchedulingTemplate schedulingTemplate : schedulingTemplates) {
			assertNotNull(schedulingTemplate);
			numberOfSchedulingTemplates++;
		}
		Assert.assertEquals(6, numberOfSchedulingTemplates);
	}
	
	@Test
	public void testFindSchedulingTemplateWithExistingId() {
		// Given
		Long id = 1L;
		
		// When
		SchedulingTemplate schedulingTemplate = subject.findById(id).orElse(null);
		
		// Then
		assertNotNull(schedulingTemplate);
		Assert.assertEquals(1L, schedulingTemplate.getOrganisation().getId().longValue());
		Assert.assertEquals(id, schedulingTemplate.getId());
		Assert.assertEquals(22L, schedulingTemplate.getConferencingSysId().longValue());
		Assert.assertEquals("abc", schedulingTemplate.getUriPrefix());
		Assert.assertEquals("test.dk", schedulingTemplate.getUriDomain());
		Assert.assertTrue(schedulingTemplate.getHostPinRequired());
		Assert.assertEquals(1L, schedulingTemplate.getHostPinRangeLow().longValue());
		Assert.assertEquals(91L, schedulingTemplate.getHostPinRangeHigh().longValue());
		Assert.assertFalse(schedulingTemplate.getGuestPinRequired());
		Assert.assertEquals(100L, schedulingTemplate.getGuestPinRangeLow().longValue());
		Assert.assertEquals(991L, schedulingTemplate.getGuestPinRangeHigh().longValue());
		Assert.assertEquals(15, schedulingTemplate.getVMRAvailableBefore());
		Assert.assertEquals(10, schedulingTemplate.getMaxParticipants());
		Assert.assertTrue(schedulingTemplate.getEndMeetingOnEndTime());
		Assert.assertEquals(1000L, schedulingTemplate.getUriNumberRangeLow().longValue());
		Assert.assertEquals(9991L, schedulingTemplate.getUriNumberRangeHigh().longValue());
		Assert.assertEquals("/api/admin/configuration/v1/ivr_theme/10/", schedulingTemplate.getIvrTheme());
	}

	@Test
	public void testFindSchedulingTemplateWithNonExistingId() {
		// Given
		Long id = 1999L;
		
		// When
		SchedulingTemplate schedulingTemplate = subject.findById(id).orElse(null);
		
		// Then
		Assert.assertNull(schedulingTemplate);
	}
	@Test
	public void testFindScheduligTemplateWithExistingOrganisation() {
		// Given
		Organisation organisation = subjectO.findById(1L).orElse(null);
		
		// When	
		List<SchedulingTemplate> schedulingTemplates = subject.findByOrganisationAndDeletedTimeIsNull(organisation); 
		
		// Then
		assertNotNull(schedulingTemplates);
		Assert.assertEquals(1, schedulingTemplates.size());
	}
	
	@Test
	public void testFindScheduligTemplateWithOrganisationNull() {
		
		// Given
		Long conferencingSysId = 7L; 
		String uriPrefix = "abcd";
		String uriDomain = "test7.dk"; 
		boolean hostPinRequired = true; 
		Long hostPinRangeLow = 7L;
		Long hostPinRangeHigh = 97L;
		boolean guestPinRequired = false; 
		Long guestPinRangeLow = 107L;
		Long guestPinRangeHigh = 997L;
		int vMRAvailableBefore = 10; 
		int maxParticipants = 17;
		boolean endMeetingOnEndTime = true;
		Long uriNumberRangeLow = 1007L;
		Long uriNumberRangeHigh = 9997L;
		String ivrTheme = "/api/admin/configuration/v1/ivr_theme/10/";

		// When
		// Then
		Iterable<SchedulingTemplate> schedulingTemplates = subject.findByOrganisationIsNullAndDeletedTimeIsNull();
		int numberOfSchedulingTemplates = 0;
		for (SchedulingTemplate schedulingTemplate : schedulingTemplates) {
			Assert.assertNull(schedulingTemplate.getOrganisation());
			numberOfSchedulingTemplates++;
		}
		
		if (numberOfSchedulingTemplates < 1) {
			SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
			schedulingTemplate.setConferencingSysId(conferencingSysId);
			schedulingTemplate.setUriPrefix(uriPrefix);
			schedulingTemplate.setUriDomain(uriDomain);
			schedulingTemplate.setHostPinRequired(hostPinRequired);
			schedulingTemplate.setHostPinRangeLow(hostPinRangeLow);
			schedulingTemplate.setHostPinRangeHigh(hostPinRangeHigh);
			schedulingTemplate.setGuestPinRequired(guestPinRequired);
			schedulingTemplate.setGuestPinRangeLow(guestPinRangeLow);
			schedulingTemplate.setGuestPinRangeHigh(guestPinRangeHigh);
			schedulingTemplate.setVMRAvailableBefore(vMRAvailableBefore);
			schedulingTemplate.setMaxParticipants(maxParticipants);
			schedulingTemplate.setEndMeetingOnEndTime(endMeetingOnEndTime);
			schedulingTemplate.setUriNumberRangeLow(uriNumberRangeLow);
			schedulingTemplate.setUriNumberRangeHigh(uriNumberRangeHigh);
			schedulingTemplate.setIvrTheme(ivrTheme);

			subject.save(schedulingTemplate);
			schedulingTemplates = subject.findByOrganisationIsNullAndDeletedTimeIsNull();
			
			assertNotNull(schedulingTemplates);
			numberOfSchedulingTemplates = 0;
			for (SchedulingTemplate schedulingTemplate2 : schedulingTemplates) {
				assertNotNull(schedulingTemplate2);
				Assert.assertNull(schedulingTemplate2.getOrganisation());
				numberOfSchedulingTemplates++;
			}
			Assert.assertEquals(1, numberOfSchedulingTemplates);
			
		}
				
	}
	@Test
	public void testFindScheduligTemplateWithExistingOrganisationAndIsDefault() {
		// Given
		Organisation organisation = subjectO.findById(3L).orElse(null);
		
		// When	
		List<SchedulingTemplate> schedulingTemplates = subject.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(organisation, true); 
		
		// Then
		assertNotNull(schedulingTemplates);
		Assert.assertEquals(1, schedulingTemplates.size());
	}
	
	@Test
	public void testFindScheduligTemplateWithExistingOrganisationAndId() {
		// Given
		Organisation organisation = subjectO.findById(3L).orElse(null);
		Long id = 5L;
		
		// When	
		SchedulingTemplate schedulingTemplate = subject.findByOrganisationAndIdAndDeletedTimeIsNull(organisation, id); 
		
		// Then
		assertNotNull(schedulingTemplate);
		Assert.assertEquals(id, schedulingTemplate.getId());
	
	}
	
	@Test
	public void testFindScheduligTemplateWithExistingOrganisationAndIdNotFound() {
		// Given
		Organisation organisation = subjectO.findById(3L).orElse(null);
		Long id = 777L;
		
		// When	
		SchedulingTemplate schedulingTemplate = subject.findByOrganisationAndIdAndDeletedTimeIsNull(organisation, id); 
		
		// Then
		Assert.assertNull(schedulingTemplate);
	
	}

	@Test
	public void testFindByOrganisationIdAndIsDefaultTemplateAndDeletedTimeIsNull() {
		var result = subject.findByOrganisationIdAndIsDefaultTemplateAndDeletedTimeIsNull("pool-test-org");

		assertNotNull(result);
		Assert.assertEquals(1, result.size());
	}
}