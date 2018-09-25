package dk.medcom.video.api.repository;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingTemplate;;

public class SchedulingTemplateRepositoryTest extends RepositoryTest{

	@Resource
    private SchedulingTemplateRepository subject;
	
	@Resource
    private OrganisationRepository subjectO;
	
	@Test
	public void testSchedulingTemplate() {
		
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
		
		Organisation organisation = subjectO.findOne(organisationId);
		
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
		
		// When
		schedulingTemplate = subject.save(schedulingTemplate);
		
		// Then
		Assert.assertNotNull(schedulingTemplate);
		Assert.assertNotNull(schedulingTemplate.getId());
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
				
	}
	
	@Test
	public void testFindAllSchedulingTemplate() {
		// Given
		
		// When
		Iterable<SchedulingTemplate> schedulingTemplates = subject.findAll();
		
		// Then
		Assert.assertNotNull(schedulingTemplates);
		int numberOfSchedulingTemplates = 0;
		for (SchedulingTemplate schedulingTemplate : schedulingTemplates) {
			Assert.assertNotNull(schedulingTemplate);
			numberOfSchedulingTemplates++;
		}
		Assert.assertEquals(1, numberOfSchedulingTemplates);
	}
	
	@Test
	public void testFindSchedulingTemplateWithExistingId() {
		// Given
		Long id = new Long(1);
		
		// When
		SchedulingTemplate schedulingTemplate = subject.findOne(id);
		
		// Then
		Assert.assertNotNull(schedulingTemplate);
		Assert.assertEquals(1L, schedulingTemplate.getOrganisation().getId().longValue());
		Assert.assertEquals(id, schedulingTemplate.getId());
		Assert.assertEquals(22L, schedulingTemplate.getConferencingSysId().longValue());
		Assert.assertEquals("abc", schedulingTemplate.getUriPrefix());
		Assert.assertEquals("test.dk", schedulingTemplate.getUriDomain());
		Assert.assertEquals(true, schedulingTemplate.getHostPinRequired());
		Assert.assertEquals(1L, schedulingTemplate.getHostPinRangeLow().longValue());
		Assert.assertEquals(91L, schedulingTemplate.getHostPinRangeHigh().longValue());
		Assert.assertEquals(false, schedulingTemplate.getGuestPinRequired());
		Assert.assertEquals(100L, schedulingTemplate.getGuestPinRangeLow().longValue());
		Assert.assertEquals(991L, schedulingTemplate.getGuestPinRangeHigh().longValue());
		Assert.assertEquals(15, schedulingTemplate.getVMRAvailableBefore());
		Assert.assertEquals(10, schedulingTemplate.getMaxParticipants());
		Assert.assertEquals(true, schedulingTemplate.getEndMeetingOnEndTime());
		Assert.assertEquals(1000L, schedulingTemplate.getUriNumberRangeLow().longValue());
		Assert.assertEquals(9991L, schedulingTemplate.getUriNumberRangeHigh().longValue());
	}

	@Test
	public void testFindSchedulingTemplateWithNonExistingId() {
		// Given
		Long id = new Long(1999);
		
		// When
		SchedulingTemplate schedulingTemplate = subject.findOne(id);
		
		// Then
		Assert.assertNull(schedulingTemplate);
	}
	@Test
	public void testFindScheduligTemplateWithExistingOrganisation() {
		// Given
		Organisation organisation = subjectO.findOne(1L);
		
		// When	
		List<SchedulingTemplate> schedulingTemplates = subject.findByOrganisation(organisation); 
		
		// Then
		Assert.assertNotNull(schedulingTemplates);
		Assert.assertEquals(1, schedulingTemplates.size());
	}
}