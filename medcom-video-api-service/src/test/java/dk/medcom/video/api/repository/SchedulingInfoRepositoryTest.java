package dk.medcom.video.api.repository;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingTemplate;

public class SchedulingInfoRepositoryTest extends RepositoryTest{

	@Resource
    private SchedulingInfoRepository subject;

	@Resource
    private MeetingRepository subjectM;
	
	@Resource
    private SchedulingTemplateRepository subjectST;
	
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
		
		int provisionStatus = 0;
		String provisionVMRId = "PVMRID";
	    Calendar calendar = new GregorianCalendar(2018,10,01,13,15,00);
		
		Long meetingId = new Long(4);
		Long schedulingTemplateId = new Long(1);
		
		SchedulingInfo schedulingInfo = new SchedulingInfo();
		schedulingInfo.setHostPin(hostPin);
		schedulingInfo.setGuestPin(guestPin);
		schedulingInfo.setVMRAvailableBefore(vMRAvailableBefore);
		
		schedulingInfo.setMaxParticipants(maxParticipants);
		schedulingInfo.setEndMeetingOnEndTime(endMeetingOnEndTime);
		schedulingInfo.setUriWithDomain(uriWithDomain);
		schedulingInfo.setUriWithoutDomain(uriWithoutDomain);
		schedulingInfo.setProvisionStatus(provisionStatus);
		schedulingInfo.setProvisionTimestamp(calendar.getTime());
		schedulingInfo.setProvisionVMRId(provisionVMRId);
		
		Meeting meeting = subjectM.findOne(meetingId);
		schedulingInfo.setMeeting(meeting);
		schedulingInfo.setUuid(meeting.getUuid());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(meeting.getStartTime());
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - schedulingInfo.getVMRAvailableBefore());
		schedulingInfo.setvMRStartTime(cal.getTime());

		
		SchedulingTemplate schedulingTemplate = subjectST.findOne(schedulingTemplateId);
		schedulingInfo.setSchedulingTemplate(schedulingTemplate);
		
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
		Assert.assertEquals(uriWithDomain, schedulingInfo.getUriWithDomain());
		Assert.assertEquals(uriWithoutDomain, schedulingInfo.getUriWithoutDomain());
		Assert.assertEquals(provisionStatus, schedulingInfo.getProvisionStatus());
		Assert.assertEquals(calendar.getTime(), schedulingInfo.getProvisionTimestamp());
		Assert.assertEquals(provisionVMRId, schedulingInfo.getProvisionVMRId());
		
		Assert.assertEquals(meetingId, schedulingInfo.getMeeting().getId());
		Assert.assertEquals(meeting.getUuid(), schedulingInfo.getUuid());
		Assert.assertEquals(schedulingTemplateId, schedulingInfo.getSchedulingTemplate().getId());
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
		Assert.assertEquals(3, numberOfSchedulingInfo);
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
		int provisionStatus = 0;
		
		// When
		Iterable<SchedulingInfo> schedulingInfos = subject.findAllWithinAdjustedTimeIntervalAndStatus(calendarFrom.getTime(), calendarTo.getTime(), provisionStatus);
		
		// Then
		Assert.assertNotNull(schedulingInfos);
		int numberOfSchedulingInfo = 0;
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			Assert.assertNotNull(schedulingInfo);
			numberOfSchedulingInfo++;
		}
		Assert.assertEquals(3, numberOfSchedulingInfo);
	}
	@Test
	public void testFindOneInIntervalAndProvisionStatus0() {
		// Given
		
	    Calendar calendarFrom = new GregorianCalendar(2018,11,01,15,15,00); //month is zero-based
	    Calendar calendarTo = new GregorianCalendar(2018,11,02,14,31,00);   //Interval hits meeting id 3 ('2018-12-02 15:00:00', '2018-12-02 16:00:00', vmrstart_time is '2018-12-02 14:30:00') 
		int provisionStatus = 0;
	
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

}