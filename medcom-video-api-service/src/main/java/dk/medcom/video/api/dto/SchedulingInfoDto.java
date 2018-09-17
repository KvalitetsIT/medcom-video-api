package dk.medcom.video.api.dto;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import dk.medcom.video.api.controller.MeetingController;
import dk.medcom.video.api.controller.SchedulingInfoController;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.SchedulingInfo;


public class SchedulingInfoDto extends ResourceSupport {

	
	public String uuid;


	//public MeetingDto meetingDto; //not included intentionally - loop reference

	public Long hostPin; 		
	public Long guestPin;

	public int vmrAvailableBefore;
	public int maxParticipants;		
	
	public SchedulingInfoDto() {	
	}
	
	public SchedulingInfoDto(SchedulingInfo schedulingInfo) {
		
		uuid = schedulingInfo.getUuid();
		hostPin = schedulingInfo.getHostPin();
		guestPin = schedulingInfo.getGuestPin();
		vmrAvailableBefore = schedulingInfo.getVMRAvailableBefore();
		maxParticipants = schedulingInfo.getMaxParticipants();
		

		//String schedulingInfoId = uuid;
		try {  
			Link selfLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(uuid)).withRel("self");
			add(selfLink);
		} catch (RessourceNotFoundException | PermissionDeniedException e) {
		}
			
		try {
			Link meetingLink = linkTo(methodOn(MeetingController.class).getMeetingByUUID(uuid)).withRel("meeting");
			add(meetingLink);
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
	
	public void setmaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
	
}
