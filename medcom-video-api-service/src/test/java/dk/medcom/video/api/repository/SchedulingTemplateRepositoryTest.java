package dk.medcom.video.api.repository;

import dk.medcom.video.api.api.DirectMedia;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
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
		String customPortalGuest = UUID.randomUUID().toString();
		String customPortalHost = UUID.randomUUID().toString();
		String returnUrl = UUID.randomUUID().toString();

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
		schedulingTemplate.setCustomPortalGuest(customPortalGuest);
		schedulingTemplate.setCustomPortalHost(customPortalHost);
		schedulingTemplate.setReturnUrl(returnUrl);
		schedulingTemplate.setDirectMedia(DirectMedia.best_effort);

		// When
		schedulingTemplate = subject.save(schedulingTemplate);
		
		// Then
		assertNotNull(schedulingTemplate);
		assertNotNull(schedulingTemplate.getId());
		assertEquals(organisation, schedulingTemplate.getOrganisation());
		assertEquals(conferencingSysId, schedulingTemplate.getConferencingSysId());
		assertEquals(uriPrefix, schedulingTemplate.getUriPrefix());
		assertEquals(uriDomain, schedulingTemplate.getUriDomain());
		assertEquals(hostPinRequired, schedulingTemplate.getHostPinRequired());
		assertEquals(hostPinRangeLow, schedulingTemplate.getHostPinRangeLow());
		assertEquals(hostPinRangeHigh, schedulingTemplate.getHostPinRangeHigh());
		assertEquals(guestPinRequired, schedulingTemplate.getGuestPinRequired());
		assertEquals(guestPinRangeLow, schedulingTemplate.getGuestPinRangeLow());
		assertEquals(guestPinRangeHigh, schedulingTemplate.getGuestPinRangeHigh());
		assertEquals(vMRAvailableBefore, schedulingTemplate.getVMRAvailableBefore());
		assertEquals(maxParticipants, schedulingTemplate.getMaxParticipants());
		assertEquals(endMeetingOnEndTime, schedulingTemplate.getEndMeetingOnEndTime());
		assertEquals(uriNumberRangeLow, schedulingTemplate.getUriNumberRangeLow());
		assertEquals(uriNumberRangeHigh, schedulingTemplate.getUriNumberRangeHigh());
		assertEquals(ivrTheme, schedulingTemplate.getIvrTheme());
		assertEquals(customPortalGuest, schedulingTemplate.getCustomPortalGuest());
		assertEquals(customPortalHost, schedulingTemplate.getCustomPortalHost());
		assertEquals(returnUrl, schedulingTemplate.getReturnUrl());
		assertEquals(DirectMedia.best_effort, schedulingTemplate.getDirectMedia());
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
		assertEquals(8, numberOfSchedulingTemplates);
	}
	
	@Test
	public void testFindSchedulingTemplateWithExistingId() {
		// Given
		Long id = 1L;
		
		// When
		SchedulingTemplate schedulingTemplate = subject.findById(id).orElse(null);
		
		// Then
		assertNotNull(schedulingTemplate);
		assertEquals(1L, schedulingTemplate.getOrganisation().getId().longValue());
		assertEquals(id, schedulingTemplate.getId());
		assertEquals(22L, schedulingTemplate.getConferencingSysId().longValue());
		assertEquals("abc", schedulingTemplate.getUriPrefix());
		assertEquals("test.dk", schedulingTemplate.getUriDomain());
		Assert.assertTrue(schedulingTemplate.getHostPinRequired());
		assertEquals(1L, schedulingTemplate.getHostPinRangeLow().longValue());
		assertEquals(91L, schedulingTemplate.getHostPinRangeHigh().longValue());
		Assert.assertFalse(schedulingTemplate.getGuestPinRequired());
		assertEquals(100L, schedulingTemplate.getGuestPinRangeLow().longValue());
		assertEquals(991L, schedulingTemplate.getGuestPinRangeHigh().longValue());
		assertEquals(15, schedulingTemplate.getVMRAvailableBefore());
		assertEquals(10, schedulingTemplate.getMaxParticipants());
		Assert.assertTrue(schedulingTemplate.getEndMeetingOnEndTime());
		assertEquals(1000L, schedulingTemplate.getUriNumberRangeLow().longValue());
		assertEquals(9991L, schedulingTemplate.getUriNumberRangeHigh().longValue());
		assertEquals("/api/admin/configuration/v1/ivr_theme/10/", schedulingTemplate.getIvrTheme());
		assertEquals("some_portal_guest", schedulingTemplate.getCustomPortalGuest());
		assertEquals("some_portal_host", schedulingTemplate.getCustomPortalHost());
		assertEquals("return_url", schedulingTemplate.getReturnUrl());
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
	public void testFindSchedulingTemplateWithExistingOrganisation() {
		// Given
		Organisation organisation = subjectO.findById(1L).orElse(null);
		
		// When	
		List<SchedulingTemplate> schedulingTemplates = subject.findByOrganisationAndDeletedTimeIsNull(organisation); 
		
		// Then
		assertNotNull(schedulingTemplates);
		assertEquals(1, schedulingTemplates.size());
	}
	
	@Test
	public void testFindSchedulingTemplateWithOrganisationNull() {
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
			schedulingTemplate.setDirectMedia(DirectMedia.best_effort);

			subject.save(schedulingTemplate);
			schedulingTemplates = subject.findByOrganisationIsNullAndDeletedTimeIsNull();
			
			assertNotNull(schedulingTemplates);
			numberOfSchedulingTemplates = 0;
			for (SchedulingTemplate schedulingTemplate2 : schedulingTemplates) {
				assertNotNull(schedulingTemplate2);
				Assert.assertNull(schedulingTemplate2.getOrganisation());
				numberOfSchedulingTemplates++;
			}
			assertEquals(1, numberOfSchedulingTemplates);
		}
	}

	@Test
	public void testFindSchedulingTemplateWithExistingOrganisationAndIsDefault() {
		// Given
		Organisation organisation = subjectO.findById(3L).orElse(null);
		
		// When	
		List<SchedulingTemplate> schedulingTemplates = subject.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(organisation, true); 
		
		// Then
		assertNotNull(schedulingTemplates);
		assertEquals(1, schedulingTemplates.size());
	}
	
	@Test
	public void testFindSchedulingTemplateWithExistingOrganisationAndId() {
		// Given
		Organisation organisation = subjectO.findById(3L).orElse(null);
		Long id = 5L;
		
		// When	
		SchedulingTemplate schedulingTemplate = subject.findByOrganisationAndIdAndDeletedTimeIsNull(organisation, id); 
		
		// Then
		assertNotNull(schedulingTemplate);
		assertEquals(id, schedulingTemplate.getId());
	
	}
	
	@Test
	public void testFindSchedulingTemplateWithExistingOrganisationAndIdNotFound() {
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
		assertEquals(1, result.size());
	}

	@Test
	public void testFindByOrganisationAndIsPoolTemplateAndDeletedTimeIsNull() {
		// Given
		Organisation organisation = subjectO.findById(8L).orElse(null);

		// When
		List<SchedulingTemplate> schedulingTemplates = subject.findByOrganisationAndIsPoolTemplateAndDeletedTimeIsNull(organisation, true);

		// Then
		assertNotNull(schedulingTemplates);
		assertEquals(1, schedulingTemplates.size());

		var schedulingTemplate = schedulingTemplates.get(0);
		assertNotNull(schedulingTemplate);
		assertEquals(7L, schedulingTemplate.getId().longValue());
		assertEquals(8L, schedulingTemplate.getOrganisation().getId().longValue());
		assertEquals(22L, schedulingTemplate.getConferencingSysId().longValue());
		assertEquals("abc3c", schedulingTemplate.getUriPrefix());
		assertEquals("test.dk", schedulingTemplate.getUriDomain());
		Assert.assertTrue(schedulingTemplate.getHostPinRequired());
		assertEquals(1L, schedulingTemplate.getHostPinRangeLow().longValue());
		assertEquals(91L, schedulingTemplate.getHostPinRangeHigh().longValue());
		Assert.assertFalse(schedulingTemplate.getGuestPinRequired());
		assertEquals(100L, schedulingTemplate.getGuestPinRangeLow().longValue());
		assertEquals(991L, schedulingTemplate.getGuestPinRangeHigh().longValue());
		assertEquals(15, schedulingTemplate.getVMRAvailableBefore());
		assertEquals(25, schedulingTemplate.getMaxParticipants());
		Assert.assertTrue(schedulingTemplate.getEndMeetingOnEndTime());
		assertEquals(1000L, schedulingTemplate.getUriNumberRangeLow().longValue());
		assertEquals(9991L, schedulingTemplate.getUriNumberRangeHigh().longValue());
		assertEquals("/api/admin/configuration/v1/ivr_theme/30/", schedulingTemplate.getIvrTheme());
		Assert.assertTrue(schedulingTemplate.getIsPoolTemplate());
	}
}