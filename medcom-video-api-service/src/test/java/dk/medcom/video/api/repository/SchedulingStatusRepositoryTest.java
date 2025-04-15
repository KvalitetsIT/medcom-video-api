package dk.medcom.video.api.repository;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jakarta.annotation.Resource;

import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.SchedulingStatusRepository;
import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.SchedulingStatus;
import dk.medcom.video.api.api.ProvisionStatus;

public class SchedulingStatusRepositoryTest extends RepositoryTest{
	@Resource
    private SchedulingStatusRepository subject;

	@Resource
    private MeetingRepository subjectM;
	
	@Test
	public void testSchedulingStatus() {
		// Given
		ProvisionStatus provisionStatus = ProvisionStatus.AWAITS_PROVISION;
		String provisionStatusDescription = "All is okay yet";
		Long meetingId = 4L;
		Calendar calendar = new GregorianCalendar(2018, Calendar.NOVEMBER,1,13,15,0);
		Meeting meeting = subjectM.findById(meetingId).orElse(null);
	    
		SchedulingStatus schedulingStatus = new SchedulingStatus();
		schedulingStatus.setTimeStamp(calendar.getTime());
		schedulingStatus.setProvisionStatus(provisionStatus);
		schedulingStatus.setProvisionStatusDescription(provisionStatusDescription);
		schedulingStatus.setMeeting(meeting);
		
		// When
		schedulingStatus = subject.save(schedulingStatus);

		// Then
		Assert.assertNotNull(schedulingStatus);
		Assert.assertNotNull(schedulingStatus.getId());
		Assert.assertEquals(provisionStatus, schedulingStatus.getProvisionStatus());
		Assert.assertEquals(provisionStatusDescription, schedulingStatus.getProvisionStatusDescription());
		Assert.assertEquals(meetingId, schedulingStatus.getMeeting().getId());
		
		Assert.assertEquals(calendar.getTime(), schedulingStatus.getTimeStamp());
	}
	
	@Test
	public void testFindSchedulingStatusWithExistingId() {
		// Given
		Long id = 301L;
		
		// When
		SchedulingStatus schedulingStatus = subject.findById(id).orElse(null);
		
		// Then
		Assert.assertNotNull(schedulingStatus);
		Assert.assertEquals(id, schedulingStatus.getId());
		Assert.assertEquals(ProvisionStatus.AWAITS_PROVISION, schedulingStatus.getProvisionStatus());
		Assert.assertEquals("all ok", schedulingStatus.getProvisionStatusDescription());
	}

	@Test
	public void testFindSchedulingStatusWithNonExistingId() {
		// Given
		Long id = 1999L;
		
		// When
		SchedulingStatus schedulingStatus = subject.findById(id).orElse(null);
		
		// Then
		Assert.assertNull(schedulingStatus);
	}
	
	@Test
	public void testGetMeetingOnExistingSchedulingStatus() {
		
		// Given
		Long schedulingStatusId = 301L;
		Long meetingId = 1L;
			
		// When
		SchedulingStatus schedulingStatus = subject.findById(schedulingStatusId).orElse(null);
			
		// Then
		Assert.assertNotNull(schedulingStatus);
		Assert.assertEquals(meetingId, schedulingStatus.getMeeting().getId());
	}

	@Test
	public void testSetMeetingOnExistingSchedulingStatus() {

		// Given
		Long schedulingStatusId = 302L;
		Long meetingId = 2L;
			
		// When
		SchedulingStatus schedulingStatus = subject.findById(schedulingStatusId).orElse(null);
	    Meeting meeting = subjectM.findById(meetingId).orElse(null);
	    schedulingStatus.setMeeting(meeting);
			
		// Then
		Assert.assertNotNull(schedulingStatus);
		Assert.assertNotNull(meeting);
		Assert.assertEquals(meetingId, schedulingStatus.getMeeting().getId());
	}

}