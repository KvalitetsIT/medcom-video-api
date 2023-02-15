package dk.medcom.video.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.organisation.OrganisationTree;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import dk.medcom.video.api.service.OrganisationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.api.CreateSchedulingTemplateDto;
import dk.medcom.video.api.api.UpdateSchedulingTemplateDto;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

public class SchedulingTemplateServiceTest {
	private CreateSchedulingTemplateDto createSchedulingTemplateDto;
	private UpdateSchedulingTemplateDto updateSchedulingTemplateDto;
	private MeetingUser meetingUser;
	private Organisation organisation;
	private OrganisationTreeServiceClient organisationTreeServiceClient;
	private SchedulingTemplateRepository schedulingTemplateRepository;

	@Before
	public void prepareTest() {
		organisationTreeServiceClient = Mockito.mock(OrganisationTreeServiceClient.class);
		createSchedulingTemplateDto = getCreateSchedulingTemplateDtoWithDefaultValues();
		updateSchedulingTemplateDto = getUpdateSchedulingTemplateDtoWithDefaultValues();
		meetingUser = new MeetingUser();
		organisation = new Organisation();
		organisation.setOrganisationId("org");
		meetingUser.setOrganisation(organisation);
	}

	@Test 
	public void testCreateSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateServiceImpl schedulingTemplateService = schedulingTemplateServiceMocked(userContext, meetingUser, true);
		createSchedulingTemplateDto.setIsPoolTemplate(true);

		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.createSchedulingTemplate(createSchedulingTemplateDto, true);
		
		// Then
		assertNotNull(schedulingTemplate);

		assertEquals(createSchedulingTemplateDto.getCustomPortalGuest(), schedulingTemplate.getCustomPortalGuest());

		ArgumentCaptor<SchedulingTemplate> schedulingTemplateArgumentCaptor = ArgumentCaptor.forClass(SchedulingTemplate.class);
		Mockito.verify(schedulingTemplateRepository).save(schedulingTemplateArgumentCaptor.capture());
		assertNotNull(schedulingTemplateArgumentCaptor.getValue());
		assertEquals(createSchedulingTemplateDto.getCustomPortalGuest(), schedulingTemplateArgumentCaptor.getValue().getCustomPortalGuest());
		assertEquals(createSchedulingTemplateDto.getCustomPortalHost(), schedulingTemplateArgumentCaptor.getValue().getCustomPortalHost());
		assertEquals(createSchedulingTemplateDto.getReturnUrl(), schedulingTemplateArgumentCaptor.getValue().getReturnUrl());
		assertEquals(createSchedulingTemplateDto.getIsPoolTemplate(), schedulingTemplateArgumentCaptor.getValue().getIsPoolTemplate());
	}
	
	@Test 
	public void testUpdateSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateServiceImpl schedulingTemplateService = schedulingTemplateServiceMocked(userContext, meetingUser, false);
		updateSchedulingTemplateDto.setGuestPinRequired(false);
		updateSchedulingTemplateDto.setCustomPortalGuest("some_portal_guest");
		updateSchedulingTemplateDto.setCustomPortalHost("some_portal_host");
		updateSchedulingTemplateDto.setReturnUrl("return_url");
		updateSchedulingTemplateDto.setIsPoolTemplate(true);
		
		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.updateSchedulingTemplate(1L, updateSchedulingTemplateDto);
		
		// Then
		assertNotNull(schedulingTemplate);
		Assert.assertFalse(schedulingTemplate.getGuestPinRequired());

		ArgumentCaptor<SchedulingTemplate> schedulingTemplateArgumentCaptor = ArgumentCaptor.forClass(SchedulingTemplate.class);
		Mockito.verify(schedulingTemplateRepository).save(schedulingTemplateArgumentCaptor.capture());
		assertNotNull(schedulingTemplateArgumentCaptor.getValue());
		assertEquals(updateSchedulingTemplateDto.getCustomPortalGuest(), schedulingTemplateArgumentCaptor.getValue().getCustomPortalGuest());
		assertEquals(updateSchedulingTemplateDto.getCustomPortalHost(), schedulingTemplateArgumentCaptor.getValue().getCustomPortalHost());
		assertEquals(updateSchedulingTemplateDto.getReturnUrl(), schedulingTemplateArgumentCaptor.getValue().getReturnUrl());
		assertEquals(updateSchedulingTemplateDto.getIsPoolTemplate(), schedulingTemplateArgumentCaptor.getValue().getIsPoolTemplate());
	}

	@Test(expected = RessourceNotFoundException.class)
	public void testDeleteSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException  {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateServiceImpl schedulingTemplateService = schedulingTemplateServiceMocked(userContext, meetingUser, false);
		updateSchedulingTemplateDto.setGuestPinRequired(false);
		
		// When
		schedulingTemplateService.deleteSchedulingTemplate(1L);
		
		// Then
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(777L); //cheating, to retrieve null
	}

	@Test 
	public void getGetSchedulingTemplateInOrganisationTree() throws PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		SchedulingTemplateServiceImpl schedulingTemplateService = simpleSchedulingTemplateServiceMocked(userContext, meetingUser, schedulingTemplateRepository);
		
		List<SchedulingTemplate> schedulingTemplatesInService = new ArrayList();
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  1L));
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  2L));
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  3L));
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(true))).thenReturn(schedulingTemplatesInService);
		
		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateInOrganisationTree();
		
		// Then
		assertNotNull(schedulingTemplate);
		assertNotNull(schedulingTemplate.getOrganisation());
		assertEquals(schedulingTemplatesInService.get(0).getCustomPortalGuest(), schedulingTemplate.getCustomPortalGuest());
		assertEquals(schedulingTemplatesInService.get(0).getCustomPortalHost(), schedulingTemplate.getCustomPortalHost());
		assertEquals(schedulingTemplatesInService.get(0).getReturnUrl(), schedulingTemplate.getReturnUrl());
		assertEquals(schedulingTemplatesInService.get(0).getIsPoolTemplate(), schedulingTemplate.getIsPoolTemplate());
		Mockito.verify(organisationTreeServiceClient, times(0)).getOrganisationTree("org");

	}

	@Test
	public void getGetSchedulingTemplateInOrganisationTreeOtherOrganisation() throws PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		SchedulingTemplateServiceImpl schedulingTemplateService = simpleSchedulingTemplateServiceMocked(userContext, meetingUser, schedulingTemplateRepository);

		var inputSchedulingTemplate = getSchedulingTemplateWithDefaultValues(organisation,  1L);
		inputSchedulingTemplate.setIsDefaultTemplate(true);
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(true))).thenReturn(Collections.emptyList());
		Mockito.when(schedulingTemplateRepository.findByOrganisationIdAndIsDefaultTemplateAndDeletedTimeIsNull("child")).thenReturn(null);
		Mockito.when(schedulingTemplateRepository.findByOrganisationIdAndIsDefaultTemplateAndDeletedTimeIsNull("childOne")).thenReturn(null);
		Mockito.when(schedulingTemplateRepository.findByOrganisationIdAndIsDefaultTemplateAndDeletedTimeIsNull("parent")).thenReturn(Collections.singletonList(inputSchedulingTemplate));
		var child = createOrganisationTree("org", "org-name", null);
		var childOne = createOrganisationTree("childOne", "childOne-name", Collections.singletonList(child));
		var parent = createOrganisationTree("parent", "parent-name", Collections.singletonList(childOne));
		var superParent = createOrganisationTree("superParent", "superParent-name", Collections.singletonList(parent));
		Mockito.when(organisationTreeServiceClient.getOrganisationTree("org")).thenReturn(superParent);

		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateInOrganisationTree();

		// Then
		assertNotNull(schedulingTemplate);
		assertNotNull(schedulingTemplate.getOrganisation());
		assertEquals(inputSchedulingTemplate.getCustomPortalGuest(), schedulingTemplate.getCustomPortalGuest());
		assertEquals(inputSchedulingTemplate.getCustomPortalHost(), schedulingTemplate.getCustomPortalHost());
		assertEquals(inputSchedulingTemplate.getReturnUrl(), schedulingTemplate.getReturnUrl());
		assertEquals(inputSchedulingTemplate.getIsPoolTemplate(), schedulingTemplate.getIsPoolTemplate());
		Mockito.verify(organisationTreeServiceClient, times(1)).getOrganisationTree("org");
	}
	
	@Test 
	public void testGetSharedSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		SchedulingTemplateServiceImpl schedulingTemplateService = simpleSchedulingTemplateServiceMocked(userContext, meetingUser, schedulingTemplateRepository);
		
		List<SchedulingTemplate> schedulingTemplatesInServiceEmpty = new ArrayList<>();
		List<SchedulingTemplate> schedulingTemplatesInService = new ArrayList<>();

		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(null,  1L));
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(true))).thenReturn(schedulingTemplatesInServiceEmpty);
		Mockito.when(schedulingTemplateRepository.findByOrganisationIsNullAndDeletedTimeIsNull()).thenReturn(schedulingTemplatesInService);
		Mockito.when(organisationTreeServiceClient.getOrganisationTree("org")).thenReturn(createOrganisationTree("org", "org-name", null));

		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateInOrganisationTree();
		
		// Then
		assertNotNull(schedulingTemplate);
		Assert.assertNull(schedulingTemplate.getOrganisation());
		assertEquals(schedulingTemplatesInService.get(0).getCustomPortalGuest(), schedulingTemplate.getCustomPortalGuest());
		assertEquals(schedulingTemplatesInService.get(0).getCustomPortalHost(), schedulingTemplate.getCustomPortalHost());
		assertEquals(schedulingTemplatesInService.get(0).getReturnUrl(), schedulingTemplate.getReturnUrl());
		assertEquals(schedulingTemplatesInService.get(0).getIsPoolTemplate(), schedulingTemplate.getIsPoolTemplate());
		Mockito.verify(organisationTreeServiceClient, times(1)).getOrganisationTree("org");
	}

	private OrganisationTree createOrganisationTree(String code, String name, List<OrganisationTree> children) {
		var organisationTree = new OrganisationTree();
		organisationTree.setName(name);
		organisationTree.setCode(code);
		organisationTree.setPoolSize(0);
		if(children != null) {
			organisationTree.getChildren().addAll(children);

		}

		return organisationTree;
	}
	
	@Test 
	public void testGetSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException  {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		SchedulingTemplateServiceImpl schedulingTemplateService = simpleSchedulingTemplateServiceMocked(userContext, meetingUser, schedulingTemplateRepository);
		
		SchedulingTemplate schedulingTemplateInService = getSchedulingTemplateWithDefaultValues(organisation,  1L);
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIdAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(1L))).thenReturn(schedulingTemplateInService);

		schedulingTemplateInService.setIsPoolTemplate(true);
		
		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(1L);

		// Then
		assertNotNull(schedulingTemplate);
		assertEquals(schedulingTemplateInService.getCustomPortalGuest(), schedulingTemplate.getCustomPortalGuest());
		assertEquals(schedulingTemplateInService.getCustomPortalHost(), schedulingTemplate.getCustomPortalHost());
		assertEquals(schedulingTemplateInService.getReturnUrl(), schedulingTemplate.getReturnUrl());
		assertEquals(schedulingTemplateInService.getIsPoolTemplate(), schedulingTemplate.getIsPoolTemplate());
	}
	
	@Test  
	public void testGetSchedulingTemplates() throws PermissionDeniedException, RessourceNotFoundException {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		SchedulingTemplateServiceImpl schedulingTemplateService = simpleSchedulingTemplateServiceMocked(userContext, meetingUser, schedulingTemplateRepository);
				
		List<SchedulingTemplate> schedulingTemplatesInService = new ArrayList<>();
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  1L));
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  2L));
		var template = getSchedulingTemplateWithDefaultValues(organisation, 3L);
		template.setIsPoolTemplate(true);
		schedulingTemplatesInService.add(template);
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndDeletedTimeIsNull(Mockito.any(Organisation.class))).thenReturn(schedulingTemplatesInService);
		
		// When
		List<SchedulingTemplate> schedulingTemplates = schedulingTemplateService.getSchedulingTemplates();
		
		// Then
		assertNotNull(schedulingTemplates);
		assertEquals(3, schedulingTemplates.size());
		assertFalse(schedulingTemplates.get(0).getIsPoolTemplate());
		assertFalse(schedulingTemplates.get(1).getIsPoolTemplate());
		assertTrue(schedulingTemplates.get(2).getIsPoolTemplate());
 
	}

	@Test
	public void testCreateSchedulingTemplateOnlyOnePool() throws RessourceNotFoundException, PermissionDeniedException {
		//Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateServiceImpl schedulingTemplateService = schedulingTemplateServiceMocked(userContext, meetingUser, true);

		//When
		List<SchedulingTemplate> templates = new ArrayList<>();
		SchedulingTemplate template = getSchedulingTemplateWithDefaultValues(organisation, 1L);
		template.setIsPoolTemplate(true);
		templates.add(template);
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsPoolTemplateAndDeletedTimeIsNull(organisation, true)).thenReturn(templates);
		createSchedulingTemplateDto.setIsPoolTemplate(true);

		//Then
		var expectedException = assertThrows(NotAcceptableException.class, () -> schedulingTemplateService.createSchedulingTemplate(createSchedulingTemplateDto, true));
		assertEquals("Create or update of pool template failed due to only one pool template allowed", expectedException.getMessage());
	}

	@Test
	public void testUpdateSchedulingTemplateOnlyOnePool() throws RessourceNotFoundException, PermissionDeniedException {
		//Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateServiceImpl schedulingTemplateService = schedulingTemplateServiceMocked(userContext, meetingUser, true);

		//When
		List<SchedulingTemplate> templates = new ArrayList<>();
		SchedulingTemplate template = getSchedulingTemplateWithDefaultValues(organisation, 1L);
		template.setIsPoolTemplate(true);
		templates.add(template);
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsPoolTemplateAndDeletedTimeIsNull(organisation, true)).thenReturn(templates);
		updateSchedulingTemplateDto.setIsPoolTemplate(true);

		//Then
		var expectedException = assertThrows(NotAcceptableException.class, () -> schedulingTemplateService.updateSchedulingTemplate(1L, updateSchedulingTemplateDto));
		assertEquals("Create or update of pool template failed due to only one pool template allowed", expectedException.getMessage());
	}

	private CreateSchedulingTemplateDto getCreateSchedulingTemplateDtoWithDefaultValues() {
		CreateSchedulingTemplateDto createSchedulingTemplateDto = new CreateSchedulingTemplateDto();
		createSchedulingTemplateDto.setConferencingSysId(1L);
		createSchedulingTemplateDto.setUriPrefix("a");
		createSchedulingTemplateDto.setUriDomain("b");
		createSchedulingTemplateDto.setHostPinRequired(true);
		createSchedulingTemplateDto.setHostPinRangeLow(10L);
		createSchedulingTemplateDto.setHostPinRangeHigh(99L);
		createSchedulingTemplateDto.setGuestPinRequired(true);
		createSchedulingTemplateDto.setGuestPinRangeLow(100L);
		createSchedulingTemplateDto.setGuestPinRangeHigh(999L);
		createSchedulingTemplateDto.setvMRAvailableBefore(15);
		createSchedulingTemplateDto.setMaxParticipants(10);
		createSchedulingTemplateDto.setEndMeetingOnEndTime(true);
		createSchedulingTemplateDto.setUriNumberRangeLow(1000L);
		createSchedulingTemplateDto.setUriNumberRangeHigh(9999L);
		createSchedulingTemplateDto.setIvrTheme("20");
		createSchedulingTemplateDto.setIsDefaultTemplate(false);
		createSchedulingTemplateDto.setIsPoolTemplate(false);
		createSchedulingTemplateDto.setCustomPortalGuest(UUID.randomUUID().toString());
		createSchedulingTemplateDto.setCustomPortalHost(UUID.randomUUID().toString());
		createSchedulingTemplateDto.setReturnUrl(UUID.randomUUID().toString());

		return createSchedulingTemplateDto;
	}

	private UpdateSchedulingTemplateDto getUpdateSchedulingTemplateDtoWithDefaultValues() {
		UpdateSchedulingTemplateDto updateSchedulingTemplateDto = new UpdateSchedulingTemplateDto();
		updateSchedulingTemplateDto.setConferencingSysId(1L);
		updateSchedulingTemplateDto.setUriPrefix("a");
		updateSchedulingTemplateDto.setUriDomain("b");
		updateSchedulingTemplateDto.setHostPinRequired(true);
		updateSchedulingTemplateDto.setHostPinRangeLow(10L);
		updateSchedulingTemplateDto.setHostPinRangeHigh(99L);
		updateSchedulingTemplateDto.setGuestPinRequired(true);
		updateSchedulingTemplateDto.setGuestPinRangeLow(100L);
		updateSchedulingTemplateDto.setGuestPinRangeHigh(999L);
		updateSchedulingTemplateDto.setvMRAvailableBefore(15);
		updateSchedulingTemplateDto.setMaxParticipants(10);
		updateSchedulingTemplateDto.setEndMeetingOnEndTime(true);
		updateSchedulingTemplateDto.setUriNumberRangeLow(1000L);
		updateSchedulingTemplateDto.setUriNumberRangeHigh(9999L);
		updateSchedulingTemplateDto.setIvrTheme("20");
		updateSchedulingTemplateDto.setIsDefaultTemplate(false);
		updateSchedulingTemplateDto.setIsPoolTemplate(false);

		return updateSchedulingTemplateDto;
	}

	private SchedulingTemplate getSchedulingTemplateWithDefaultValues(Organisation organisation, Long id) {
		SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
		schedulingTemplate.setOrganisation(organisation);
		schedulingTemplate.setId(id);
		schedulingTemplate.setConferencingSysId(1L);
		schedulingTemplate.setUriPrefix("a");
		schedulingTemplate.setUriDomain("b");
		schedulingTemplate.setHostPinRequired(true);
		schedulingTemplate.setHostPinRangeLow(10L);
		schedulingTemplate.setHostPinRangeHigh(99L);
		schedulingTemplate.setGuestPinRequired(true);
		schedulingTemplate.setGuestPinRangeLow(100L);
		schedulingTemplate.setGuestPinRangeHigh(999L);
		schedulingTemplate.setVMRAvailableBefore(15);
		schedulingTemplate.setMaxParticipants(10);
		schedulingTemplate.setEndMeetingOnEndTime(true);
		schedulingTemplate.setUriNumberRangeLow(1000L);
		schedulingTemplate.setUriNumberRangeHigh(9999L);
		schedulingTemplate.setIvrTheme("20");
		schedulingTemplate.setIsDefaultTemplate(false);
		schedulingTemplate.setIsPoolTemplate(false);
		schedulingTemplate.setCustomPortalGuest(UUID.randomUUID().toString());
		schedulingTemplate.setCustomPortalHost(UUID.randomUUID().toString());
		schedulingTemplate.setReturnUrl(UUID.randomUUID().toString());

		return schedulingTemplate;
	}

	private SchedulingTemplateServiceImpl schedulingTemplateServiceMocked(UserContext userContext, MeetingUser meetingUser, boolean isCreate) throws PermissionDeniedException, RessourceNotFoundException {
		schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		SchedulingTemplateServiceImpl schedulingTemplateService = simpleSchedulingTemplateServiceMocked(userContext, meetingUser, schedulingTemplateRepository);

		SchedulingTemplate schedulingTemplateInService = getSchedulingTemplateWithDefaultValues(organisation,  1L);


		if (isCreate) {
			Mockito.when(schedulingTemplateRepository.save(Mockito.any(SchedulingTemplate.class))).thenReturn(schedulingTemplateInService);
		} else {
			Mockito.when(schedulingTemplateRepository.save(schedulingTemplateInService)).thenAnswer(i -> i.getArguments()[0]); //returns the actual modified meeting from the updateMeeting call
		}

		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIdAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(1L))).thenReturn(schedulingTemplateInService);
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIdAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(777L))).thenReturn(null);

		List<SchedulingTemplate> schedulingTemplatesInServiceEmpty = new ArrayList<>();
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(true))).thenReturn(schedulingTemplatesInServiceEmpty);

		Mockito.when(schedulingTemplateRepository.save(Mockito.any())).thenAnswer(x -> x.getArgument(0));

		return schedulingTemplateService;
	}

	private SchedulingTemplateServiceImpl simpleSchedulingTemplateServiceMocked(UserContext userContext, MeetingUser meetingUser, SchedulingTemplateRepository schedulingTemplateRepository) throws PermissionDeniedException, RessourceNotFoundException {

		UserContextService userService = Mockito.mock(UserContextService.class);
		OrganisationService organisationService = Mockito.mock(OrganisationService.class);
		MeetingUserServiceImpl meetingUserService = Mockito.mock(MeetingUserServiceImpl.class);

		SchedulingTemplateServiceImpl schedulingTemplateService = new SchedulingTemplateServiceImpl(schedulingTemplateRepository, userService, organisationService, meetingUserService, organisationTreeServiceClient);

		Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser()).thenReturn(meetingUser);
		Mockito.when(organisationService.getUserOrganisation()).thenReturn(meetingUser.getOrganisation());

		return schedulingTemplateService;
	}
}
