package dk.medcom.video.api.dto;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Date;

import dk.medcom.video.api.controller.SchedulingInfoController;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingInfo;



public class SchedulingInfoDto extends ResourceSupport {

	
	public String uuid;
	public Long hostPin; 		
	public Long guestPin;
	public int vmrAvailableBefore;
	public int maxParticipants;
	public boolean endMeetingOnEndTime;
	private String uriWithDomain;		
	private String uriWithoutDomain;
	private ProvisionStatus provisionStatus;
	private String provisionStatusDescription;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z")		//Date format should be: "2018-07-12T09:00:00
	private Date provisionTimestamp;
	private String provisionVmrId;
	
	public MeetingDto meetingDetails;
	
	public SchedulingInfoDto() {	
	}
	
	public SchedulingInfoDto(SchedulingInfo schedulingInfo) {
		
		uuid = schedulingInfo.getUuid();
		hostPin = schedulingInfo.getHostPin();
		guestPin = schedulingInfo.getGuestPin();
		vmrAvailableBefore = schedulingInfo.getVMRAvailableBefore();
		maxParticipants = schedulingInfo.getMaxParticipants();
		endMeetingOnEndTime = schedulingInfo.getEndMeetingOnEndTime();
		uriWithDomain = schedulingInfo.getUriWithDomain();
		uriWithoutDomain = schedulingInfo.getUriWithoutDomain();
		provisionStatus = schedulingInfo.getProvisionStatus();
		provisionStatusDescription = schedulingInfo.getProvisionStatusDescription();
		provisionTimestamp = schedulingInfo.getProvisionTimestamp();	
		provisionVmrId = schedulingInfo.getProvisionVMRId();
		
		Meeting meeting = schedulingInfo.getMeeting();
		meetingDetails = new MeetingDto(meeting);

		try {  
			Link selfLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(uuid)).withRel("self");
			add(selfLink);
			
		} catch (RessourceNotFoundException | PermissionDeniedException e) {
		}
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Long getHostPin() {
		return hostPin;
	}
	public void setHostPin(Long hostPin) {
		this.hostPin = hostPin;
	}
	public Long getGuestPin() {
		return guestPin;
	}
	public void setGuestPin(Long guestPin) {
		this.guestPin = guestPin;
	}

	public int getVmrAvailableBefore() {
		return vmrAvailableBefore;
	}
 
	public void setVmrAvailableBefore(int vmrAvailableBefore) {
		this.vmrAvailableBefore = vmrAvailableBefore;
	}
	
	public int getMaxParticipants() {
		return maxParticipants;
	}
	
	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
	public boolean getEndMeetingOnEndTime() {
		return endMeetingOnEndTime;
	}
	
	public void setEndMeetingOnEndTime(boolean endMeetingOnEndTime) {
		this.endMeetingOnEndTime = endMeetingOnEndTime;
	}
	public String getUriWithDomain() {
		return uriWithDomain;
	}

	public void setUriWithDomain(String uriWithDomain) {
		this.uriWithDomain = uriWithDomain;
	}

	public String getUriWithoutDomain() {
		return uriWithoutDomain;
	}

	public void setUriWithoutDomain(String uriWithoutDomain) {
		this.uriWithoutDomain = uriWithoutDomain;
	}

	public ProvisionStatus getProvisionStatus() {
		return provisionStatus;
	}

	public void setProvisionStatus(ProvisionStatus provisionStatus) {
		this.provisionStatus = provisionStatus;
	}
	public String getProvisionStatusDescription() {
		return provisionStatusDescription;
	}

	public void setProvisionStatusDescription(String provisionStatusDescription) {
		this.provisionStatusDescription = provisionStatusDescription;
	}

	public Date getProvisionTimestamp() {
		return provisionTimestamp;
	}

	public void setProvisionTimestamp(Date provisionTimestamp) {
		this.provisionTimestamp = provisionTimestamp;
	}

	public String getProvisionVmrId() {
		return provisionVmrId;
	}

	public void setProvisionVmrId(String provisionVmrId) {
		this.provisionVmrId = provisionVmrId;
	}
	
}