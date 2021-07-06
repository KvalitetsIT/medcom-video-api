package dk.medcom.video.api.service;

import dk.medcom.video.api.api.*;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class SchedulingTemplateService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoService.class);

	@Autowired
	SchedulingTemplateRepository schedulingTemplateRepository;
	
	@Autowired
	UserContextService userService;
	
	@Autowired
	OrganisationService organisationService;
	
	@Autowired
	MeetingUserService meetingUserService;

	
	@Value("${scheduling.template.default.conferencing.sys.id}")
	private Long conferencingSysId;
	@Value("${scheduling.template.default.uri.prefix}")
	private String uriPrefix;  		
	@Value("${scheduling.template.default.uri.domain}")
	private String uriDomain;  		
	@Value("${scheduling.template.default.host.pin.required}")
	private boolean hostPinRequired;
	@Value("${scheduling.template.default.host.pin.range.low}")
	private Long hostPinRangeLow; 	
	@Value("${scheduling.template.default.host.pin.range.high}")
	private Long hostPinRangeHigh; 	
	@Value("${scheduling.template.default.guest.pin.required}")
	private boolean guestPinRequired;
	@Value("${scheduling.template.default.guest.pin.range.low}")
	private Long guestPinRangeLow;	
	@Value("${scheduling.template.default.guest.pin.range.high}")
	private Long guestPinRangeHigh;	
	@Value("${scheduling.template.default.vmravailable.before}")
	private int vMRAvailableBefore;	
	@Value("${scheduling.template.default.max.participants}")
	private int maxParticipants;	
	@Value("${scheduling.template.default.end.meeting.on.end.time}")
	private boolean endMeetingOnEndTime;	
	@Value("${scheduling.template.default.uri.number.range.low}")
	private Long uriNumberRangeLow;			
	@Value("${scheduling.template.default.uri.number.range.high}")
	private Long uriNumberRangeHigh;		
	@Value("${scheduling.template.default.ivr.theme}")
	private String ivrTheme;

	private final OrganisationFinder organisationFinder = new OrganisationFinder();

	@Autowired
	private OrganisationTreeServiceClient organisationTreeServiceClient;

	public SchedulingTemplateService() {
		
	}
	
	public SchedulingTemplateService(SchedulingTemplateRepository schedulingTemplateRepository, UserContextService userService, OrganisationService organisationService, MeetingUserService meetingUserService, OrganisationTreeServiceClient organisationTreeServiceClient) {
	 	this.schedulingTemplateRepository = schedulingTemplateRepository;
	 	this.userService = userService;
	 	this.organisationService = organisationService;
	 	this.meetingUserService = meetingUserService;
		this.organisationTreeServiceClient = organisationTreeServiceClient;
	}

	public SchedulingTemplate getSchedulingTemplateInOrganisationTree() throws PermissionDeniedException {
		Organisation organisation = organisationService.getUserOrganisation();
		List<SchedulingTemplate> schedulingTemplates = null;
		if (organisation != null) {
			//first try: find default template for organization. Use list just in case.
			schedulingTemplates = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(organisation, true);
			if (schedulingTemplates.size() > 0) {
				LOGGER.debug("Template found using default organization template: " + schedulingTemplates.get(0).toString());
				return schedulingTemplates.get(0);
			}

			// Find in tree
			var organisationTree = organisationTreeServiceClient.getOrganisationTree(organisation.getOrganisationId());
			var parent = organisationFinder.findParentOrganisation(organisation.getOrganisationId(), organisationTree);
			while(parent.isPresent()) {
				var schedulingTemplateFromTree = schedulingTemplateRepository.findByOrganisationIdAndIsDefaultTemplateAndDeletedTimeIsNull(parent.get().getCode());
				if(schedulingTemplateFromTree != null && !schedulingTemplateFromTree.isEmpty()) {
					LOGGER.debug("Scheduling template found in organisation tree. Using scheduling template from organisation: {}", parent.get().getCode());
					return schedulingTemplateFromTree.get(0);
				}
				parent = organisationFinder.findParentOrganisation(parent.get().getCode(), organisationTree);
			}

			//second try: find shared default template, where organization is null
			schedulingTemplates = schedulingTemplateRepository.findByOrganisationIsNullAndDeletedTimeIsNull();
			if (schedulingTemplates.size() > 0) {
				LOGGER.debug("Template found using shared default template: " + schedulingTemplates.get(0).toString());
				return schedulingTemplates.get(0);
			}

			//if none of above is found - create the shared default template
			CreateSchedulingTemplateDto createSchedulingTemplateDto = getSchedulingTemplateDto();
			SchedulingTemplate schedulingTemplate = createSchedulingTemplate(createSchedulingTemplateDto, false);
			LOGGER.debug("Creating default schedulingTemplate: " + schedulingTemplate.toString());
			return schedulingTemplateRepository.save(schedulingTemplate);

		}
		else {
			LOGGER.debug("organization is null");
			throw new PermissionDeniedException();
		}
	}

	public SchedulingTemplate getSchedulingTemplateFromOrganisationAndId(Long schedulingTemplateId) throws PermissionDeniedException, RessourceNotFoundException {
		SchedulingTemplate schedulingTemplate = schedulingTemplateRepository.findByOrganisationAndIdAndDeletedTimeIsNull(organisationService.getUserOrganisation(), schedulingTemplateId);
		
		if (schedulingTemplate == null) {
			LOGGER.debug("scheduleTemplate not found. Id: " + schedulingTemplateId + ". Organisation: " + organisationService.getUserOrganisation().toString());
			throw new RessourceNotFoundException("schedulingTemplate", "id");
		}
		return schedulingTemplate;
	}
	
	public List<SchedulingTemplate> getSchedulingTemplates() throws PermissionDeniedException  {
		return schedulingTemplateRepository.findByOrganisationAndDeletedTimeIsNull(organisationService.getUserOrganisation()) ;
	}

	public SchedulingTemplate createSchedulingTemplate(CreateSchedulingTemplateDto createSchedulingTemplateDto, boolean includeOrganisation) throws PermissionDeniedException  {
		LOGGER.debug("Entry createSchedulingTemplate");

		SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
		
		//first find any other default template and make it non-default 
		if (createSchedulingTemplateDto.getIsDefaultTemplate()) {
			LOGGER.debug("SchedulingTemplate is a default template");
			if (!checkIfDefaultTemplateExistAndRemove()) {
				LOGGER.debug("It was not possible to remove other default templates");
				throw new PermissionDeniedException();
			}
		}
		
		//then create the new template
		if (includeOrganisation) {
			schedulingTemplate.setOrganisation(organisationService.getUserOrganisation());	
		}
		schedulingTemplate.setConferencingSysId(createSchedulingTemplateDto.getConferencingSysId());
		schedulingTemplate.setUriPrefix(createSchedulingTemplateDto.getUriPrefix());
		schedulingTemplate.setUriDomain(createSchedulingTemplateDto.getUriDomain());
		schedulingTemplate.setHostPinRequired(createSchedulingTemplateDto.isHostPinRequired());
		schedulingTemplate.setHostPinRangeLow(createSchedulingTemplateDto.getHostPinRangeLow());
		schedulingTemplate.setHostPinRangeHigh(createSchedulingTemplateDto.getHostPinRangeHigh());
		schedulingTemplate.setGuestPinRequired(createSchedulingTemplateDto.isGuestPinRequired());
		schedulingTemplate.setGuestPinRangeLow(createSchedulingTemplateDto.getGuestPinRangeLow());
		schedulingTemplate.setGuestPinRangeHigh(createSchedulingTemplateDto.getGuestPinRangeHigh());
//		if (createSchedulingTemplateDto.getvMRAvailableBefore() == null) {
//			
//		}
		schedulingTemplate.setVMRAvailableBefore(createSchedulingTemplateDto.getvMRAvailableBefore());
		schedulingTemplate.setMaxParticipants(createSchedulingTemplateDto.getMaxParticipants());
		schedulingTemplate.setEndMeetingOnEndTime(createSchedulingTemplateDto.isEndMeetingOnEndTime());
		schedulingTemplate.setUriNumberRangeLow(createSchedulingTemplateDto.getUriNumberRangeLow());
		schedulingTemplate.setUriNumberRangeHigh(createSchedulingTemplateDto.getUriNumberRangeHigh());
		schedulingTemplate.setIvrTheme(createSchedulingTemplateDto.getIvrTheme());
		schedulingTemplate.setIsDefaultTemplate(createSchedulingTemplateDto.getIsDefaultTemplate());

		schedulingTemplate.setVmrType(createSchedulingTemplateDto.getVmrType() != null ? createSchedulingTemplateDto.getVmrType() : VmrType.CONFERENCE);
		schedulingTemplate.setHostView(createSchedulingTemplateDto.getHostView() != null ? createSchedulingTemplateDto.getHostView() : ViewType.ONE_MAIN_SEVEN_PIPS);
		schedulingTemplate.setGuestView(createSchedulingTemplateDto.getGuestView() != null ? createSchedulingTemplateDto.getGuestView() : ViewType.ONE_MAIN_SEVEN_PIPS);
		schedulingTemplate.setVmrQuality(createSchedulingTemplateDto.getVmrQuality() != null ? createSchedulingTemplateDto.getVmrQuality() : VmrQuality.HD);
		schedulingTemplate.setEnableOverlayText(createSchedulingTemplateDto.getEnableOverlayText() != null ? createSchedulingTemplateDto.getEnableOverlayText() : true);
		schedulingTemplate.setGuestsCanPresent(createSchedulingTemplateDto.getGuestsCanPresent() != null ? createSchedulingTemplateDto.getGuestsCanPresent() : true);
		schedulingTemplate.setForcePresenterIntoMain(createSchedulingTemplateDto.getForcePresenterIntoMain() != null ? createSchedulingTemplateDto.getForcePresenterIntoMain() : true);
		schedulingTemplate.setForceEncryption(createSchedulingTemplateDto.getForceEncryption() != null ? createSchedulingTemplateDto.getForceEncryption() : false);
		schedulingTemplate.setMuteAllGuests(createSchedulingTemplateDto.getMuteAllGuests() != null ? createSchedulingTemplateDto.getMuteAllGuests() : false);

		schedulingTemplate.setCreatedBy(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingTemplate.setCreatedTime(calendarNow.getTime());
		schedulingTemplate = schedulingTemplateRepository.save(schedulingTemplate);
		
		LOGGER.debug("Exit createSchedulingTemplate");
		return schedulingTemplate;
	}
	
	public SchedulingTemplate updateSchedulingTemplate(Long id, UpdateSchedulingTemplateDto updateSchedulingTemplateDto) throws PermissionDeniedException, RessourceNotFoundException  {
		LOGGER.debug("Entry updateSchedulingTemplate. id/updateSchedulingTemplateDto. id=" + id);
		
		SchedulingTemplate schedulingTemplate = getSchedulingTemplateFromOrganisationAndId(id);

		//first find any other default template and make it non-default 
		if (updateSchedulingTemplateDto.getIsDefaultTemplate() && !schedulingTemplate.getIsDefaultTemplate() ) {
			LOGGER.debug("SchedulingTemplate is a default template");
			if (!checkIfDefaultTemplateExistAndRemove()) {
				LOGGER.debug("It was not possible to remove other default templates");
				throw new PermissionDeniedException();
			}
		}
		//then update the template
		schedulingTemplate.setConferencingSysId(updateSchedulingTemplateDto.getConferencingSysId());
		schedulingTemplate.setUriPrefix(updateSchedulingTemplateDto.getUriPrefix());
		schedulingTemplate.setUriDomain(updateSchedulingTemplateDto.getUriDomain());
		schedulingTemplate.setHostPinRequired(updateSchedulingTemplateDto.isHostPinRequired());
		schedulingTemplate.setHostPinRangeLow(updateSchedulingTemplateDto.getHostPinRangeLow());
		schedulingTemplate.setHostPinRangeHigh(updateSchedulingTemplateDto.getHostPinRangeHigh());
		schedulingTemplate.setGuestPinRequired(updateSchedulingTemplateDto.isGuestPinRequired());
		schedulingTemplate.setGuestPinRangeLow(updateSchedulingTemplateDto.getGuestPinRangeLow());
		schedulingTemplate.setGuestPinRangeHigh(updateSchedulingTemplateDto.getGuestPinRangeHigh());
		schedulingTemplate.setVMRAvailableBefore(updateSchedulingTemplateDto.getvMRAvailableBefore());
		schedulingTemplate.setMaxParticipants(updateSchedulingTemplateDto.getMaxParticipants());
		schedulingTemplate.setEndMeetingOnEndTime(updateSchedulingTemplateDto.isEndMeetingOnEndTime());
		schedulingTemplate.setUriNumberRangeLow(updateSchedulingTemplateDto.getUriNumberRangeLow());
		schedulingTemplate.setUriNumberRangeHigh(updateSchedulingTemplateDto.getUriNumberRangeHigh());
		schedulingTemplate.setIvrTheme(updateSchedulingTemplateDto.getIvrTheme());
		schedulingTemplate.setIsDefaultTemplate(updateSchedulingTemplateDto.getIsDefaultTemplate());

		schedulingTemplate.setVmrType(updateSchedulingTemplateDto.getVmrType() != null ? updateSchedulingTemplateDto.getVmrType() : VmrType.CONFERENCE);
		schedulingTemplate.setHostView(updateSchedulingTemplateDto.getHostView() != null ? updateSchedulingTemplateDto.getHostView() : ViewType.ONE_MAIN_SEVEN_PIPS);
		schedulingTemplate.setGuestView(updateSchedulingTemplateDto.getGuestView() != null ? updateSchedulingTemplateDto.getGuestView() : ViewType.ONE_MAIN_SEVEN_PIPS);
		schedulingTemplate.setVmrQuality(updateSchedulingTemplateDto.getVmrQuality() != null ? updateSchedulingTemplateDto.getVmrQuality() : VmrQuality.HD);
		schedulingTemplate.setEnableOverlayText(updateSchedulingTemplateDto.getEnableOverlayText() != null ? updateSchedulingTemplateDto.getEnableOverlayText() : true);
		schedulingTemplate.setGuestsCanPresent(updateSchedulingTemplateDto.getGuestsCanPresent() != null ? updateSchedulingTemplateDto.getGuestsCanPresent() : true);
		schedulingTemplate.setForcePresenterIntoMain(updateSchedulingTemplateDto.getForcePresenterIntoMain() != null ? updateSchedulingTemplateDto.getForcePresenterIntoMain() : true);
		schedulingTemplate.setForceEncryption(updateSchedulingTemplateDto.getForceEncryption() != null ? updateSchedulingTemplateDto.getForceEncryption() : false);
		schedulingTemplate.setMuteAllGuests(updateSchedulingTemplateDto.getMuteAllGuests() != null ? updateSchedulingTemplateDto.getMuteAllGuests() : false);
		
		schedulingTemplate.setUpdatedBy(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingTemplate.setUpdatedTime(calendarNow.getTime());

		schedulingTemplate = schedulingTemplateRepository.save(schedulingTemplate);
		
		LOGGER.debug("Exit updateSchedulingTemplate");
		return schedulingTemplate;
	}
	
	public void deleteSchedulingTemplate(Long id) throws PermissionDeniedException, RessourceNotFoundException  {
		LOGGER.debug("Entry deleteSchedulingTemplate. id: " + id);
		
		SchedulingTemplate schedulingTemplate = getSchedulingTemplateFromOrganisationAndId(id);
		
		schedulingTemplate.setDeletedBy(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingTemplate.setDeletedTime(calendarNow.getTime());
		
		schedulingTemplate = schedulingTemplateRepository.save(schedulingTemplate);
		LOGGER.debug("Exit deleteSchedulingTemplate");
	}

	private boolean checkIfDefaultTemplateExistAndRemove() throws PermissionDeniedException {
		List<SchedulingTemplate> schedulingTemplates = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(organisationService.getUserOrganisation(), true); 
		//cannot return "same" record as being return, because compare on	 isDefaultTemplate has been made in call method. 	

		if (schedulingTemplates.size() == 0) {
			LOGGER.debug("No existing default template exist for organization: " + organisationService.getUserOrganisation().toString());
			return true;
		}
		
		for (SchedulingTemplate schedulingTemplate : schedulingTemplates) {
			schedulingTemplate.setIsDefaultTemplate(false);
			schedulingTemplate.setUpdatedBy(meetingUserService.getOrCreateCurrentMeetingUser());
			Calendar calendarNow = new GregorianCalendar();
			schedulingTemplate.setUpdatedTime(calendarNow.getTime());
			schedulingTemplateRepository.save(schedulingTemplate);
			LOGGER.debug("Changed default template not to be default: " + schedulingTemplate.toString());
		}
		
		//double check just to be sure that the reset was completed
		schedulingTemplates = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(organisationService.getUserOrganisation(), true);	

		if (schedulingTemplates.size() == 0) {
			LOGGER.debug("Existing default template has been reset as non-default for organization: " + organisationService.getUserOrganisation().toString());
			return true;
		}
		
		//we have not been able to reset existing template and hence cannot add a new default
		LOGGER.debug("Existing default templates prevent adding a new one. Organisation : " + organisationService.getUserOrganisation().toString());
		return false;
	}
	
	private CreateSchedulingTemplateDto getSchedulingTemplateDto()  {
		CreateSchedulingTemplateDto createSchedulingTemplateDto = new CreateSchedulingTemplateDto();
		//schedulingTemplate.setOrganisation(organisation); //for default schedulingTemplate the organisation is null
		createSchedulingTemplateDto.setConferencingSysId(conferencingSysId);
		createSchedulingTemplateDto.setUriPrefix(uriPrefix);
		createSchedulingTemplateDto.setUriDomain(uriDomain);
		createSchedulingTemplateDto.setHostPinRequired(hostPinRequired);
		createSchedulingTemplateDto.setHostPinRangeLow(hostPinRangeLow);
		createSchedulingTemplateDto.setHostPinRangeHigh(hostPinRangeHigh);
		createSchedulingTemplateDto.setGuestPinRequired(guestPinRequired);
		createSchedulingTemplateDto.setGuestPinRangeLow(guestPinRangeLow);
		createSchedulingTemplateDto.setGuestPinRangeHigh(guestPinRangeHigh);
		createSchedulingTemplateDto.setvMRAvailableBefore(vMRAvailableBefore);
		createSchedulingTemplateDto.setMaxParticipants(maxParticipants);
		createSchedulingTemplateDto.setEndMeetingOnEndTime(endMeetingOnEndTime);
		createSchedulingTemplateDto.setUriNumberRangeLow(uriNumberRangeLow);
		createSchedulingTemplateDto.setUriNumberRangeHigh(uriNumberRangeHigh);
		createSchedulingTemplateDto.setIvrTheme(ivrTheme);;
		return createSchedulingTemplateDto;
	}
	
}
