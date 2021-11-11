package dk.medcom.video.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.medcom.video.api.organisation.OrganisationTree;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import dk.medcom.video.api.service.OrganisationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

import static org.mockito.Mockito.times;

public class SchedulingTemplateServiceTest {
	
	private CreateSchedulingTemplateDto createSchedulingTemplateDto;	
	private UpdateSchedulingTemplateDto updateSchedulingTemplateDto;
	private MeetingUser meetingUser;
	private Organisation organisation;
	private OrganisationTreeServiceClient organisationTreeServiceClient;
	

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
	
	public CreateSchedulingTemplateDto getCreateSchedulingTemplateDtoWithDefaultValues() {
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
		return createSchedulingTemplateDto;
	}
	
	public UpdateSchedulingTemplateDto getUpdateSchedulingTemplateDtoWithDefaultValues() {
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
		return updateSchedulingTemplateDto;
	}
	            
	public SchedulingTemplate getSchedulingTemplateWithDefaultValues(Organisation organisation, Long id) {
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
		return schedulingTemplate;
	}
	
	private SchedulingTemplateServiceImpl SchedulingTemplateServiceMocked(UserContext userContext, MeetingUser meetingUser, boolean isCreate) throws PermissionDeniedException, RessourceNotFoundException {
		SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		SchedulingTemplateServiceImpl schedulingTemplateService = simpleSchedulingTemplateServiceMocked(userContext, meetingUser, schedulingTemplateRepository);
	
		SchedulingTemplate schedulingTemplateInService = getSchedulingTemplateWithDefaultValues(organisation,  1L);

		
		if (isCreate) {
			Mockito.when(schedulingTemplateRepository.save(Mockito.any(SchedulingTemplate.class))).thenReturn(schedulingTemplateInService);	
		} else {
			Mockito.when(schedulingTemplateRepository.save(schedulingTemplateInService)).thenAnswer(i -> i.getArguments()[0]); //returns the actual modified meeting from the updateMeething call	
		}
		
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIdAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(1L))).thenReturn(schedulingTemplateInService);
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIdAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(777L))).thenReturn(null);
		
		List<SchedulingTemplate> schedulingTemplatesInServiceEmpty = new ArrayList();
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(true))).thenReturn(schedulingTemplatesInServiceEmpty);
		
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
	
	@Test 
	public void testCreateSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateServiceImpl schedulingTemplateService = SchedulingTemplateServiceMocked(userContext, meetingUser, true);
		
		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.createSchedulingTemplate(createSchedulingTemplateDto, true);
		
		// Then
		Assert.assertNotNull(schedulingTemplate);

	}
	
	@Test 
	public void testUpdateSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateServiceImpl schedulingTemplateService = SchedulingTemplateServiceMocked(userContext, meetingUser, false);
		updateSchedulingTemplateDto.setGuestPinRequired(false);
		
		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.updateSchedulingTemplate(1L, updateSchedulingTemplateDto);
		
		// Then
		Assert.assertNotNull(schedulingTemplate);
		Assert.assertEquals(false, schedulingTemplate.getGuestPinRequired());

	}

	@Test(expected = RessourceNotFoundException.class)
	public void testDeleteSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException  {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateServiceImpl schedulingTemplateService = SchedulingTemplateServiceMocked(userContext, meetingUser, false);
		updateSchedulingTemplateDto.setGuestPinRequired(false);
		
		// When
		schedulingTemplateService.deleteSchedulingTemplate(1L);
		
		// Then
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(777L); //cheating, to retrieve null
	}


	@Test 
	public void getGetSchedulingTemplateInOrganisationTree() throws PermissionDeniedException, RessourceNotFoundException   {
		
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
		Assert.assertNotNull(schedulingTemplate);
		Assert.assertNotNull(schedulingTemplate.getOrganisation());
		Mockito.verify(organisationTreeServiceClient, times(0)).getOrganisationTree("org");

	}

	@Test
	public void getGetSchedulingTemplateInOrganisationTreeOtherOrganisation() throws PermissionDeniedException, RessourceNotFoundException   {

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
		var child = createOrganisationTree("org", "org-name", null, 0);
		var childOne = createOrganisationTree("childOne", "childOne-name", Collections.singletonList(child), 0);
		var parent = createOrganisationTree("parent", "parent-name", Collections.singletonList(childOne), 0);
		var superParent = createOrganisationTree("superParent", "superParent-name", Collections.singletonList(parent), 0);
		Mockito.when(organisationTreeServiceClient.getOrganisationTree("org")).thenReturn(superParent);

		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateInOrganisationTree();

		// Then
		Assert.assertNotNull(schedulingTemplate);
		Assert.assertNotNull(schedulingTemplate.getOrganisation());
		Mockito.verify(organisationTreeServiceClient, times(1)).getOrganisationTree("org");
	}
	
	@Test 
	public void testGetSharedSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException   {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		SchedulingTemplateServiceImpl schedulingTemplateService = simpleSchedulingTemplateServiceMocked(userContext, meetingUser, schedulingTemplateRepository);
		
		List<SchedulingTemplate> schedulingTemplatesInServiceEmpty = new ArrayList();
		List<SchedulingTemplate> schedulingTemplatesInService = new ArrayList();

		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(null,  1L));
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(Organisation.class), Mockito.eq(true))).thenReturn(schedulingTemplatesInServiceEmpty);
		Mockito.when(schedulingTemplateRepository.findByOrganisationIsNullAndDeletedTimeIsNull()).thenReturn(schedulingTemplatesInService);
		Mockito.when(organisationTreeServiceClient.getOrganisationTree("org")).thenReturn(createOrganisationTree("org", "org-name", null, 0));

		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateInOrganisationTree();
		
		// Then
		Assert.assertNotNull(schedulingTemplate);
		Assert.assertNull(schedulingTemplate.getOrganisation());
		Mockito.verify(organisationTreeServiceClient, times(1)).getOrganisationTree("org");
	}

	private OrganisationTree createOrganisationTree(String code, String name, List<OrganisationTree> children, int poolSize) {
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
		
		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(1L);
		
		// Then
		Assert.assertNotNull(schedulingTemplate);

	}
	
	@Test  
	public void testGetSchedulingTemplates() throws PermissionDeniedException, RessourceNotFoundException {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		SchedulingTemplateServiceImpl schedulingTemplateService = simpleSchedulingTemplateServiceMocked(userContext, meetingUser, schedulingTemplateRepository);
				
		List<SchedulingTemplate> schedulingTemplatesInService = new ArrayList();
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  1L));
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  2L));
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  3L));
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndDeletedTimeIsNull(Mockito.any(Organisation.class))).thenReturn(schedulingTemplatesInService);
		
		// When
		List<SchedulingTemplate> schedulingTemplates = schedulingTemplateService.getSchedulingTemplates();
		
		// Then
		Assert.assertNotNull(schedulingTemplates);
		Assert.assertEquals(3, schedulingTemplates.size());
 
	}

}
