package dk.medcom.video.api.repository;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jakarta.annotation.Resource;

import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.SchedulingStatusRepository;
import org.junit.jupiter.api.Test;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.SchedulingStatus;
import dk.medcom.video.api.dao.entity.ProvisionStatus;

import static org.junit.jupiter.api.Assertions.*;

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
		assertNotNull(schedulingStatus);
		assertNotNull(schedulingStatus.getId());
		assertEquals(provisionStatus, schedulingStatus.getProvisionStatus());
		assertEquals(provisionStatusDescription, schedulingStatus.getProvisionStatusDescription());
		assertEquals(meetingId, schedulingStatus.getMeeting().getId());
		
		assertEquals(calendar.getTime(), schedulingStatus.getTimeStamp());
	}
	
	@Test
	public void testFindSchedulingStatusWithExistingId() {
		// Given
		Long id = 301L;
		
		// When
		SchedulingStatus schedulingStatus = subject.findById(id).orElse(null);
		
		// Then
		assertNotNull(schedulingStatus);
		assertEquals(id, schedulingStatus.getId());
		assertEquals(ProvisionStatus.AWAITS_PROVISION, schedulingStatus.getProvisionStatus());
		assertEquals("all ok", schedulingStatus.getProvisionStatusDescription());
	}

	@Test
	public void testFindSchedulingStatusWithNonExistingId() {
		// Given
		Long id = 1999L;
		
		// When
		SchedulingStatus schedulingStatus = subject.findById(id).orElse(null);
		
		// Then
		assertNull(schedulingStatus);
	}
	
	@Test
	public void testGetMeetingOnExistingSchedulingStatus() {
		
		// Given
		Long schedulingStatusId = 301L;
		Long meetingId = 1L;
			
		// When
		SchedulingStatus schedulingStatus = subject.findById(schedulingStatusId).orElse(null);
			
		// Then
		assertNotNull(schedulingStatus);
		assertEquals(meetingId, schedulingStatus.getMeeting().getId());
	}

	@Test
	public void testSetMeetingOnExistingSchedulingStatus() {

		// Given
		Long schedulingStatusId = 302L;
		Long meetingId = 2L;
			
		// When
		SchedulingStatus schedulingStatus = subject.findById(schedulingStatusId).orElse(null);
        assertNotNull(schedulingStatus);
        Meeting meeting = subjectM.findById(meetingId).orElse(null);
	    schedulingStatus.setMeeting(meeting);
			
		// Then
		assertNotNull(schedulingStatus);
		assertNotNull(meeting);
		assertEquals(meetingId, schedulingStatus.getMeeting().getId());
	}

}