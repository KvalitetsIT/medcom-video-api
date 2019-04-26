package dk.medcom.video.api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.CreateSchedulingTemplateDto;
import dk.medcom.video.api.dto.UpdateSchedulingTemplateDto;
import dk.medcom.video.api.repository.OrganisationRepository;
import dk.medcom.video.api.repository.SchedulingTemplateRepository;

@Component
public class SchedulingTemplateService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoService.class);

	@Autowired
	SchedulingTemplateRepository schedulingTemplateRepository;
	
	@Autowired
	UserContextService userService;
	
	@Autowired
	OrganisationService organisationService;
	
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
	
	public SchedulingTemplateService() {
		
	}
	
	public SchedulingTemplateService(SchedulingTemplateRepository schedulingTemplateRepository, UserContextService userService, OrganisationService organisationService) {
	 	this.schedulingTemplateRepository = schedulingTemplateRepository;
	 	this.userService = userService;
	 	this.organisationService = organisationService;
	}

	
	public SchedulingTemplate getSchedulingTemplate() throws PermissionDeniedException{
		
		Organisation organisation = organisationService.getUserOrganisation();
		List<SchedulingTemplate> schedulingTemplates = null;
		if (organisation != null) {
			//first try: find default template for organization. Use list just in case.
			schedulingTemplates = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplate(organisation, true);
			if (schedulingTemplates.size() > 0) {
				LOGGER.debug("Template found using default organization template: " + schedulingTemplates.get(0).toString());
				return schedulingTemplates.get(0);
			}
			//second try: find other organization template. 			
			schedulingTemplates = schedulingTemplateRepository.findByOrganisation(organisation) ; //TODO: order by id eller væk
			if (schedulingTemplates.size() > 0) { 
				LOGGER.debug("Template found using organization template: " + schedulingTemplates.get(0).toString());
				return schedulingTemplates.get(0);
			}
			
			//third try: find shared default template, where organization is null
			schedulingTemplates = schedulingTemplateRepository.findByOrganisationIsNull();
			if (schedulingTemplates.size() > 0) {
				LOGGER.debug("Template found using shared default template: " + schedulingTemplates.get(0).toString());
				return schedulingTemplates.get(0);
			}
			
			//if none of above is found - create teh shared default template
			SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
			//schedulingTemplate.setOrganisation(organisation); //for default schedulingTemplate the organisation is null
			schedulingTemplate.setConferencingSysId(conferencingSysId);
			schedulingTemplate.setUriPrefix(uriPrefix);
			schedulingTemplate.setUriDomain(uriDomain);
			schedulingTemplate.setHostPinRequired(hostPinRequired);
			schedulingTemplate.setHostPinRangeLow(hostPinRangeLow);
			schedulingTemplate.setHostPinRangeHigh(hostPinRangeHigh);
			schedulingTemplate.setGuestPinRequired(guestPinRequired);
			schedulingTemplate.setGuestPinRangeLow(guestPinRangeLow);
			schedulingTemplate.setGuestPinRangeHigh(guestPinRangeHigh);
			schedulingTemplate.setVMRAvailableBefore(vMRAvailableBefore);
			schedulingTemplate.setMaxParticipants(maxParticipants);
			schedulingTemplate.setEndMeetingOnEndTime(endMeetingOnEndTime);
			schedulingTemplate.setUriNumberRangeLow(uriNumberRangeLow);
			schedulingTemplate.setUriNumberRangeHigh(uriNumberRangeHigh);
			schedulingTemplate.setIvrTheme(ivrTheme);;
			LOGGER.debug("Creating default schedulingTemplate: " + schedulingTemplate.toString());
			return schedulingTemplateRepository.save(schedulingTemplate);

		} else {
			throw new PermissionDeniedException();
		}
		
	}
	public SchedulingTemplate getSchedulingTemplateFromOrganisation(long schedulingTemplateId) throws PermissionDeniedException {
		
		Organisation organisation = organisationService.getUserOrganisation();
		if (organisation != null) {
			SchedulingTemplate schedulingTemplates = schedulingTemplateRepository.findOne(schedulingTemplateId) ;
			if (organisation.equals(schedulingTemplates.getOrganisation())) {
				return schedulingTemplates;
			}
		}
		return null;
	}
	
	public SchedulingTemplate getSchedulingTemplateFromOrganisationAndId(Long schedulingTemplateId) throws PermissionDeniedException, RessourceNotFoundException {
		SchedulingTemplate schedulingTemplate = schedulingTemplateRepository.findByOrganisationAndId(organisationService.getUserOrganisation(), schedulingTemplateId);
		
		if (schedulingTemplate == null) {
			LOGGER.debug("scheduleTemplate not found. Id: " + schedulingTemplateId + ". Organisation: " + organisationService.getUserOrganisation().toString());
			throw new RessourceNotFoundException("schedulingTemplate", "id");
		}
		return schedulingTemplate;
	}
	
	public List<SchedulingTemplate> getSchedulingTemplates() throws PermissionDeniedException  {
		return schedulingTemplateRepository.findByOrganisation(organisationService.getUserOrganisation()) ;
	}

	public SchedulingTemplate createSchedulingTemplate(CreateSchedulingTemplateDto createSchedulingTemplateDto) throws PermissionDeniedException  {
		LOGGER.debug("Entry createSchedulingTemplate");
		SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
		
		//first find any other default template and make it non-default 
		if (createSchedulingTemplateDto.getIsDefaultTemplate()) {
			if (!checkIfDefaultTemplateExistAndRemove()) {
				throw new PermissionDeniedException();
			}
		}
		
		//then create the new template
		schedulingTemplate.setOrganisation(organisationService.getUserOrganisation());
		schedulingTemplate.setConferencingSysId(createSchedulingTemplateDto.getConferencingSysId());
		schedulingTemplate.setUriPrefix(createSchedulingTemplateDto.getUriPrefix());
		schedulingTemplate.setUriDomain(createSchedulingTemplateDto.getUriDomain());
		schedulingTemplate.setHostPinRequired(createSchedulingTemplateDto.isHostPinRequired());
		schedulingTemplate.setHostPinRangeLow(createSchedulingTemplateDto.getHostPinRangeLow());
		schedulingTemplate.setHostPinRangeHigh(createSchedulingTemplateDto.getHostPinRangeHigh());
		schedulingTemplate.setGuestPinRequired(createSchedulingTemplateDto.isGuestPinRequired());
		schedulingTemplate.setGuestPinRangeLow(createSchedulingTemplateDto.getGuestPinRangeLow());
		schedulingTemplate.setGuestPinRangeHigh(createSchedulingTemplateDto.getGuestPinRangeHigh());
		schedulingTemplate.setVMRAvailableBefore(createSchedulingTemplateDto.getvMRAvailableBefore());
		schedulingTemplate.setMaxParticipants(createSchedulingTemplateDto.getMaxParticipants());
		schedulingTemplate.setEndMeetingOnEndTime(createSchedulingTemplateDto.isEndMeetingOnEndTime());
		schedulingTemplate.setUriNumberRangeLow(createSchedulingTemplateDto.getUriNumberRangeLow());
		schedulingTemplate.setUriNumberRangeHigh(createSchedulingTemplateDto.getUriNumberRangeHigh());
		schedulingTemplate.setIvrTheme(createSchedulingTemplateDto.getIvrTheme());
		schedulingTemplate.setIsDefaultTemplate(createSchedulingTemplateDto.getIsDefaultTemplate());
				
		schedulingTemplate = schedulingTemplateRepository.save(schedulingTemplate);
		
		LOGGER.debug("Exit createSchedulingTemplate");
		return schedulingTemplate;
	}
	
	public SchedulingTemplate updateSchedulingTemplate(Long id, UpdateSchedulingTemplateDto updateSchedulingTemplateDto) throws PermissionDeniedException, RessourceNotFoundException  {
		LOGGER.debug("Entry updateSchedulingTemplate. id/updateSchedulingTemplateDto. id=" + id);
		
		SchedulingTemplate schedulingTemplate = getSchedulingTemplateFromOrganisationAndId(id);

		//first find any other default template and make it non-default 
		if (updateSchedulingTemplateDto.getIsDefaultTemplate() && !schedulingTemplate.getIsDefaultTemplate() ) {
			if (!checkIfDefaultTemplateExistAndRemove()) {
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

		schedulingTemplate = schedulingTemplateRepository.save(schedulingTemplate);
		
		LOGGER.debug("Exit updateSchedulingTemplate");
		return schedulingTemplate;
	}

	private boolean checkIfDefaultTemplateExistAndRemove() throws PermissionDeniedException {
		List<SchedulingTemplate> schedulingTemplates = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplate(organisationService.getUserOrganisation(), true); 
		//cannot return "same" record as being return, because compare on	 isDefaultTemplate has been made in call method. 	

		if (schedulingTemplates.size() == 0) {
			LOGGER.debug("No existing default template exist for organization: " + organisationService.getUserOrganisation().toString());
			return true;
		}
		
		for (SchedulingTemplate schedulingTemplate : schedulingTemplates) {
			schedulingTemplate.setIsDefaultTemplate(false);
			schedulingTemplateRepository.save(schedulingTemplate);
			LOGGER.debug("Changed default template not to be default: " + schedulingTemplate.toString());
		}
		
		//double check just to be sure that the reset was completed
		schedulingTemplates = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplate(organisationService.getUserOrganisation(), true);	

		if (schedulingTemplates.size() == 0) {
			LOGGER.debug("Existing default template has been reset as non-default for organization: " + organisationService.getUserOrganisation().toString());
			return true;
		}
		
		//we have not been able to reset existing template and hence cannot add a new default
		LOGGER.debug("Existing default templates prevent adding a new one. Organisation : " + organisationService.getUserOrganisation().toString());
		return false;
	}
	
}
