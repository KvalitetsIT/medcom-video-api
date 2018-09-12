package dk.medcom.video.api.repository;


import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingInfo;

public class SchedulingInfoRepositoryTest extends RepositoryTest{

	@Resource
    private SchedulingInfoRepository subject;

	@Resource
    private MeetingRepository subjectM;
	
	@Test
	public void testSchedulingInfo() {
		
		// Given
		Long hostPin = 1010L;
		Long guestPin = 2010L;
		int vMRAvailableBefore = 45; 
		int maxParticipants = 20;
		Long meetingId = new Long(1);
		
		SchedulingInfo schedulingInfo = new SchedulingInfo();
		schedulingInfo.setHostPin(hostPin);
		schedulingInfo.setGuestPin(guestPin);
		schedulingInfo.setVMRAvailableBefore(vMRAvailableBefore);
		schedulingInfo.setMaxParticipants(maxParticipants);
		
		Meeting meeting = subjectM.findOne(meetingId);
		schedulingInfo.setMeeting(meeting);
		schedulingInfo.setUuid(meeting.getUuid());
		
		// When
		schedulingInfo = subject.save(schedulingInfo);
		
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(schedulingInfo.getId());
		Assert.assertEquals(hostPin, schedulingInfo.getHostPin());
		Assert.assertEquals(guestPin, schedulingInfo.getGuestPin());
		Assert.assertEquals(vMRAvailableBefore, schedulingInfo.getVMRAvailableBefore());
		Assert.assertEquals(maxParticipants, schedulingInfo.getMaxParticipants());
		Assert.assertEquals(meetingId, schedulingInfo.getMeeting().getId());
		Assert.assertEquals(meeting.getUuid(), schedulingInfo.getUuid());
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
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(meeting);
		Assert.assertEquals(meetingId, schedulingInfo.getMeeting().getId());
	}

}