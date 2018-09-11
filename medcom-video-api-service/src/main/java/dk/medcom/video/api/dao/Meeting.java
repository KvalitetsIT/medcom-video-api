//TODO Lene: SPØRGSMÅL: skulle der laves link til SchedulingTemplate fra SchedulignInfo eller?
package dk.medcom.video.api.dao;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "meetings")
public class Meeting {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String uuid;
	private String subject;
	private String organisationId;

	@ManyToOne
    @JoinColumn(name="created_by")
	private MeetingUser createdBy;
	
	private Date startTime;
	private Date endTime;
	private String description;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getOrganisationId() {
		return organisationId;
	}

	public void setOrganisationId(String organisationId) {
		this.organisationId = organisationId;
	}
	
	public MeetingUser getMeetingUser() {
		return createdBy;
	}

	public void setMeetingUser(MeetingUser meetingUser) {
		this.createdBy = meetingUser;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
