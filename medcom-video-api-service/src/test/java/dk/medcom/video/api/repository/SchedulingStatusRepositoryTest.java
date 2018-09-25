package dk.medcom.video.api.repository;


import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingStatus;
import dk.medcom.video.api.dto.ProvisionStatus;

public class SchedulingStatusRepositoryTest extends RepositoryTest{

	@Resource
    private SchedulingStatusRepository subject;

	@Resource
    private MeetingRepository subjectM;
	
	@Test
	public void testSchedulingStatus() {
		
		// Given
		ProvisionStatus provisionStatus = ProvisionStatus.AWAITS_PROVISION; 
		Long meetingId = new Long(4);
		Calendar calendar = new GregorianCalendar(2018,10,01,13,15,00);
		Meeting meeting = subjectM.findOne(meetingId);
	    
		SchedulingStatus schedulingStatus = new SchedulingStatus();
		schedulingStatus.setTimeStamp(calendar.getTime());
		schedulingStatus.setProvisionStatus(provisionStatus);
		schedulingStatus.setMeeting(meeting);
		
		// When
		schedulingStatus = subject.save(schedulingStatus);
		
		
		// Then
		Assert.assertNotNull(schedulingStatus);
		Assert.assertNotNull(schedulingStatus.getId());
		Assert.assertEquals(provisionStatus, schedulingStatus.getProvisionStatus());
		Assert.assertEquals(meetingId, schedulingStatus.getMeeting().getId());
		
		Assert.assertEquals(calendar.getTime(), schedulingStatus.getTimeStamp());
	}
	
	@Test
	public void testFindSchedulingStatusWithExistingId() {
		// Given
		Long id = new Long(301);
		
		// When
		SchedulingStatus schedulingStatus = subject.findOne(id);
		
		// Then
		Assert.assertNotNull(schedulingStatus);
		Assert.assertEquals(id, schedulingStatus.getId());
		Assert.assertEquals(ProvisionStatus.AWAITS_PROVISION, schedulingStatus.getProvisionStatus());
	}

	@Test
	public void testFindSchedulingStatusWithNonExistingId() {
		// Given
		Long id = new Long(1999);
		
		// When
		SchedulingStatus schedulingStatus = subject.findOne(id);
		
		// Then
		Assert.assertNull(schedulingStatus);
	}
	
	@Test
	public void testGetMeetingOnExistingSchedulingStatus() {
		
		// Given
		Long schedulingStatusId = new Long(301);
		Long meetingId = new Long(1);
			
		// When
		SchedulingStatus schedulingStatus = subject.findOne(schedulingStatusId);
			
		// Then
		Assert.assertNotNull(schedulingStatus);
		Assert.assertEquals(meetingId, schedulingStatus.getMeeting().getId());
	}

	@Test
	public void testSetMeetingOnExistingSchedulingStatus() {

		// Given
		Long schedulingStatusId = new Long(302);
		Long meetingId = new Long(2);
			
		// When
		SchedulingStatus schedulingStatus = subject.findOne(schedulingStatusId);
	    Meeting meeting = subjectM.findOne(meetingId);
	    schedulingStatus.setMeeting(meeting);
			
		// Then
		Assert.assertNotNull(schedulingStatus);
		Assert.assertNotNull(meeting);
		Assert.assertEquals(meetingId, schedulingStatus.getMeeting().getId());
	}

}