package dk.medcom.video.api.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonFormat;


public class CreateMeetingDto {

	@NotNull
	@Size(max=100, message="subject should have a maximum of 100 characters")
	public String subject;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z")
	public Date startTime;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z")
	public Date endTime;
	
	@Size(max=500, message="description should have a maximum of 500 characters")
	public String description;
	
	@Size(max=100, message="Project Code should have a maximum of 100 characters")
	public String projectCode;
	
	@Email
	public String organizedByEmail;
	
	public int maxParticipants;
	
	public Boolean endMeetingOnEndTime;
	
	private Long schedulingTemplateId;

	
	public int getMaxParticipants() {
		return maxParticipants;
	}

	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}

	public Boolean isEndMeetingOnEndTime() {
		return endMeetingOnEndTime;
	}

	public void setEndMeetingOnEndTime(Boolean endMeetingOnEndTime) {
		this.endMeetingOnEndTime = endMeetingOnEndTime;
	}

	public Long getSchedulingTemplateId() {
		return schedulingTemplateId;
	}

	public void setSchedulingTemplateId(Long schedulingTemplateId) {
		this.schedulingTemplateId = schedulingTemplateId;
	}


	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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
	
	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	public String getOrganizedByEmail() {
		return organizedByEmail;
	}

	public void setOrganizedByEmail(String organizedByEmail) {
		this.organizedByEmail = organizedByEmail;
	}

}
