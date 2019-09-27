package dk.medcom.video.api.service;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.UpdateMeetingDto;
import dk.medcom.video.api.repository.MeetingRepository;

public class MeetingServiceTest {
	private Calendar calendarDate = new GregorianCalendar(2018, Calendar.OCTOBER, 1,13,15, 0);
	private Calendar calendarStart = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,13,15, 0);
	private Calendar calendarStartUpdated = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,13,45, 0);
	private Calendar calendarEnd = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,14,15, 0);
	private Calendar calendarEndUpdated = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,14,45, 0);
	
	private CreateMeetingDto createMeetingDto;	
	private UpdateMeetingDto updateMeetingDto;
	private MeetingUser meetingUser;

	@Before
	public void prepareTest() {
		createMeetingDto = getCreateMeetingDtoWithDefaultValues();
		updateMeetingDto = getUpdateMeetingDtoWithDefaultValues();
		meetingUser = new MeetingUser();
		Organisation organisation = new Organisation();
		meetingUser.setOrganisation(organisation);
	}
	
	private Meeting getMeetingWithDefaultValues(MeetingUser meetingUser, String uuid) {
		Meeting meeting = new Meeting();
		meeting.setOrganizedByUser(meetingUser);	
		meeting.setId((long) 100077);
		meeting.setUuid(uuid);
		meeting.setSubject("Test møde");
		meeting.setOrganisation(meetingUser.getOrganisation());
		meeting.setMeetingUser(meetingUser);
		meeting.setCreatedTime(calendarDate.getTime());
		meeting.setUpdatedByUser(meetingUser);
		meeting.setUpdatedTime(calendarDate.getTime());
		meeting.setOrganizedByUser(meetingUser);
		meeting.setStartTime(calendarStart.getTime());
		meeting.setEndTime(calendarEnd.getTime());
		meeting.setDescription("Meeting Description long text");
		meeting.setProjectCode("P001");

		return meeting;
	}
	private UpdateMeetingDto getUpdateMeetingDtoWithDefaultValues() {
		UpdateMeetingDto meetingDto = new UpdateMeetingDto();
		meetingDto.setSubject("Test mødev2");
		meetingDto.setStartTime(calendarStartUpdated.getTime());
		meetingDto.setEndTime(calendarEndUpdated.getTime());
		meetingDto.setDescription("Meeting Description long textv2");
		meetingDto.setProjectCode("P001v2");
		meetingDto.setOrganizedByEmail("you@mail.dk");
		return meetingDto;
	}
	private CreateMeetingDto getCreateMeetingDtoWithDefaultValues() {
		CreateMeetingDto createMeetingDto = new CreateMeetingDto();
		createMeetingDto.setSubject("Test mødev2");
		createMeetingDto.setStartTime(calendarStart.getTime());
//		createMeetingDto.setStartTime(calendarStartUpdated.getTime());
//		createMeetingDto.setEndTime(calendarEndUpdated.getTime());
		createMeetingDto.setEndTime(calendarEnd.getTime());
		createMeetingDto.setDescription("Meeting Description long textv2");
		createMeetingDto.setProjectCode("P001v2");
		createMeetingDto.setOrganizedByEmail("you@mail.dk");
		return createMeetingDto;
	}
	
	private MeetingService createMeetingServiceMocked(UserContext userContext, MeetingUser meetingUser, String uuid, ProvisionStatus provisionStatus) throws PermissionDeniedException, RessourceNotFoundException {
		
		MeetingRepository meetingRepository = Mockito.mock(MeetingRepository.class);
		MeetingUserService meetingUserService = Mockito.mock(MeetingUserService.class);
		SchedulingInfoService schedulingInfoService  = Mockito.mock(SchedulingInfoService.class);
		SchedulingStatusService schedulingStatusService  = Mockito.mock(SchedulingStatusService.class);
		OrganisationService organisationService = Mockito.mock(OrganisationService.class);
		UserContextService userContextService = Mockito.mock(UserContextService.class);
		
		MeetingService meetingService = new MeetingService(meetingRepository, meetingUserService, schedulingInfoService, schedulingStatusService, organisationService, userContextService);
		Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
		
		MeetingUser meetingUserOrganizer = new MeetingUser();
		Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser()).thenReturn(meetingUser);
		Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser(Mockito.anyString())).thenReturn(meetingUserOrganizer);
		
		Mockito.when(organisationService.getUserOrganisation()).thenReturn(meetingUser.getOrganisation());
		Meeting meetingInService = getMeetingWithDefaultValues(meetingUser,  uuid);
		Mockito.when(meetingRepository.findOneByUuid(Mockito.anyString())).thenReturn(meetingInService);
		SchedulingInfo schedulingInfo = new SchedulingInfo();
		schedulingInfo.setProvisionStatus(provisionStatus);
		Mockito.when(schedulingInfoService.getSchedulingInfoByUuid(Mockito.anyString())).thenReturn(schedulingInfo);
		Mockito.when(meetingRepository.save(meetingInService)).thenAnswer(i -> i.getArguments()[0]); //returns the actual modified meeting from the updateMeething call
		
		return meetingService;
	}
	// *** Get meeting tests **********************************************************************************************************
	// *** Create meeting tests **********************************************************************************************************
	// *** Delete meeting tests **********************************************************************************************************
	// *** Update meeting tests **********************************************************************************************************
	@Test
	public void testRoleUserUpdateMeetingWithStatusAwaitsProvisionUpdatesAllValues() throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		
		// Given
		String uuid = "7cc82183-0d47-439a-a00c-38f7a5a01fce";		
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid, ProvisionStatus.AWAITS_PROVISION);
		Meeting meetingToCompare = getMeetingWithDefaultValues(meetingUser, uuid);
		
		// When
		Meeting meeting = meetingService.updateMeeting(uuid, updateMeetingDto);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(updateMeetingDto.getSubject(), meeting.getSubject());
		Assert.assertEquals(calendarStartUpdated.getTime(), meeting.getStartTime());
		Assert.assertEquals(calendarEndUpdated.getTime(), meeting.getEndTime());
		Assert.assertEquals(updateMeetingDto.getDescription(), meeting.getDescription());
		Assert.assertEquals(updateMeetingDto.getProjectCode(), meeting.getProjectCode());
		Assert.assertNotEquals(meetingToCompare.getUpdatedTime(),meeting.getUpdatedTime() );
		Assert.assertEquals(meetingUser, meeting.getOrganizedByUser());
	}
	
	@Test
	public void testRoleMeetingPlannerUpdateMeetingWithDifferentMeetingUser() throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		
		// Given
		String uuid = "7cc82183-0d47-439a-a00c-38f7a5a01fce";
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.MEETING_PLANNER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid, ProvisionStatus.AWAITS_PROVISION);
		Meeting meetingToCompare = getMeetingWithDefaultValues(meetingUser, uuid);
		
		// When
		Meeting meeting = meetingService.updateMeeting(uuid, updateMeetingDto);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(updateMeetingDto.getSubject(), meeting.getSubject());
		Assert.assertEquals(calendarStartUpdated.getTime(), meeting.getStartTime());
		Assert.assertEquals(calendarEndUpdated.getTime(), meeting.getEndTime());
		Assert.assertEquals(updateMeetingDto.getDescription(), meeting.getDescription());
		Assert.assertEquals(updateMeetingDto.getProjectCode(), meeting.getProjectCode());
		Assert.assertNotEquals(meetingToCompare.getUpdatedTime(),meeting.getUpdatedTime() );
		Assert.assertNotEquals(meetingUser, meeting.getOrganizedByUser());
	}
	
	@Test
	public void testRoleUserUpdateMeetingWithStatusProvisionedOkUpdatesEndTimeUpdatedTimeAndupdateUserOnly() throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		
		// Given
		String uuid = "7cc82183-0d47-439a-a00c-38f7a5a01fce";
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid, ProvisionStatus.PROVISIONED_OK);
		Meeting meetingToCompare = getMeetingWithDefaultValues(meetingUser, uuid);
		
		// When
		Meeting meeting = meetingService.updateMeeting(uuid, updateMeetingDto);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(meetingToCompare.getStartTime(), meeting.getStartTime());
		Assert.assertEquals(calendarEndUpdated.getTime(), meeting.getEndTime());
		Assert.assertEquals(meetingToCompare.getDescription(), meeting.getDescription());
		Assert.assertEquals(meetingToCompare.getProjectCode(), meeting.getProjectCode());
		Assert.assertNotEquals(meetingToCompare.getUpdatedTime(),meeting.getUpdatedTime() );
	}

	@Test(expected = NotAcceptableException.class)
	public void testRoleUserUpdateMeetingWithStatusProvisionedProblemsReturnsError() throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		
		// Given
		String uuid = "7cc82183-0d47-439a-a00c-38f7a5a01fce";
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid, ProvisionStatus.PROVISION_PROBLEMS);
		getMeetingWithDefaultValues(meetingUser, uuid);
		
		// When
		meetingService.updateMeeting(uuid, updateMeetingDto);
		
		// Then
		// assert Excpetion

	}
	// *** Other tests **********************************************************************************************************
	@Test
	public void testConversionFromDtoToDao() throws PermissionDeniedException, NotValidDataException, RessourceNotFoundException {
		// Given
		String uuid = "7cc82183-0d47-439a-a00c-38f7a5a01fce";		
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid, ProvisionStatus.AWAITS_PROVISION);
		
		// When
		Meeting meeting = meetingService.convert(createMeetingDto);
		
		// Then
		Assert.assertNotNull(meeting);
		Assert.assertEquals(createMeetingDto.getSubject(), meeting.getSubject());
		Assert.assertNotNull(meeting.getUuid());
//		Assert.assertEquals(meetingUser, meeting.getOrganizedByUser()); //TODO: 0 -fix test
		Assert.assertEquals(calendarStart.getTime(), meeting.getStartTime());
		Assert.assertEquals(calendarEnd.getTime(), meeting.getEndTime());
		Assert.assertEquals(createMeetingDto.getDescription(), meeting.getDescription());
		Assert.assertEquals(createMeetingDto.getProjectCode(), meeting.getProjectCode());
	}
}
