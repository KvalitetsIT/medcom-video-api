package dk.medcom.video.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.repository.OrganisationRepository;
import dk.medcom.video.api.repository.SchedulingTemplateRepository;

@Component
public class SchedulingTemplateService {

	@Autowired
	SchedulingTemplateRepository schedulingTemplateRepository;
	
	@Autowired
	OrganisationRepository organisationRepository;
	
	@Autowired
	UserContextService userService;
	
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
	
	public SchedulingTemplate getSchedulingTemplate() throws PermissionDeniedException{
		
		Organisation organisation = organisationRepository.findByOrganisationId(userService.getUserContext().getUserOrganisation());
		if (organisation != null) {
			List<SchedulingTemplate> schedulingTemplates = schedulingTemplateRepository.findByOrganisation(organisation) ;
			if (schedulingTemplates.size() > 0) {
				return schedulingTemplates.get(0);
			} else {
				schedulingTemplates = schedulingTemplateRepository.findByOrganisationIsNull();
				if (schedulingTemplates.size() > 0) {
					return schedulingTemplates.get(0);
				} else {
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
					return schedulingTemplateRepository.save(schedulingTemplate);
				}
			}
		} else {
			throw new PermissionDeniedException();
		}
		
	}

}
