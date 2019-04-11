package dk.medcom.video.api.repository;


import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.ProvisionStatus;

public class SchedulingInfoRepositoryTest extends RepositoryTest{

	@Resource
    private SchedulingInfoRepository subject;

	@Resource
    private MeetingRepository subjectM;
	
	@Resource
    private SchedulingTemplateRepository subjectST;
	
	@Resource
    private MeetingUserRepository subjectMU;
	
	@Test
	public void testSchedulingInfo() {
		
		// Given
		Long hostPin = 1010L;
		Long guestPin = 2010L;
		int vMRAvailableBefore = 45; 
		int maxParticipants = 20;
		boolean endMeetingOnEndTime = true;
		String uriWithDomain = "7777@test.dk";
		String uriWithoutDomain = "7777";
		Long meetingUserId = new Long(101);
		
		ProvisionStatus provisionStatus = ProvisionStatus.AWAITS_PROVISION;
		String provisionStatusDescription = "All okay untill now";
		String provisionVMRId = "PVMRID";
	    Calendar calendar = new GregorianCalendar(2018,10,01,13,15,00);
		
		Long meetingId = new Long(4);
		Long schedulingTemplateId = new Long(1);
		
		String portalLink = "https://portal-test.vconf.dk/?url=7777@test.dk&pin=2010&start_dato=2018-12-02T15:00:00";
		String ivrTheme = "/api/admin/configuration/v1/ivr_theme/10/";
		
		SchedulingInfo schedulingInfo = new SchedulingInfo();
		schedulingInfo.setHostPin(hostPin);
		schedulingInfo.setGuestPin(guestPin);
		schedulingInfo.setVMRAvailableBefore(vMRAvailableBefore);
		
		schedulingInfo.setMaxParticipants(maxParticipants);
		schedulingInfo.setEndMeetingOnEndTime(endMeetingOnEndTime);
		schedulingInfo.setUriWithDomain(uriWithDomain);
		schedulingInfo.setUriWithoutDomain(uriWithoutDomain);
		schedulingInfo.setProvisionStatus(provisionStatus);
		schedulingInfo.setProvisionStatusDescription(provisionStatusDescription);
		schedulingInfo.setProvisionTimestamp(calendar.getTime());
		schedulingInfo.setProvisionVMRId(provisionVMRId);
		
		Meeting meeting = subjectM.findOne(meetingId);
		schedulingInfo.setMeeting(meeting);
		schedulingInfo.setUuid(meeting.getUuid());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(meeting.getStartTime());
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - schedulingInfo.getVMRAvailableBefore());
		schedulingInfo.setvMRStartTime(cal.getTime());
		
		MeetingUser meetingUser = subjectMU.findOne(meetingUserId);
		schedulingInfo.setMeetingUser(meetingUser);
	    schedulingInfo.setUpdatedByUser(meetingUser);
		
		Calendar calendarCreate = new GregorianCalendar(2018,8,01,13,30,00);
	    schedulingInfo.setCreatedTime(calendarCreate.getTime());
	    schedulingInfo.setUpdatedTime(calendarCreate.getTime());

		SchedulingTemplate schedulingTemplate = subjectST.findOne(schedulingTemplateId);
		schedulingInfo.setSchedulingTemplate(schedulingTemplate);
		schedulingInfo.setPortalLink(portalLink);
		schedulingInfo.setIvrTheme(ivrTheme);
		
		// When
		schedulingInfo = subject.save(schedulingInfo);
		
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(schedulingInfo.getId());
		Assert.assertEquals(hostPin, schedulingInfo.getHostPin());
		Assert.assertEquals(guestPin, schedulingInfo.getGuestPin());
		Assert.assertEquals(vMRAvailableBefore, schedulingInfo.getVMRAvailableBefore());
		Assert.assertEquals(cal.getTime(), schedulingInfo.getvMRStartTime());
		Assert.assertEquals(maxParticipants, schedulingInfo.getMaxParticipants());
		Assert.assertEquals(endMeetingOnEndTime, schedulingInfo.getEndMeetingOnEndTime());
		Assert.assertEquals(uriWithDomain, schedulingInfo.getUriWithDomain());
		Assert.assertEquals(uriWithoutDomain, schedulingInfo.getUriWithoutDomain());
		Assert.assertEquals(provisionStatus, schedulingInfo.getProvisionStatus());
		Assert.assertEquals(provisionStatusDescription, schedulingInfo.getProvisionStatusDescription());
		Assert.assertEquals(calendar.getTime(), schedulingInfo.getProvisionTimestamp());
		Assert.assertEquals(provisionVMRId, schedulingInfo.getProvisionVMRId());
		
		Assert.assertEquals(meetingId, schedulingInfo.getMeeting().getId());
		Assert.assertEquals(meeting.getUuid(), schedulingInfo.getUuid());
		Assert.assertEquals(schedulingTemplateId, schedulingInfo.getSchedulingTemplate().getId());
		Assert.assertEquals(portalLink, schedulingInfo.getPortalLink());
		Assert.assertEquals(ivrTheme, schedulingInfo.getIvrTheme());
		
		Assert.assertEquals(calendarCreate.getTime(), schedulingInfo.getCreatedTime());
		Assert.assertEquals(calendarCreate.getTime(), schedulingInfo.getUpdatedTime());
		Assert.assertEquals(meetingUserId, schedulingInfo.getMeetingUser().getId());
		Assert.assertEquals(meetingUserId, schedulingInfo.getUpdatedByUser().getId());
	}
	
	@Test
	public void testFindAllSchedulingInfo() {
		// Given
		
		// When
		Iterable<SchedulingInfo> schedulingInfos = subject.findAll();
		
		// Then
		Assert.assertNotNull(schedulingInfos);
		int numberOfSchedulingInfo = 0;
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			Assert.assertNotNull(schedulingInfo);
			numberOfSchedulingInfo++;
		}
		Assert.assertEquals(5, numberOfSchedulingInfo);
	}
	
	@Test
	public void testFindSchedulingInfoWithExistingId() {
		// Given
		Long id = new Long(201);
		
		// When
		SchedulingInfo schedulingInfo = subject.findOne(id);
		
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertEquals(id, schedulingInfo.getId());
		Assert.assertEquals(1001L, schedulingInfo.getHostPin().longValue());
		Assert.assertEquals(2001L, schedulingInfo.getGuestPin().longValue());
		Assert.assertEquals(15, schedulingInfo.getVMRAvailableBefore());
		Assert.assertEquals(10, schedulingInfo.getMaxParticipants());
		Assert.assertEquals(ProvisionStatus.AWAITS_PROVISION, schedulingInfo.getProvisionStatus());
		Assert.assertEquals("all ok", schedulingInfo.getProvisionStatusDescription());
	}

	@Test
	public void testFindSchedulingInfoWithNonExistingId() {
		// Given
		Long id = new Long(1999);
		
		// When
		SchedulingInfo schedulingInfo = subject.findOne(id);
		
		// Then
		Assert.assertNull(schedulingInfo);
	}
	
	@Test
	public void testGetMeetingOnExistingSchedulingInfo() {
		
		// Given
		Long schedulingInfoId = new Long(201);
		Long meetingId = new Long(1);
			
		// When
		SchedulingInfo schedulingInfo = subject.findOne(schedulingInfoId);
		Meeting meeting = subjectM.findOne(meetingId);
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertEquals(meetingId, schedulingInfo.getMeeting().getId());
		Assert.assertEquals(meeting.getUuid(), schedulingInfo.getUuid());
	}

	@Test
	public void testSetMeetingOnExistingSchedulingInfo() {

		// Given
		Long schedulingInfoId = new Long(202);
		Long meetingId = new Long(2);
			
		// When
		SchedulingInfo schedulingInfo = subject.findOne(schedulingInfoId);
	    Meeting meeting = subjectM.findOne(meetingId);
	    schedulingInfo.setMeeting(meeting);
	    schedulingInfo.setUuid(meeting.getUuid());
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(meeting);
		Assert.assertEquals(meetingId, schedulingInfo.getMeeting().getId());
		Assert.assertEquals(meeting.getUuid(), schedulingInfo.getUuid());
	}
	
	@Test
	public void testFindAllInIntervalAndProvisionStatus0() {
		// Given
	    Calendar calendarFrom = new GregorianCalendar(2012,10,01,13,15,00);
	    Calendar calendarTo = new GregorianCalendar(2050,10,01,13,15,00);
	    ProvisionStatus provisionStatus = ProvisionStatus.AWAITS_PROVISION;
		
		// When
		Iterable<SchedulingInfo> schedulingInfos = subject.findAllWithinAdjustedTimeIntervalAndStatus(calendarFrom.getTime(), calendarTo.getTime(), provisionStatus);
		
		// Then
		Assert.assertNotNull(schedulingInfos);
		int numberOfSchedulingInfo = 0;
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			Assert.assertNotNull(schedulingInfo);
			numberOfSchedulingInfo++;
		}
		Assert.assertEquals(5, numberOfSchedulingInfo);
	}
	@Test
	public void testFindOneInIntervalAndProvisionStatus0() {
		// Given
		
	    Calendar calendarFrom = new GregorianCalendar(2018,11,01,15,15,00); //month is zero-based
	    Calendar calendarTo = new GregorianCalendar(2018,11,02,14,31,00);   //Interval hits meeting id 3 ('2018-12-02 15:00:00', '2018-12-02 16:00:00', vmrstart_time is '2018-12-02 14:30:00') 
	    ProvisionStatus provisionStatus = ProvisionStatus.AWAITS_PROVISION;
	
		// When
		Iterable<SchedulingInfo> schedulingInfos = subject.findAllWithinAdjustedTimeIntervalAndStatus(calendarFrom.getTime(), calendarTo.getTime(), provisionStatus);
		
		// Then
		Assert.assertNotNull(schedulingInfos);
		int numberOfSchedulingInfo = 0;
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			Assert.assertNotNull(schedulingInfo);
			numberOfSchedulingInfo++;
		}
		Assert.assertEquals(1, numberOfSchedulingInfo);
	}
	@Test
	public void testGetMeetingUserOnExistingSchedulingInfo() {
		
		// Given
		Long schedulingInfoId = new Long(202);
		Long meetingUserId = new Long(102);
			
		// When
		SchedulingInfo schedulingInfo = subject.findOne(schedulingInfoId);
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertEquals(meetingUserId, schedulingInfo.getMeetingUser().getId());

	}

	@Test
	public void testSetMeetingUserOnExistingMeeting() {
		
		// Given
		Long schedulingInfoId = new Long(203);
		Long meetingUserId = new Long(102);
			
		// When
		SchedulingInfo schedulingInfo = subject.findOne(schedulingInfoId);
	    MeetingUser meetingUser = subjectMU.findOne(meetingUserId);
	    schedulingInfo.setMeetingUser(meetingUser);	    
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(meetingUserId, schedulingInfo.getMeetingUser().getId());
	
	}
	@Test
	public void testGetUpdatedByUserOnExistingSchedulingInfo() {
		
		// Given
		Long schedulingInfoId = new Long(202);
		Long meetingUserId = new Long(102);
			
		// When
		SchedulingInfo schedulingInfo = subject.findOne(schedulingInfoId);
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertEquals(meetingUserId, schedulingInfo.getUpdatedByUser().getId());

	}

	@Test
	public void testSetUpdatedByUserOnExistingMeeting() {
		
		// Given
		Long schedulingInfoId = new Long(203);
		Long meetingUserId = new Long(102);
			
		// When
		SchedulingInfo schedulingInfo = subject.findOne(schedulingInfoId);
	    MeetingUser meetingUser = subjectMU.findOne(meetingUserId);
	    schedulingInfo.setUpdatedByUser(meetingUser);	    
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(meetingUser);
		Assert.assertEquals(meetingUserId, schedulingInfo.getUpdatedByUser().getId());
	
	}

}