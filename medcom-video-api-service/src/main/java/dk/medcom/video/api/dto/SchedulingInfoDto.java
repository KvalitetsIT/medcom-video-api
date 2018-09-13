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

	//TODO Lene: pin skal da vel ikke med ud...?
	//public Long hostPin; 		
	//public Long guestPin;

	public int vMRAvailableBefore;
	public int maxParticipants;		
	
	public SchedulingInfoDto(SchedulingInfo schedulingInfo) {
		
		uuid = schedulingInfo.getUuid();
		vMRAvailableBefore = schedulingInfo.getVMRAvailableBefore();
		maxParticipants = schedulingInfo.getMaxParticipants();
		

		//String schedulingInfoId = uuid;
		try {  
			Link selfLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(uuid)).withRel("self");
			add(selfLink);
		} catch (RessourceNotFoundException e) {
		} catch (PermissionDeniedException e) {
		}
			
		try {
			Link meetingLink = linkTo(methodOn(MeetingController.class).getMeetingByUUID(uuid)).withRel("meeting");
			add(meetingLink);
		} catch (RessourceNotFoundException e) {			
		} catch (PermissionDeniedException e) {
		}
		
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public int getVMRAvailableBefore() {
		return vMRAvailableBefore;
	}
 
	public void setVMRAvailableBefore(int vMRAvailableBefore) {
		this.vMRAvailableBefore = vMRAvailableBefore;
	}
	
	public int getMaxParticipants() {
		return maxParticipants;
	}
	
	public void setmaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
	
}
