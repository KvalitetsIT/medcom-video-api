package dk.medcom.video.api.service;

import java.util.ArrayList;
import java.util.List;

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
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.CreateSchedulingTemplateDto;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.UpdateSchedulingTemplateDto;
import dk.medcom.video.api.repository.SchedulingTemplateRepository;

public class SchedulingTemplateServiceTest {
	
	private CreateSchedulingTemplateDto createSchedulingTemplateDto;	
	private UpdateSchedulingTemplateDto updateSchedulingTemplateDto;
	private MeetingUser meetingUser;
	private Organisation organisation;
	

	@Before
	public void prepareTest() {
		createSchedulingTemplateDto = getCreateSchedulingTemplateDtoWithDefaultValues();
		updateSchedulingTemplateDto = getUpdateSchedulingTemplateDtoWithDefaultValues();
		meetingUser = new MeetingUser();
		organisation = new Organisation();
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
	
	private SchedulingTemplateService createSchedulingTemplateServiceMocked(UserContext userContext, MeetingUser meetingUser, boolean isCreate) throws PermissionDeniedException, RessourceNotFoundException {
		//TODO and clean up
		SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
		
		UserContextService userService = Mockito.mock(UserContextService.class);
		OrganisationService organisationService = Mockito.mock(OrganisationService.class);


//TODO: ryd op
//		UserContextService userContextService = Mockito.mock(UserContextService.class);
//		
		SchedulingTemplateService schedulingTemplateService = new SchedulingTemplateService(schedulingTemplateRepository, userService, organisationService);
//		Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
//		
//		MeetingUser meetingUserOrganizer = new MeetingUser();
//		Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser()).thenReturn(meetingUser);
//		Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser(Mockito.anyString())).thenReturn(meetingUserOrganizer);
//		
		Mockito.when(organisationService.getUserOrganisation()).thenReturn(meetingUser.getOrganisation());
//		Meeting meetingInService = getMeetingWithDefaultValues(meetingUser,  uuid);
//		Mockito.when(meetingRepository.findOneByUuid(Mockito.anyString())).thenReturn(meetingInService);
//		SchedulingInfo schedulingInfo = new SchedulingInfo();
//		schedulingInfo.setProvisionStatus(provisionStatus);
//		Mockito.when(schedulingInfoService.getSchedulingInfoByUuid(Mockito.anyString())).thenReturn(schedulingInfo);
		
		SchedulingTemplate schedulingTemplateInService = getSchedulingTemplateWithDefaultValues(organisation,  1L);
		List<SchedulingTemplate> schedulingTemplatesInService = new ArrayList();
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  1L));
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  2L));
		schedulingTemplatesInService.add(getSchedulingTemplateWithDefaultValues(organisation,  3L));
	
		
		if (isCreate) {
			Mockito.when(schedulingTemplateRepository.save(Mockito.any(SchedulingTemplate.class))).thenReturn(schedulingTemplateInService);	
		} else {
			Mockito.when(schedulingTemplateRepository.save(schedulingTemplateInService)).thenAnswer(i -> i.getArguments()[0]); //returns the actual modified meeting from the updateMeething call	
		}
		Mockito.when(schedulingTemplateRepository.findByOrganisation(Mockito.any(Organisation.class))).thenReturn(schedulingTemplatesInService);
		Mockito.when(schedulingTemplateRepository.findByOrganisationAndId(Mockito.any(Organisation.class), Mockito.anyLong())).thenReturn(schedulingTemplateInService);
		
		
		return schedulingTemplateService;
	}
	
	@Test 
	public void testCreateSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateService schedulingTemplateService = createSchedulingTemplateServiceMocked(userContext, meetingUser, true);
		
		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.createSchedulingTemplate(createSchedulingTemplateDto);
		
		// Then
		Assert.assertNotNull(schedulingTemplate);
		//TODO: udvid test
	}
	
	@Test 
	public void testUpdateSchedulingTemplate() throws PermissionDeniedException, RessourceNotFoundException {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateService schedulingTemplateService = createSchedulingTemplateServiceMocked(userContext, meetingUser, false);
		updateSchedulingTemplateDto.setGuestPinRequired(false);
		
		// When
		SchedulingTemplate schedulingTemplate = schedulingTemplateService.updateSchedulingTemplate(1L, updateSchedulingTemplateDto);
		
		// Then
		Assert.assertNotNull(schedulingTemplate);
		Assert.assertEquals(false, schedulingTemplate.getGuestPinRequired());
		//TODO: udvid test
	}
	
	@Test  
	public void testGetSchedulingTemplates() throws PermissionDeniedException, RessourceNotFoundException {
		
		// Given
		UserContext userContext = new UserContextImpl("org", "test@test.dk", UserRole.ADMIN);

		SchedulingTemplateService schedulingTemplateService = createSchedulingTemplateServiceMocked(userContext, meetingUser, false);
		
		// When
		List<SchedulingTemplate> schedulingTemplates = schedulingTemplateService.getSchedulingTemplates();
		
		// Then
		Assert.assertNotNull(schedulingTemplates);
		Assert.assertEquals(3, schedulingTemplates.size());
		//TODO: udvid test 
	}
	
//TODO: flere teste se test rapport	

}
