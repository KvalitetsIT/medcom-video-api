package dk.medcom.video.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dk.medcom.video.api.controller.MeetingController;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingLabel;
import dk.medcom.video.api.dao.MeetingUser;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MeetingDto extends RepresentationModel {

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
	private List<String> labels;
	private String shortId;

	public MeetingDto() {
		// Empty constructor
	}

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
		shortId = meeting.getShortId();

		labels = meeting.getMeetingLabels().stream().map(MeetingLabel::getLabel).collect(Collectors.toList());

		try { 
			Link selfLink = linkTo(methodOn(MeetingController.class).getMeetingByUUID(uuid)).withRel("self");
			add(selfLink);
		} catch (RessourceNotFoundException | PermissionDeniedException e) {
			// Empty
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

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public String getShortId() {
		return shortId;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
	}
}
