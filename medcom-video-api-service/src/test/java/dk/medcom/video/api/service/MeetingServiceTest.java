package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.*;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.MeetingType;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.UpdateMeetingDto;
import dk.medcom.video.api.repository.MeetingLabelRepository;
import dk.medcom.video.api.repository.MeetingRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;

import java.sql.SQLException;
import java.util.*;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

public class MeetingServiceTest {
	private final Calendar calendarDate = new GregorianCalendar(2018, Calendar.OCTOBER, 1, 13, 15, 0);
	private final Calendar calendarStart = new GregorianCalendar(2018, Calendar.NOVEMBER, 1, 13, 15, 0);
	private final Calendar calendarStartUpdated = new GregorianCalendar(2018, Calendar.NOVEMBER, 1, 13, 45, 0);
	private final Calendar calendarEnd = new GregorianCalendar(2018, Calendar.NOVEMBER, 1, 14, 15, 0);
	private final Calendar calendarEndUpdated = new GregorianCalendar(2018, Calendar.NOVEMBER, 1, 14, 45, 0);

	private CreateMeetingDto createMeetingDto;
	private UpdateMeetingDto updateMeetingDto;
	private MeetingUser meetingUser;

	private MeetingLabelRepository meetingLabelRepository;

	private SchedulingInfoService schedulingInfoService;
	private MeetingRepository meetingRepository;
	private UUID reservationId = UUID.randomUUID();
	private SchedulingInfo schedulingInfo;

	@Before
	public void prepareTest() {
		createMeetingDto = getCreateMeetingDtoWithDefaultValues();
		updateMeetingDto = getUpdateMeetingDtoWithDefaultValues();
		meetingUser = new MeetingUser();
		Organisation organisation = new Organisation();
		organisation.setOrganisationId("RH");
		organisation.setName("some name");
		organisation.setId(1234L);
		organisation.setPoolSize(10);

		meetingUser.setOrganisation(organisation);
		meetingUser.setEmail("test@test.dk");

		meetingLabelRepository = Mockito.mock(MeetingLabelRepository.class);
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

	private MeetingService createMeetingServiceMocked(UserContext userContext, MeetingUser meetingUser, String meetingUuid, ProvisionStatus provisionStatus) throws PermissionDeniedException, RessourceNotFoundException {
		return createMeetingServiceMocked(userContext, meetingUser, meetingUuid, provisionStatus, meetingLabelRepository, null);
	}

	private MeetingService createMeetingServiceMocked(UserContext userContext, MeetingUser meetingUser, String meetingUuid, ProvisionStatus provisionStatus, Integer userOrganisationPoolSize) throws PermissionDeniedException, RessourceNotFoundException {
		return createMeetingServiceMocked(userContext, meetingUser, meetingUuid, provisionStatus, meetingLabelRepository, userOrganisationPoolSize);
	}

	private MeetingService createMeetingServiceMocked(UserContext userContext, MeetingUser meetingUser, String meetingUuid, ProvisionStatus provisionStatus, MeetingLabelRepository meetingLabelRepository, Integer userOrganistionPoolSize) throws PermissionDeniedException, RessourceNotFoundException {

		meetingRepository = Mockito.mock(MeetingRepository.class);
		MeetingUserService meetingUserService = Mockito.mock(MeetingUserService.class);
		schedulingInfoService = Mockito.mock(SchedulingInfoService.class);
		SchedulingStatusService schedulingStatusService = Mockito.mock(SchedulingStatusService.class);
		OrganisationService organisationService = Mockito.mock(OrganisationService.class);
		UserContextService userContextService = Mockito.mock(UserContextService.class);

		MeetingService meetingService = new MeetingService(meetingRepository, meetingUserService, schedulingInfoService, schedulingStatusService, organisationService, userContextService, meetingLabelRepository);
		Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

		MeetingUser meetingUserOrganizer = new MeetingUser();
		meetingUserOrganizer.setEmail("some@email.com");

		Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser()).thenReturn(meetingUser);
		Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser(Mockito.anyString())).thenReturn(meetingUserOrganizer);

		Mockito.when(organisationService.getUserOrganisation()).thenReturn(meetingUser.getOrganisation());
		Mockito.when(organisationService.getPoolSizeForOrganisation("org")).thenReturn(null);
		Mockito.when(organisationService.getPoolSizeForUserOrganisation()).thenReturn(userOrganistionPoolSize);

		Meeting meetingInService = getMeetingWithDefaultValues(meetingUser, meetingUuid);
		Mockito.when(meetingRepository.findOneByUuid(Mockito.anyString())).thenReturn(meetingInService);
		schedulingInfo = new SchedulingInfo();
		schedulingInfo.setId(100L);
		schedulingInfo.setProvisionStatus(provisionStatus);
		Organisation organisation = new Organisation();
		organisation.setOrganisationId("org");
		schedulingInfo.setOrganisation(organisation);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(new SchedulingInfo());
		Mockito.when(schedulingInfoService.getSchedulingInfoByUuid(Mockito.anyString())).thenReturn(schedulingInfo);
		Mockito.when(schedulingInfoService.getUnusedSchedulingInfoForOrganisation(meetingUser.getOrganisation())).thenReturn(schedulingInfo.getId());
		Mockito.when(schedulingInfoService.getSchedulingInfoByReservation(reservationId)).thenReturn(schedulingInfo);
		Mockito.when(meetingRepository.save(meetingInService)).thenAnswer(i -> i.getArguments()[0]); //returns the actual modified meeting from the updateMeething call
		Mockito.when(meetingRepository.save((Meeting) Mockito.argThat(x -> ((Meeting) x).getUuid().equals(meetingUuid)))).thenAnswer(i -> {
			Meeting meeting = (Meeting) i.getArguments()[0];
			meeting.setId(57483L);
			return meeting;
		});

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
		assertEquals(updateMeetingDto.getSubject(), meeting.getSubject());
		assertEquals(calendarStartUpdated.getTime(), meeting.getStartTime());
		assertEquals(calendarEndUpdated.getTime(), meeting.getEndTime());
		assertEquals(updateMeetingDto.getDescription(), meeting.getDescription());
		assertEquals(updateMeetingDto.getProjectCode(), meeting.getProjectCode());
		Assert.assertNotEquals(meetingToCompare.getUpdatedTime(), meeting.getUpdatedTime());
		assertEquals(meetingUser, meeting.getOrganizedByUser());
	}

	@Test
	public void testUpdateMeetingUpdatesLabels() throws NotValidDataException, PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		// Given
		String uuid = "7cc82183-0d47-439a-a00c-38f7a5a01fce";
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid, ProvisionStatus.AWAITS_PROVISION);

		// When
		updateMeetingDto.setLabels(Arrays.asList("first label", "second label"));
		meetingService.updateMeeting(uuid, updateMeetingDto);

		// Then
		Mockito.verify(meetingLabelRepository, times(1)).deleteByMeeting(Mockito.any(Meeting.class));
		ArgumentCaptor<Iterable<MeetingLabel>> labelsCaptor = ArgumentCaptor.forClass(Iterable.class);
		Mockito.verify(meetingLabelRepository, times(1)).saveAll(labelsCaptor.capture());

		List<MeetingLabel> labels = new ArrayList<>();
		labelsCaptor.getValue().forEach(labels::add);
		assertEquals(2, labels.size());
		Optional<MeetingLabel> firstLabel = labels.stream().filter(x -> x.getLabel().equals("first label")).findFirst();
		assertTrue(firstLabel.isPresent());
		assertEquals("first label", firstLabel.get().getLabel());

		Optional<MeetingLabel> secondLabel = labels.stream().filter(x -> x.getLabel().equals("second label")).findFirst();
		assertTrue(secondLabel.isPresent());
		assertEquals("second label", secondLabel.get().getLabel());
	}

	@Test
	public void deleteMeetingDeletesLabels() throws PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		// Given
		String uuid = "7cc82183-0d47-439a-a00c-38f7a5a01fce";
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid, ProvisionStatus.AWAITS_PROVISION);

		// When
		meetingService.deleteMeeting(uuid);

		// Then
		Mockito.verify(meetingLabelRepository, times(1)).deleteByMeeting(Mockito.any(Meeting.class));
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
		assertEquals(updateMeetingDto.getSubject(), meeting.getSubject());
		assertEquals(calendarStartUpdated.getTime(), meeting.getStartTime());
		assertEquals(calendarEndUpdated.getTime(), meeting.getEndTime());
		assertEquals(updateMeetingDto.getDescription(), meeting.getDescription());
		assertEquals(updateMeetingDto.getProjectCode(), meeting.getProjectCode());
		Assert.assertNotEquals(meetingToCompare.getUpdatedTime(), meeting.getUpdatedTime());
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
		assertEquals(meetingToCompare.getStartTime(), meeting.getStartTime());
		assertEquals(calendarEndUpdated.getTime(), meeting.getEndTime());
		assertEquals(meetingToCompare.getDescription(), meeting.getDescription());
		assertEquals(meetingToCompare.getProjectCode(), meeting.getProjectCode());
		Assert.assertNotEquals(meetingToCompare.getUpdatedTime(), meeting.getUpdatedTime());
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
		assertEquals(createMeetingDto.getSubject(), meeting.getSubject());
		Assert.assertNotNull(meeting.getUuid());
//		Assert.assertEquals(meetingUser, meeting.getOrganizedByUser()); //TODO: 0 -fix test
		assertEquals(calendarStart.getTime(), meeting.getStartTime());
		assertEquals(calendarEnd.getTime(), meeting.getEndTime());
		assertEquals(createMeetingDto.getDescription(), meeting.getDescription());
		assertEquals(createMeetingDto.getProjectCode(), meeting.getProjectCode());
	}

	@Test
	public void testCreatePooledMeeting() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);
		input.setMeetingType(MeetingType.POOL);

		// Stuff
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK, 10);
		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());

		Mockito.verify(schedulingInfoService, times(0)).createSchedulingInfo(Mockito.any(), Mockito.any());
		Mockito.verify(schedulingInfoService, times(1)).attachMeetingToSchedulingInfo(result);
	}

	@Test
	public void testCreateMeetingSchedulingInfoReserved() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);
		input.setSchedulingInfoReservationId(reservationId);
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK, 10);
		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());

		Mockito.verify(schedulingInfoService, times(1)).attachMeetingToSchedulingInfo(result, schedulingInfo);
	}

	@Test(expected = NotValidDataException.class)
	public void testCreateMeetingSchedulingInfoReservedNotFound() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);
		input.setSchedulingInfoReservationId(UUID.randomUUID());
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK, 10);
		Mockito.reset(schedulingInfoService);
		Mockito.when(schedulingInfoService.getSchedulingInfoByReservation(Mockito.any())).thenThrow(new RessourceNotFoundException("NOT FOUND", "reservationID"));

		meetingService.createMeeting(input);
	}

	@Test(expected = NotValidDataException.class)
	public void testCreateMeetingSchedulingInfoReservedOtherOrganisation() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);
		input.setSchedulingInfoReservationId(reservationId);
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK, 10);
		schedulingInfo.getOrganisation().setOrganisationId("some_other_org");

		meetingService.createMeeting(input);
	}

	@Test
	public void testCreatePooledMeetingFutureNewSchedulingInfo() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, (24*60)+1);
		input.setStartTime(now.getTime());
		input.setUuid(uuid);
		input.setMeetingType(MeetingType.POOL);

		// Stuff
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK, 10);
		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());

		Mockito.verify(schedulingInfoService, times(1)).createSchedulingInfo(Mockito.any(), Mockito.any());
		Mockito.verify(schedulingInfoService, times(0)).attachMeetingToSchedulingInfo(result);
	}

	@Test(expected = NotValidDataException.class)
	public void testCanNotCreateMeetingWhenNoScheduleAvailable() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);
		input.setMeetingType(MeetingType.POOL);

		// Stuff
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		meetingService.createMeeting(input);
	}

	@Test
	public void testCreateMeeting() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		MeetingLabelRepository meetingLabelRepository = Mockito.mock(MeetingLabelRepository.class);
		MeetingLabel firstLabel = new MeetingLabel();
		firstLabel.setId(1L);
		firstLabel.setLabel("first label");

		MeetingLabel secondLabel = new MeetingLabel();
		secondLabel.setId(2L);
		secondLabel.setLabel("second label");

		List<MeetingLabel> labels = Arrays.asList(firstLabel, secondLabel);
		Mockito.when(meetingLabelRepository.saveAll(Mockito.anyList())).thenReturn(labels);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);
		input.setEndTime(new Date());
		input.setLabels(Arrays.asList("first label", "second label"));

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK, meetingLabelRepository, null);
		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());

		Mockito.verify(schedulingInfoService, times(0)).createSchedulingInfo(Mockito.any(), Mockito.any());
		Mockito.verify(schedulingInfoService, times(1)).attachMeetingToSchedulingInfo(result);

		ArgumentCaptor<Set<MeetingLabel>> labelsCaptor = ArgumentCaptor.forClass(Set.class);
		Mockito.verify(meetingLabelRepository, times(1)).saveAll(labelsCaptor.capture());
		Set<MeetingLabel> savedLabels = labelsCaptor.getValue();
		assertEquals(2, savedLabels.size());

		Optional<MeetingLabel> firstSavedLabel = savedLabels.stream().filter(x -> x.getLabel().equals("first label")).findFirst();
		assertTrue(firstSavedLabel.isPresent());
		assertEquals(firstLabel.getLabel(), firstSavedLabel.get().getLabel());

		Optional<MeetingLabel> secondSavedLabel = savedLabels.stream().filter(x -> x.getLabel().equals("second label")).findFirst();
		assertTrue(secondSavedLabel.isPresent());
		assertEquals(secondLabel.getLabel(), secondSavedLabel.get().getLabel());

		Mockito.verifyNoMoreInteractions(schedulingInfoService);

		var meetingCaptor = ArgumentCaptor.forClass(Meeting.class);
		Mockito.verify(meetingRepository).save(meetingCaptor.capture());
		var savedMeeting = meetingCaptor.getValue();
		assertNotNull(savedMeeting);
		assertNotNull(savedMeeting.getShortId());
		assertEquals("This is a description", savedMeeting.getDescription());
	}

	@Test
	public void testCreateMeetingFuture() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		MeetingLabelRepository meetingLabelRepository = Mockito.mock(MeetingLabelRepository.class);
		MeetingLabel firstLabel = new MeetingLabel();
		firstLabel.setId(1L);
		firstLabel.setLabel("first label");

		MeetingLabel secondLabel = new MeetingLabel();
		secondLabel.setId(2L);
		secondLabel.setLabel("second label");

		List<MeetingLabel> labels = Arrays.asList(firstLabel, secondLabel);
		Mockito.when(meetingLabelRepository.saveAll(Mockito.anyList())).thenReturn(labels);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, (24*60)+1);
		input.setStartTime(now.getTime());
		input.setUuid(uuid);
		input.setEndTime(new Date());
		input.setLabels(Arrays.asList("first label", "second label"));

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK, meetingLabelRepository, null);
		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());

		Mockito.verify(schedulingInfoService, times(1)).createSchedulingInfo(Mockito.any(), Mockito.any());
		Mockito.verify(schedulingInfoService, times(0)).attachMeetingToSchedulingInfo(result);

		ArgumentCaptor<Set<MeetingLabel>> labelsCaptor = ArgumentCaptor.forClass(Set.class);
		Mockito.verify(meetingLabelRepository, times(1)).saveAll(labelsCaptor.capture());
		Set<MeetingLabel> savedLabels = labelsCaptor.getValue();
		assertEquals(2, savedLabels.size());

		Optional<MeetingLabel> firstSavedLabel = savedLabels.stream().filter(x -> x.getLabel().equals("first label")).findFirst();
		assertTrue(firstSavedLabel.isPresent());
		assertEquals(firstLabel.getLabel(), firstSavedLabel.get().getLabel());

		Optional<MeetingLabel> secondSavedLabel = savedLabels.stream().filter(x -> x.getLabel().equals("second label")).findFirst();
		assertTrue(secondSavedLabel.isPresent());
		assertEquals(secondLabel.getLabel(), secondSavedLabel.get().getLabel());

		Mockito.verifyNoMoreInteractions(schedulingInfoService);
	}

	@Test(expected = NotValidDataException.class)
	public void testCanNotCreateAdhocMeetingOnNonAdhocOrganisation() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		meetingUser.getOrganisation().setPoolSize(null);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);
		input.setMeetingType(MeetingType.POOL);

		// Stuff
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		meetingService.createMeeting(input);
	}

	@Test
	public void testHandleDuplicateShortId() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);

		// Stuff
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);

		Mockito.reset(meetingRepository);
		ConstraintViolationException constraintException = new ConstraintViolationException("Duplicate short_id", new SQLException(), "short_id");
		DuplicateKeyException duplicateKeyException = new DuplicateKeyException("Duplicate Key", constraintException);

		Mockito.when(meetingRepository.save(Mockito.argThat(x -> x.getUuid().equals(uuid.toString()))))
				.thenThrow(duplicateKeyException)
				.thenAnswer(i -> {
			Meeting meeting = (Meeting) i.getArguments()[0];
			meeting.setId(57483L);
			return meeting;
		});

		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());
		assertEquals(12, result.getShortId().length());
		Mockito.verify(schedulingInfoService, times(0)).createSchedulingInfo(Mockito.any(), Mockito.any());
		Mockito.verify(schedulingInfoService, times(1)).attachMeetingToSchedulingInfo(result);
	}

	@Test(expected = NotValidDataException.class)
	public void testFailureOnManyDuplicateShortId() throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);

		// Stuff
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);

		Mockito.reset(meetingRepository);
		ConstraintViolationException constraintException = new ConstraintViolationException("Duplicate short_id", new SQLException(), "short_id");
		DuplicateKeyException duplicateKeyException = new DuplicateKeyException("Duplicate Key", constraintException);

		Mockito.when(meetingRepository.save(Mockito.argThat(x -> x.getUuid().equals(uuid.toString()))))
				.thenThrow(duplicateKeyException)
				.thenThrow(duplicateKeyException)
				.thenThrow(duplicateKeyException)
				.thenThrow(duplicateKeyException)
				.thenThrow(duplicateKeyException)
//				.thenAnswer(i -> {
//					Meeting meeting = (Meeting) i.getArguments()[0];
//					meeting.setId(57483L);
//					return meeting;
//				});
		;

		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());

		Mockito.verify(schedulingInfoService, times(0)).createSchedulingInfo(Mockito.any(), Mockito.any());
		Mockito.verify(schedulingInfoService, times(1)).attachMeetingToSchedulingInfo(result);
	}

	@Test
	public void testGetMeetingByOrganizedByOtherUser() throws RessourceNotFoundException, PermissionDeniedException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		List<Meeting> result = meetingService.getMeetingsByOrganizedBy("john@invalid.com");
		assertEquals(0, result.size());

		Mockito.verifyZeroInteractions(meetingRepository);
	}

	@Test
	public void testGetMeetingByOrganizedBySameUser() throws PermissionDeniedException, RessourceNotFoundException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		List<Meeting> result = meetingService.getMeetingsByOrganizedBy("test@test.dk");
		assertEquals(0, result.size());

		Mockito.verify(meetingRepository).findByOrganizedBy(Mockito.any(MeetingUser.class));
	}

	@Test
	public void testGetMeetingByOrganizedAdmin() throws RessourceNotFoundException, PermissionDeniedException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		meetingService.getMeetingsByOrganizedBy("test@test.dk");

		Mockito.verify(meetingRepository).findByOrganisationAndOrganizedBy(Mockito.any(Organisation.class), Mockito.any(MeetingUser.class));
	}

	@Test
	public void testGetMeetingByUriUser() throws RessourceNotFoundException, PermissionDeniedException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		meetingService.getMeetingsByUriWithDomain("uriWithDomain");

		Mockito.verify(meetingRepository).findByUriWithDomainAndOrganizedBy(Mockito.any(MeetingUser.class), Mockito.anyString());
	}

	@Test
	public void testGetMeetingByUriAdmin() throws RessourceNotFoundException, PermissionDeniedException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		meetingService.getMeetingsByUriWithDomain("uriWithDomain");

		Mockito.verify(meetingRepository).findByUriWithDomainAndOrganisation(Mockito.any(Organisation.class), Mockito.anyString());
	}


	@Test
	public void testGetMeetingByLabelUser() throws RessourceNotFoundException, PermissionDeniedException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.USER);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		meetingService.getMeetingsByLabel("uriWithDomain");

		Mockito.verify(meetingRepository).findByLabelAndOrganizedBy(Mockito.any(MeetingUser.class), Mockito.eq("uriWithDomain"));
	}

	@Test
	public void testGetMeetingByLabelAdmin() throws RessourceNotFoundException, PermissionDeniedException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		meetingService.getMeetingsByLabel("uriWithDomain");

		Mockito.verify(meetingRepository).findByLabelAndOrganisation(Mockito.any(Organisation.class), Mockito.eq("uriWithDomain"));
	}

	@Test
	public void testCreateMeetingFromPooledOrganization() throws NotValidDataException, PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);

		// Stuff
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());

		Mockito.verify(schedulingInfoService, times(0)).createSchedulingInfo(Mockito.any(), Mockito.any());
		Mockito.verify(schedulingInfoService, times(1)).attachMeetingToSchedulingInfo(result);
	}

	@Test
	public void testCreateMeetingFromPooledOrganizationNoFreePool() throws NotValidDataException, PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);

		// Stuff
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);
		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());

		Mockito.verify(schedulingInfoService, times(1)).createSchedulingInfo(Mockito.any(), Mockito.any());
		Mockito.verify(schedulingInfoService, times(1)).attachMeetingToSchedulingInfo(result);
	}

	@Test
	public void testCreateMeetingFromNonPooledOrganization() throws NotValidDataException, PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		meetingUser = new MeetingUser();
		Organisation organisation = new Organisation();
		organisation.setOrganisationId("RH");
		organisation.setName("some name");
		organisation.setId(1234L);
//		organisation.setPoolSize(10);

		meetingUser.setOrganisation(organisation);
		meetingUser.setEmail("test@test.dk");

		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		CreateMeetingDto input = new CreateMeetingDto();
		input.setDescription("This is a description");
		input.setOrganizedByEmail("some@email.com");
		input.setStartTime(new Date());
		input.setUuid(uuid);

		// Stuff
		input.setEndTime(new Date());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);

		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		Meeting result = meetingService.createMeeting(input);
		assertNotNull(result);
		assertEquals(uuid.toString(), result.getUuid());
		assertEquals(input.getDescription(), result.getDescription());
		assertEquals(input.getOrganizedByEmail(), result.getOrganizedByUser().getEmail());
		assertEquals(input.getStartTime(), result.getStartTime());

		Mockito.verify(schedulingInfoService, times(1)).createSchedulingInfo(Mockito.any(), Mockito.any());
		Mockito.verify(schedulingInfoService, times(1)).attachMeetingToSchedulingInfo(result);
	}

	@Test
	public void testGenericSearchAdmin() throws RessourceNotFoundException, PermissionDeniedException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		Calendar oneWeekAgo = Calendar.getInstance();
		oneWeekAgo.add(Calendar.DATE, -7);

		Calendar now = Calendar.getInstance();

		Meeting meetingOne = createMeeting(1L, now.getTime());
		Meeting meetingOneDuplicate = createMeeting(1L, now.getTime());
		Meeting meetingTwo = createMeeting(2L, now.getTime());
		Meeting meetingPastExcluded = createMeeting(5L, oneWeekAgo.getTime());
		Meeting meetingThree = createMeeting(3L, now.getTime());
		Meeting meetingFour = createMeeting(4L, now.getTime());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		Mockito.when(meetingRepository.findByOrganisationAndOrganizedBy(Mockito.any(), Mockito.any())).thenReturn(wrapInList(meetingOne));
		Mockito.when(meetingRepository.findByLabelAndOrganisation(Mockito.any(), Mockito.any())).thenReturn(wrapInList(meetingTwo));
		Mockito.when(meetingRepository.findByOrganisationAndSubjectLikeOrDescriptionLike(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(wrapInList(meetingOneDuplicate, meetingThree));
		Mockito.when(meetingRepository.findByUriWithDomainAndOrganisation(Mockito.any(), Mockito.any())).thenReturn(wrapInList(meetingFour, meetingPastExcluded));

		Calendar yesterdayCal = Calendar.getInstance();
		yesterdayCal.add(Calendar.DATE, -1);
		Date yesterday = yesterdayCal.getTime();
		Calendar tomorrowCal = Calendar.getInstance();
		tomorrowCal.add(Calendar.DATE, 1);
		Date tomorrow = tomorrowCal.getTime();
		List<Meeting> result = meetingService.searchMeetings("search", yesterday, tomorrow);

		assertEquals(4, result.size());
		assertEquals(1, result.get(0).getId().longValue());
		assertEquals(2, result.get(1).getId().longValue());
		assertEquals(3, result.get(2).getId().longValue());
		assertEquals(4, result.get(3).getId().longValue());
	}

	@Test
	public void testGenericSearchAdminWithoutDates() throws RessourceNotFoundException, PermissionDeniedException {
		UUID uuid = UUID.randomUUID();
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		Calendar oneWeekAgo = Calendar.getInstance();
		oneWeekAgo.add(Calendar.DATE, -7);

		Calendar now = Calendar.getInstance();

		Meeting meetingOne = createMeeting(1L, now.getTime());
		Meeting meetingOneDuplicate = createMeeting(1L, now.getTime());
		Meeting meetingTwo = createMeeting(2L, now.getTime());
		Meeting meetingPast = createMeeting(5L, oneWeekAgo.getTime());
		Meeting meetingThree = createMeeting(3L, now.getTime());
		Meeting meetingFour = createMeeting(4L, now.getTime());

		MeetingService meetingService = createMeetingServiceMocked(userContext, meetingUser, uuid.toString(), ProvisionStatus.PROVISIONED_OK);
		Mockito.when(schedulingInfoService.attachMeetingToSchedulingInfo(Mockito.any(Meeting.class))).thenReturn(null);

		Mockito.when(meetingRepository.findByOrganisationAndOrganizedBy(Mockito.any(), Mockito.any())).thenReturn(wrapInList(meetingOne));
		Mockito.when(meetingRepository.findByLabelAndOrganisation(Mockito.any(), Mockito.any())).thenReturn(wrapInList(meetingTwo));
		Mockito.when(meetingRepository.findByOrganisationAndSubjectLikeOrDescriptionLike(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(wrapInList(meetingOneDuplicate, meetingThree));
		Mockito.when(meetingRepository.findByUriWithDomainAndOrganisation(Mockito.any(), Mockito.any())).thenReturn(wrapInList(meetingFour, meetingPast));

		List<Meeting> result = meetingService.searchMeetings("search", null, null);

		assertEquals(5, result.size());
		assertEquals(1, result.get(0).getId().longValue());
		assertEquals(2, result.get(1).getId().longValue());
		assertEquals(3, result.get(2).getId().longValue());
		assertEquals(4, result.get(3).getId().longValue());
		assertEquals(5, result.get(4).getId().longValue());
	}

	private List<Meeting> wrapInList(Meeting... meeting) {
		ArrayList<Meeting> list = new ArrayList<>();
		list.addAll(Arrays.asList(meeting));

		return list;
	}

	private Meeting createMeeting(Long id, Date startDate) {
		Meeting meeting = new Meeting();
		meeting.setId(id);
		meeting.setStartTime(startDate);

		return meeting;
	}
}