package dk.medcom.video.api.dto;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import dk.medcom.video.api.controller.SchedulingTemplateController;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.SchedulingTemplate;

public class SchedulingTemplateDto extends ResourceSupport {
	@JsonProperty("id")
	private Long templateId;
	public String organisationId;
	private Long conferencingSysId;			
	private String uriPrefix;  				
	private String uriDomain;  				
	private boolean hostPinRequired;
	private Long hostPinRangeLow; 			
	private Long hostPinRangeHigh; 			
	private boolean guestPinRequired;
	private Long guestPinRangeLow;			
	private Long guestPinRangeHigh;			
	private int vMRAvailableBefore;			
	private int maxParticipants;			
	private boolean endMeetingOnEndTime;	
	private Long uriNumberRangeLow;			
	private Long uriNumberRangeHigh;		
	private String ivrTheme;

	private boolean isDefaultTemplate;

	public SchedulingTemplateDto() {	
	}
	
	public SchedulingTemplateDto(SchedulingTemplate schedulingTemplate) throws PermissionDeniedException {
		
		templateId = schedulingTemplate.getId();
		organisationId = schedulingTemplate.getOrganisation().getOrganisationId();
		conferencingSysId = schedulingTemplate.getConferencingSysId();
		uriPrefix = schedulingTemplate.getUriPrefix();
		uriDomain = schedulingTemplate.getUriDomain();
		hostPinRequired = schedulingTemplate.getHostPinRequired();
		hostPinRangeLow = schedulingTemplate.getHostPinRangeLow();
		hostPinRangeHigh = schedulingTemplate.getHostPinRangeHigh();
		guestPinRequired = schedulingTemplate.getHostPinRequired();
		guestPinRangeLow = schedulingTemplate.getGuestPinRangeLow();
		guestPinRangeHigh = schedulingTemplate.getGuestPinRangeHigh();
		vMRAvailableBefore = schedulingTemplate.getVMRAvailableBefore();
		maxParticipants = schedulingTemplate.getMaxParticipants();
		endMeetingOnEndTime = schedulingTemplate.getEndMeetingOnEndTime();
		uriNumberRangeLow = schedulingTemplate.getUriNumberRangeLow();
		uriNumberRangeHigh = schedulingTemplate.getUriNumberRangeHigh();
		ivrTheme = schedulingTemplate.getIvrTheme();
		isDefaultTemplate = schedulingTemplate.getIsDefaultTemplate();
		
		try { //does not make sense to throw since its only a link 
			Link selfLink = linkTo(methodOn(SchedulingTemplateController.class).getSchedulingTemplateById(templateId)).withRel("self");
	
			add(selfLink);
		} catch (RessourceNotFoundException e) {
		}

	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long id) {
		this.templateId = id;
	}

	public String getOrganisationId() {
		return organisationId;
	}

	public void setOrganisationId(String organisationId) {
		this.organisationId = organisationId;
	}

	public Long getConferencingSysId() {
		return conferencingSysId;
	}

	public void setConferencingSysId(Long conferencingSysId) {
		this.conferencingSysId = conferencingSysId;
	}

	public String getUriPrefix() {
		return uriPrefix;
	}

	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

	public String getUriDomain() {
		return uriDomain;
	}

	public void setUriDomain(String uriDomain) {
		this.uriDomain = uriDomain;
	}

	public boolean isHostPinRequired() {
		return hostPinRequired;
	}

	public void setHostPinRequired(boolean hostPinRequired) {
		this.hostPinRequired = hostPinRequired;
	}

	public Long getHostPinRangeLow() {
		return hostPinRangeLow;
	}

	public void setHostPinRangeLow(Long hostPinRangeLow) {
		this.hostPinRangeLow = hostPinRangeLow;
	}

	public Long getHostPinRangeHigh() {
		return hostPinRangeHigh;
	}

	public void setHostPinRangeHigh(Long hostPinRangeHigh) {
		this.hostPinRangeHigh = hostPinRangeHigh;
	}

	public boolean isGuestPinRequired() {
		return guestPinRequired;
	}

	public void setGuestPinRequired(boolean guestPinRequired) {
		this.guestPinRequired = guestPinRequired;
	}

	public Long getGuestPinRangeLow() {
		return guestPinRangeLow;
	}

	public void setGuestPinRangeLow(Long guestPinRangeLow) {
		this.guestPinRangeLow = guestPinRangeLow;
	}

	public Long getGuestPinRangeHigh() {
		return guestPinRangeHigh;
	}

	public void setGuestPinRangeHigh(Long guestPinRangeHigh) {
		this.guestPinRangeHigh = guestPinRangeHigh;
	}

	public int getvMRAvailableBefore() {
		return vMRAvailableBefore;
	}

	public void setvMRAvailableBefore(int vMRAvailableBefore) {
		this.vMRAvailableBefore = vMRAvailableBefore;
	}

	public int getMaxParticipants() {
		return maxParticipants;
	}

	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}

	public boolean isEndMeetingOnEndTime() {
		return endMeetingOnEndTime;
	}

	public void setEndMeetingOnEndTime(boolean endMeetingOnEndTime) {
		this.endMeetingOnEndTime = endMeetingOnEndTime;
	}

	public Long getUriNumberRangeLow() {
		return uriNumberRangeLow;
	}

	public void setUriNumberRangeLow(Long uriNumberRangeLow) {
		this.uriNumberRangeLow = uriNumberRangeLow;
	}

	public Long getUriNumberRangeHigh() {
		return uriNumberRangeHigh;
	}

	public void setUriNumberRangeHigh(Long uriNumberRangeHigh) {
		this.uriNumberRangeHigh = uriNumberRangeHigh;
	}

	public String getIvrTheme() {
		return ivrTheme;
	}

	public void setIvrTheme(String ivrTheme) {
		this.ivrTheme = ivrTheme;
	}

	public boolean getIsDefaultTemplate() {
		return isDefaultTemplate;
	}

	public void setIsDefaultTemplate(boolean isDefaultTemplate) {
		this.isDefaultTemplate = isDefaultTemplate;
	}
}