package dk.medcom.video.api.dto;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Date;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;

import dk.medcom.video.api.controller.MeetingController;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingUser;

public class MeetingDto extends ResourceSupport {

	public String subject;
	public String uuid;
	public MeetingUserDto createdBy;
	public MeetingUserDto updatedBy;
	public MeetingUserDto organizedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z") 		//Date format should be: "2018-07-12T09:00:00
	public Date startTime;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z")		//Date format should be: "2018-07-12T09:00:00
	public Date endTime;
	
	public String description;
	public String projectCode;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z") 		//Date format should be: "2018-07-12T09:00:00
	public Date createdTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z") 		//Date format should be: "2018-07-12T09:00:00
	public Date updatedTime;
	
	public MeetingDto(Meeting meeting) {
		
		subject = meeting.getSubject();
		uuid = meeting.getUuid();
		
		MeetingUser meetingUser = meeting.getMeetingUser();
		MeetingUserDto meetingUserDto = new MeetingUserDto(meetingUser);
		
		MeetingUser organizedByUser = meeting.getOrganizedByUser();
		MeetingUserDto organizedByUserDto = new MeetingUserDto(organizedByUser);
		
		MeetingUser updatedByUser = meeting.getUpdatedByUser();
		MeetingUserDto updatedByUserDto = new MeetingUserDto(updatedByUser);

		createdBy = meetingUserDto;
		organizedBy = organizedByUserDto;
		updatedBy = updatedByUserDto;
		startTime = meeting.getStartTime();
		endTime = meeting.getEndTime();
		description = meeting.getDescription();
		projectCode = meeting.getProjectCode();
		createdTime = meeting.getCreatedTime();
		updatedTime = meeting.getUpdatedTime();
		
		try { 
			Link selfLink = linkTo(methodOn(MeetingController.class).getMeetingByUUID(uuid)).withRel("self");
			add(selfLink);
		} catch (RessourceNotFoundException | PermissionDeniedException e) {
		}
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public void setCreatedBy(MeetingUserDto meetingUserDto) {
		this.createdBy =  meetingUserDto;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}
 
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public String projectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	public void setOrganizedBy(MeetingUserDto meetingUserDto) {
		this.organizedBy =  meetingUserDto;
	}
}
