package dk.medcom.video.api.repository;


import dk.medcom.video.api.dao.*;
import dk.medcom.video.api.dao.entity.*;
import dk.medcom.video.api.api.ProvisionStatus;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class SchedulingInfoRepositoryTest extends RepositoryTest {

	@Resource
    private SchedulingInfoRepository subject;

	@Resource
    private MeetingRepository subjectM;
	
	@Resource
    private SchedulingTemplateRepository subjectST;
	
	@Resource
    private MeetingUserRepository subjectMU;

	@Resource
	private OrganisationRepository subjectOrganisationRepository;
	
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
		Long meetingUserId = 101L;
		
		ProvisionStatus provisionStatus = ProvisionStatus.AWAITS_PROVISION;
		String provisionStatusDescription = "All okay untill now";
		String provisionVMRId = "PVMRID";
	    Calendar calendar = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,13,15, 0);
		
		Long meetingId = 4L;
		Long schedulingTemplateId = 1L;
		
		String portalLink = "https://portal-test.vconf.dk/?url=7777@test.dk&pin=2010&start_dato=2018-12-02T15:00:00";
		String ivrTheme = "/api/admin/configuration/v1/ivr_theme/10/";

		var reservationId = UUID.randomUUID();

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

		schedulingInfo.setReservationId(reservationId.toString());

		Meeting meeting = subjectM.findById(meetingId).orElse(null);
		assertNotNull(meeting);
		schedulingInfo.setMeeting(meeting);
		schedulingInfo.setUuid(meeting.getUuid());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(meeting.getStartTime());
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - schedulingInfo.getVMRAvailableBefore());
		schedulingInfo.setvMRStartTime(cal.getTime());
		
		MeetingUser meetingUser = subjectMU.findById(meetingUserId).orElse(null);
		schedulingInfo.setMeetingUser(meetingUser);
	    schedulingInfo.setUpdatedByUser(meetingUser);
		
		Calendar calendarCreate = new GregorianCalendar(2018, Calendar.SEPTEMBER, 1,13,30, 0);
	    schedulingInfo.setCreatedTime(calendarCreate.getTime());
	    schedulingInfo.setUpdatedTime(calendarCreate.getTime());

		SchedulingTemplate schedulingTemplate = subjectST.findById(schedulingTemplateId).orElse(null);
		schedulingInfo.setSchedulingTemplate(schedulingTemplate);
		schedulingInfo.setPortalLink(portalLink);
		schedulingInfo.setIvrTheme(ivrTheme);

		Organisation organization = subjectOrganisationRepository.findByOrganisationId("test-org");
		schedulingInfo.setOrganisation(organization);
		schedulingInfo.setPoolOverflow(true);
		
		// When
		schedulingInfo = subject.save(schedulingInfo);
		
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(schedulingInfo.getId());
		assertEquals(hostPin, schedulingInfo.getHostPin());
		assertEquals(guestPin, schedulingInfo.getGuestPin());
		assertEquals(vMRAvailableBefore, schedulingInfo.getVMRAvailableBefore());
		assertEquals(cal.getTime(), schedulingInfo.getvMRStartTime());
		assertEquals(maxParticipants, schedulingInfo.getMaxParticipants());
		assertEquals(endMeetingOnEndTime, schedulingInfo.getEndMeetingOnEndTime());
		assertEquals(uriWithDomain, schedulingInfo.getUriWithDomain());
		assertEquals(uriWithoutDomain, schedulingInfo.getUriWithoutDomain());
		assertEquals(provisionStatus, schedulingInfo.getProvisionStatus());
		assertEquals(provisionStatusDescription, schedulingInfo.getProvisionStatusDescription());
		assertEquals(calendar.getTime(), schedulingInfo.getProvisionTimestamp());
		assertEquals(provisionVMRId, schedulingInfo.getProvisionVMRId());
		
		assertEquals(meetingId, schedulingInfo.getMeeting().getId());
		assertEquals(meeting.getUuid(), schedulingInfo.getUuid());
		assertEquals(schedulingTemplateId, schedulingInfo.getSchedulingTemplate().getId());
		assertEquals(portalLink, schedulingInfo.getPortalLink());
		assertEquals(ivrTheme, schedulingInfo.getIvrTheme());
		
		assertEquals(calendarCreate.getTime(), schedulingInfo.getCreatedTime());
		assertEquals(calendarCreate.getTime(), schedulingInfo.getUpdatedTime());
		assertEquals(meetingUserId, schedulingInfo.getMeetingUser().getId());
		assertEquals(meetingUserId, schedulingInfo.getUpdatedByUser().getId());

		assertEquals(organization.getOrganisationId(), schedulingInfo.getOrganisation().getOrganisationId());

		assertEquals(reservationId.toString(), schedulingInfo.getReservationId());
		assertTrue(schedulingInfo.getPoolOverflow());
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
		assertEquals(9, numberOfSchedulingInfo);
	}
	
	@Test
	public void testFindSchedulingInfoWithExistingId() {
		// Given
		Long id = 201L;
		
		// When
		SchedulingInfo schedulingInfo = subject.findById(id).orElse(null);
		
		// Then
		Assert.assertNotNull(schedulingInfo);
		assertEquals(id, schedulingInfo.getId());
		assertEquals(1001L, schedulingInfo.getHostPin().longValue());
		assertEquals(2001L, schedulingInfo.getGuestPin().longValue());
		assertEquals(15, schedulingInfo.getVMRAvailableBefore());
		assertEquals(10, schedulingInfo.getMaxParticipants());
		assertEquals(ProvisionStatus.AWAITS_PROVISION, schedulingInfo.getProvisionStatus());
		assertEquals("all ok", schedulingInfo.getProvisionStatusDescription());
	}

	@Test
	public void testFindSchedulingInfoWithNonExistingId() {
		// Given
		Long id = 1999L;
		
		// When
		SchedulingInfo schedulingInfo = subject.findById(id).orElse(null);
		
		// Then
		assertNull(schedulingInfo);
	}
	
	@Test
	public void testGetMeetingOnExistingSchedulingInfo() {
		
		// Given
		Long schedulingInfoId = 201L;
		Long meetingId = 1L;
			
		// When
		SchedulingInfo schedulingInfo = subject.findById(schedulingInfoId).orElse(null);
		Meeting meeting = subjectM.findById(meetingId).orElse(null);
			
		// Then
		assertNotNull(meeting);
		Assert.assertNotNull(schedulingInfo);
		assertEquals(meetingId, schedulingInfo.getMeeting().getId());
		assertEquals(meeting.getUuid(), schedulingInfo.getUuid());
	}

	@Test
	public void testSetMeetingOnExistingSchedulingInfo() {

		// Given
		Long schedulingInfoId = 202L;
		Long meetingId = 2L;
			
		// When
		SchedulingInfo schedulingInfo = subject.findById(schedulingInfoId).orElse(null);
	    Meeting meeting = subjectM.findById(meetingId).orElse(null);
	    assertNotNull(schedulingInfo);
	    assertNotNull(meeting);
	    schedulingInfo.setMeeting(meeting);
	    schedulingInfo.setUuid(meeting.getUuid());
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(meeting);
		assertEquals(meetingId, schedulingInfo.getMeeting().getId());
		assertEquals(meeting.getUuid(), schedulingInfo.getUuid());
	}
	
	@Test
	public void testFindAllInIntervalAndProvisionStatus0() {
		// Given
	    Calendar calendarFrom = new GregorianCalendar(2012, Calendar.NOVEMBER, 1,13,15, 0);
	    Calendar calendarTo = new GregorianCalendar(2050, Calendar.NOVEMBER, 1,13,15, 0);
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
		assertEquals(5, numberOfSchedulingInfo);
	}
	@Test
	public void testFindOneInIntervalAndProvisionStatus0() {
		// Given
		
	    Calendar calendarFrom = new GregorianCalendar(2018, Calendar.DECEMBER, 1,15,15, 0); //month is zero-based
	    Calendar calendarTo = new GregorianCalendar(2018, Calendar.DECEMBER, 2,14,31, 0);   //Interval hits meeting id 3 ('2018-12-02 15:00:00', '2018-12-02 16:00:00', vmrstart_time is '2018-12-02 14:30:00')
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
		assertEquals(1, numberOfSchedulingInfo);
	}
	@Test
	public void testFindAllWithStartTimeLessThenAndProvisionStatus0() {
		// Given
		Calendar calendarFrom = new GregorianCalendar(2018, Calendar.NOVEMBER, 2,15,15, 0); //month is zero-based
		ProvisionStatus provisionStatus = ProvisionStatus.AWAITS_PROVISION;

		// When
		Iterable<SchedulingInfo> schedulingInfos = subject.findAllWithinStartAndEndTimeLessThenAndStatus(calendarFrom.getTime(), provisionStatus);
		// Then
		Assert.assertNotNull(schedulingInfos);
		int numberOfSchedulingInfo = 0;
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			Assert.assertNotNull(schedulingInfo);
			Assert.assertEquals(provisionStatus, schedulingInfo.getProvisionStatus());
			numberOfSchedulingInfo++;
		}
		assertEquals(1, numberOfSchedulingInfo);
	}
	@Test
	public void testFindAllWithStartTimeLessThenAndProvisionStatus0HandingZeroResult() {
		// Given
		Calendar calendarFrom = new GregorianCalendar(2018, Calendar.AUGUST, 1,15,15, 0); //month is zero-based
		ProvisionStatus provisionStatus = ProvisionStatus.AWAITS_PROVISION;

		// When
		Iterable<SchedulingInfo> schedulingInfos = subject.findAllWithinStartAndEndTimeLessThenAndStatus(calendarFrom.getTime(), provisionStatus);
		// Then
		Assert.assertNotNull(schedulingInfos);
		int numberOfSchedulingInfo = 0;
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			Assert.assertNotNull(schedulingInfo);
			Assert.assertEquals(provisionStatus, schedulingInfo.getProvisionStatus());
			numberOfSchedulingInfo++;
		}
		assertEquals(0, numberOfSchedulingInfo);
	}
	@Test
	public void testFindAllWithEndTimeLessThenAndProvisionStatus3() {
		// Given
		Calendar calendarTo = new GregorianCalendar(2019,Calendar.OCTOBER, 2,16, 0, 5);
		ProvisionStatus provisionStatus = ProvisionStatus.PROVISIONED_OK;

		// When
		Iterable<SchedulingInfo> schedulingInfos = subject.findAllWithinEndTimeLessThenAndStatus(calendarTo.getTime(), provisionStatus);
		// Then
		Assert.assertNotNull(schedulingInfos);
		int numberOfSchedulingInfo = 0;
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			Assert.assertNotNull(schedulingInfo);
			Assert.assertEquals(provisionStatus, schedulingInfo.getProvisionStatus());
			numberOfSchedulingInfo++;
		}
		assertEquals(1, numberOfSchedulingInfo);
	}
	@Test
	public void testFindAllWithEndTimeLessThenAndProvisionStatus3HandingZeroResult() {
		// Given
		Calendar calendarTo = new GregorianCalendar(2018,Calendar.OCTOBER, 2,16, 0, 5);
		ProvisionStatus provisionStatus = ProvisionStatus.PROVISIONED_OK;

		// When
		Iterable<SchedulingInfo> schedulingInfos = subject.findAllWithinEndTimeLessThenAndStatus(calendarTo.getTime(), provisionStatus);
		// Then
		Assert.assertNotNull(schedulingInfos);
		int numberOfSchedulingInfo = 0;
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			Assert.assertNotNull(schedulingInfo);
			Assert.assertEquals(provisionStatus, schedulingInfo.getProvisionStatus());
			numberOfSchedulingInfo++;
		}
		assertEquals(0, numberOfSchedulingInfo);
	}
	@Test
	public void testGetMeetingUserOnExistingSchedulingInfo() {
		
		// Given
		Long schedulingInfoId = 202L;
		Long meetingUserId = 102L;
			
		// When
		SchedulingInfo schedulingInfo = subject.findById(schedulingInfoId).orElse(null);
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		assertEquals(meetingUserId, schedulingInfo.getMeetingUser().getId());

	}

	@Test
	public void testSetMeetingUserOnExistingMeeting() {
		
		// Given
		Long schedulingInfoId = 203L;
		Long meetingUserId = 102L;
			
		// When
		SchedulingInfo schedulingInfo = subject.findById(schedulingInfoId).orElse(null);
	    MeetingUser meetingUser = subjectMU.findById(meetingUserId).orElse(null);

	    assertNotNull(schedulingInfo);
	    assertNotNull(meetingUser);
	    schedulingInfo.setMeetingUser(meetingUser);	    
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(meetingUser);
		assertEquals(meetingUserId, schedulingInfo.getMeetingUser().getId());
	
	}
	@Test
	public void testGetUpdatedByUserOnExistingSchedulingInfo() {
		
		// Given
		Long schedulingInfoId = 202L;
		Long meetingUserId = 102L;
			
		// When
		SchedulingInfo schedulingInfo = subject.findById(schedulingInfoId).orElse(null);
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		assertEquals(meetingUserId, schedulingInfo.getUpdatedByUser().getId());

	}

	@Test
	public void testSetUpdatedByUserOnExistingMeeting() {
		
		// Given
		Long schedulingInfoId = 203L;
		Long meetingUserId = 102L;
			
		// When
		SchedulingInfo schedulingInfo = subject.findById(schedulingInfoId).orElse(null);
	    MeetingUser meetingUser = subjectMU.findById(meetingUserId).orElse(null);

	    assertNotNull(schedulingInfo);
	    schedulingInfo.setUpdatedByUser(meetingUser);	    
			
		// Then
		Assert.assertNotNull(schedulingInfo);
		Assert.assertNotNull(meetingUser);
		assertEquals(meetingUserId, schedulingInfo.getUpdatedByUser().getId());
	
	}

	@Test
	public void testGetSchedulingInfoByReservationId() {
		var result = subject.findOneByReservationId("1331e914-9488-482d-9d9e-d1302c44a1de");
		assertNotNull(result);
		assertEquals("1331e914-9488-482d-9d9e-d1302c44a1de", result.getReservationId());
		assertEquals(210L, result.getId().longValue());
	}

	@Test
	public void testGetSchedulingInfoByReservationIdNotFound() {
		var result = subject.findOneByReservationId("1331e914-9488-482d-9d9e-d1302c44a1df");
		assertNull(result);
	}

	@Test
	public void testFindUnusedSchedulingInfoForOrganization() {
		Organisation organisation = new Organisation();
		organisation.setId(7L);

		List<BigInteger> schedulingInfos = subject.findByMeetingIsNullAndOrganisationAndProvisionStatus(organisation.getId(), ProvisionStatus.PROVISIONED_OK.name());
		assertNotNull(schedulingInfos);
		assertEquals(1, schedulingInfos.size());
		assertEquals(207, schedulingInfos.get(0).intValue());
	}

	@Test
	public void testUpdateReservationid()  {
		var reservationId = UUID.randomUUID();

		var optionalSchedulingInfo = subject.findById(209L);
		assertTrue(optionalSchedulingInfo.isPresent());

		optionalSchedulingInfo.get().setReservationId(reservationId.toString());
		var schedulingInfo = subject.save(optionalSchedulingInfo.get());
		assertNotNull(schedulingInfo);
		assertEquals(reservationId.toString(), optionalSchedulingInfo.get().getReservationId());
	}

	@Test
	public void testGetSchedulingInfoByMeetingsIdNull() {
		List<SchedulingInfo> schedulingInfos = subject.findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK);

		assertNotNull(schedulingInfos);
		assertEquals(2, schedulingInfos.size());
	}
}