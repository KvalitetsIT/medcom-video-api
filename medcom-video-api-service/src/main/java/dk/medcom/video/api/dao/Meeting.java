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
	
	@ManyToOne
    @JoinColumn(name="organisation_id")
	private Organisation organisation;

	@ManyToOne
    @JoinColumn(name="created_by")
	private MeetingUser createdBy;
	
	@ManyToOne
    @JoinColumn(name="organized_by")
	private MeetingUser organizedBy;
	
	private Date startTime;
	private Date endTime;
	private String description;
	
	private String projectCode;
	
	
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

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
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
	public MeetingUser getOrganizedByUser() {
		return organizedBy;
	}

	public void setOrganizedByUser(MeetingUser meetingUser) {
		this.organizedBy = meetingUser;
	}
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


}
