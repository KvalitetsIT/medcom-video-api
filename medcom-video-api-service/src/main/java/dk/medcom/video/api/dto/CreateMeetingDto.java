package dk.medcom.video.api.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CreateMeetingDto {

	@NotNull
	@Size(max=100, message="subject should have a maximum of 100 characters")
	public String subject;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	public Date startTime;


	//TODO Lene .- remove @DateTimeFormat(pattern="MM/dd/yyyy")
	//TODO Lene - VALIDERING: hvis forkert format sendes ind, så returneres en fejl
	//TODO Lene - VALIDERING: hvis required felt er tom, så fejl. okay fejlhåndtering?
	
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	public Date endTime;
	
	@Size(max=500, message="description should have a maximum of 500 characters")
	public String description;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription() {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime() {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime() {
		this.endTime = endTime;
	}
}
