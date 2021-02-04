package dk.medcom.video.api.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateMeetingDto {

	@NotNull
	@Size(max=100, message="subject should have a maximum of 100 characters")
	public String subject;
	
	@NotNull
	@JsonDeserialize(using = CustomDateDeserializer.class)
	public Date startTime;

	@NotNull
	@JsonDeserialize(using = CustomDateDeserializer.class)
	public Date endTime;
	
	@Size(max=500, message="description should have a maximum of 500 characters")
	public String description;
	
	@Size(max=100, message="Project Code should have a maximum of 100 characters")
	public String projectCode;
	
	public String organizedByEmail;
	private List<String> labels = new ArrayList<>();

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

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<String> getLabels() {
		return labels;
	}
}
