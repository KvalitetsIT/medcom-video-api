package dk.medcom.video.api.service;

import dk.medcom.video.api.api.*;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.NotAcceptableErrors;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class SchedulingTemplateServiceImpl implements SchedulingTemplateService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoServiceImpl.class);

	private final SchedulingTemplateRepository schedulingTemplateRepository;

	private final OrganisationService organisationService;
	
	private final MeetingUserService meetingUserService;

	private final Long conferencingSysId;
	private final String uriPrefix;
	private final String uriDomain;
	private final boolean hostPinRequired;
	private final Long hostPinRangeLow;
	private final Long hostPinRangeHigh;
	private final boolean guestPinRequired;
	private final Long guestPinRangeLow;
	private final Long guestPinRangeHigh;
	private final int vMRAvailableBefore;
	private final int maxParticipants;
	private final boolean endMeetingOnEndTime;
	private final Long uriNumberRangeLow;
	private final Long uriNumberRangeHigh;
	private final String ivrTheme;

	private final OrganisationFinder organisationFinder = new OrganisationFinder();

	private final OrganisationTreeServiceClient organisationTreeServiceClient;

	public SchedulingTemplateServiceImpl(SchedulingTemplateRepository schedulingTemplateRepository,
										 OrganisationService organisationService,
										 MeetingUserService meetingUserService,
										 OrganisationTreeServiceClient organisationTreeServiceClient,
										 Long conferencingSysId,
										 String uriPrefix,
										 String uriDomain,
										 boolean hostPinRequired,
										 Long hostPinRangeLow,
										 Long hostPinRangeHigh,
										 boolean guestPinRequired,
										 Long guestPinRangeLow,
										 Long guestPinRangeHigh,
										 int vMRAvailableBefore,
										 int maxParticipants,
										 boolean endMeetingOnEndTime,
										 Long uriNumberRangeLow,
										 Long uriNumberRangeHigh,
										 String ivrTheme) {
	 	this.schedulingTemplateRepository = schedulingTemplateRepository;
		this.organisationService = organisationService;
	 	this.meetingUserService = meetingUserService;
		this.organisationTreeServiceClient = organisationTreeServiceClient;
		this.conferencingSysId = conferencingSysId;
		this.uriPrefix = uriPrefix;
		this.uriDomain = uriDomain;
		this.hostPinRequired = hostPinRequired;
		this.hostPinRangeLow = hostPinRangeLow;
		this.hostPinRangeHigh = hostPinRangeHigh;
		this.guestPinRequired = guestPinRequired;
		this.guestPinRangeLow = guestPinRangeLow;
		this.guestPinRangeHigh = guestPinRangeHigh;
		this.vMRAvailableBefore = vMRAvailableBefore;
		this.maxParticipants = maxParticipants;
		this.endMeetingOnEndTime = endMeetingOnEndTime;
		this.uriNumberRangeLow = uriNumberRangeLow;
		this.uriNumberRangeHigh = uriNumberRangeHigh;
		this.ivrTheme = ivrTheme;
	}

	@Override
	public SchedulingTemplate getSchedulingTemplateInOrganisationTree() throws PermissionDeniedException, NotAcceptableException {
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

	@Override
	public SchedulingTemplate getSchedulingTemplateFromOrganisationAndId(Long schedulingTemplateId) throws PermissionDeniedException, RessourceNotFoundException {
		SchedulingTemplate schedulingTemplate = schedulingTemplateRepository.findByOrganisationAndIdAndDeletedTimeIsNull(organisationService.getUserOrganisation(), schedulingTemplateId);
		
		if (schedulingTemplate == null) {
			LOGGER.debug("scheduleTemplate not found. Id: " + schedulingTemplateId + ". Organisation: " + organisationService.getUserOrganisation().toString());
			throw new RessourceNotFoundException("schedulingTemplate", "id");
		}
		return schedulingTemplate;
	}
	
	@Override
	public List<SchedulingTemplate> getSchedulingTemplates() throws PermissionDeniedException  {
		return schedulingTemplateRepository.findByOrganisationAndDeletedTimeIsNull(organisationService.getUserOrganisation()) ;
	}

	@Override
	public SchedulingTemplate createSchedulingTemplate(CreateSchedulingTemplateDto createSchedulingTemplateDto, boolean includeOrganisation) throws PermissionDeniedException, NotAcceptableException {
		LOGGER.debug("Entry createSchedulingTemplate");

		SchedulingTemplate schedulingTemplate = new SchedulingTemplate();

		//check if pool template and if so, if another pool template already exists
		if (createSchedulingTemplateDto.getIsPoolTemplate()) {
			LOGGER.debug("SchedulingTemplate is a pool template");
			if (checkIfPoolTemplateAlreadyExists()) {
				LOGGER.debug("There was already a pool template in the organisation");
				throw new NotAcceptableException(NotAcceptableErrors.CREATE_OR_UPDATE_POOL_TEMPLATE_FAILED);
			}
		}

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
		schedulingTemplate.setIsPoolTemplate(createSchedulingTemplateDto.getIsPoolTemplate());

		schedulingTemplate.setVmrType(createSchedulingTemplateDto.getVmrType() != null ? createSchedulingTemplateDto.getVmrType() : VmrType.conference);
		schedulingTemplate.setHostView(createSchedulingTemplateDto.getHostView() != null ? createSchedulingTemplateDto.getHostView() : ViewType.one_main_seven_pips);
		schedulingTemplate.setGuestView(createSchedulingTemplateDto.getGuestView() != null ? createSchedulingTemplateDto.getGuestView() : ViewType.one_main_seven_pips);
		schedulingTemplate.setVmrQuality(createSchedulingTemplateDto.getVmrQuality() != null ? createSchedulingTemplateDto.getVmrQuality() : VmrQuality.hd);
		schedulingTemplate.setEnableOverlayText(createSchedulingTemplateDto.getEnableOverlayText() != null ? createSchedulingTemplateDto.getEnableOverlayText() : true);
		schedulingTemplate.setGuestsCanPresent(createSchedulingTemplateDto.getGuestsCanPresent() != null ? createSchedulingTemplateDto.getGuestsCanPresent() : true);
		schedulingTemplate.setForcePresenterIntoMain(createSchedulingTemplateDto.getForcePresenterIntoMain() != null ? createSchedulingTemplateDto.getForcePresenterIntoMain() : true);
		schedulingTemplate.setForceEncryption(createSchedulingTemplateDto.getForceEncryption() != null ? createSchedulingTemplateDto.getForceEncryption() : false);
		schedulingTemplate.setMuteAllGuests(createSchedulingTemplateDto.getMuteAllGuests() != null ? createSchedulingTemplateDto.getMuteAllGuests() : false);
		schedulingTemplate.setCustomPortalGuest(createSchedulingTemplateDto.getCustomPortalGuest());
		schedulingTemplate.setCustomPortalHost(createSchedulingTemplateDto.getCustomPortalHost());
		schedulingTemplate.setReturnUrl(createSchedulingTemplateDto.getReturnUrl());
		schedulingTemplate.setDirectMedia(createSchedulingTemplateDto.getDirectMedia() != null ? createSchedulingTemplateDto.getDirectMedia() : DirectMedia.never);

		schedulingTemplate.setCreatedBy(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingTemplate.setCreatedTime(calendarNow.getTime());
		schedulingTemplate = schedulingTemplateRepository.save(schedulingTemplate);
		
		LOGGER.debug("Exit createSchedulingTemplate");
		return schedulingTemplate;
	}
	
	@Override
	public SchedulingTemplate updateSchedulingTemplate(Long id, UpdateSchedulingTemplateDto updateSchedulingTemplateDto) throws PermissionDeniedException, RessourceNotFoundException, NotAcceptableException {
		LOGGER.debug("Entry updateSchedulingTemplate. id/updateSchedulingTemplateDto. id=" + id);
		
		SchedulingTemplate schedulingTemplate = getSchedulingTemplateFromOrganisationAndId(id);

		//check if pool template and if so, if another pool template already exists
		if (updateSchedulingTemplateDto.getIsPoolTemplate()) {
			LOGGER.debug("SchedulingTemplate is a pool template");
			if (checkIfPoolTemplateAlreadyExists()) {
				LOGGER.debug("There was already a pool template in the organisation");
				throw new NotAcceptableException(NotAcceptableErrors.CREATE_OR_UPDATE_POOL_TEMPLATE_FAILED);
			}
		}

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
		schedulingTemplate.setIsPoolTemplate(updateSchedulingTemplateDto.getIsPoolTemplate());

		schedulingTemplate.setVmrType(updateSchedulingTemplateDto.getVmrType() != null ? updateSchedulingTemplateDto.getVmrType() : VmrType.conference);
		schedulingTemplate.setHostView(updateSchedulingTemplateDto.getHostView() != null ? updateSchedulingTemplateDto.getHostView() : ViewType.one_main_seven_pips);
		schedulingTemplate.setGuestView(updateSchedulingTemplateDto.getGuestView() != null ? updateSchedulingTemplateDto.getGuestView() : ViewType.one_main_seven_pips);
		schedulingTemplate.setVmrQuality(updateSchedulingTemplateDto.getVmrQuality() != null ? updateSchedulingTemplateDto.getVmrQuality() : VmrQuality.hd);
		schedulingTemplate.setEnableOverlayText(updateSchedulingTemplateDto.getEnableOverlayText() != null ? updateSchedulingTemplateDto.getEnableOverlayText() : true);
		schedulingTemplate.setGuestsCanPresent(updateSchedulingTemplateDto.getGuestsCanPresent() != null ? updateSchedulingTemplateDto.getGuestsCanPresent() : true);
		schedulingTemplate.setForcePresenterIntoMain(updateSchedulingTemplateDto.getForcePresenterIntoMain() != null ? updateSchedulingTemplateDto.getForcePresenterIntoMain() : true);
		schedulingTemplate.setForceEncryption(updateSchedulingTemplateDto.getForceEncryption() != null ? updateSchedulingTemplateDto.getForceEncryption() : false);
		schedulingTemplate.setMuteAllGuests(updateSchedulingTemplateDto.getMuteAllGuests() != null ? updateSchedulingTemplateDto.getMuteAllGuests() : false);
		schedulingTemplate.setCustomPortalGuest(updateSchedulingTemplateDto.getCustomPortalGuest());
		schedulingTemplate.setCustomPortalHost(updateSchedulingTemplateDto.getCustomPortalHost());
		schedulingTemplate.setReturnUrl(updateSchedulingTemplateDto.getReturnUrl());
		schedulingTemplate.setDirectMedia(updateSchedulingTemplateDto.getDirectMedia() != null ? updateSchedulingTemplateDto.getDirectMedia() : DirectMedia.never);

		schedulingTemplate.setUpdatedBy(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingTemplate.setUpdatedTime(calendarNow.getTime());

		schedulingTemplate = schedulingTemplateRepository.save(schedulingTemplate);
		
		LOGGER.debug("Exit updateSchedulingTemplate");
		return schedulingTemplate;
	}
	
	@Override
	public void deleteSchedulingTemplate(Long id) throws PermissionDeniedException, RessourceNotFoundException  {
		LOGGER.debug("Entry deleteSchedulingTemplate. id: " + id);
		
		SchedulingTemplate schedulingTemplate = getSchedulingTemplateFromOrganisationAndId(id);
		
		schedulingTemplate.setDeletedBy(meetingUserService.getOrCreateCurrentMeetingUser());
		Calendar calendarNow = new GregorianCalendar();
		schedulingTemplate.setDeletedTime(calendarNow.getTime());
		
		schedulingTemplateRepository.save(schedulingTemplate);
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

	private boolean checkIfPoolTemplateAlreadyExists() throws PermissionDeniedException {
		List<SchedulingTemplate> poolTemplates = schedulingTemplateRepository.findByOrganisationAndIsPoolTemplateAndDeletedTimeIsNull(organisationService.getUserOrganisation(), true);

		return poolTemplates.size() == 1;
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
