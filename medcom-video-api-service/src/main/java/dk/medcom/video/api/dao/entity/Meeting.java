package dk.medcom.video.api.dao.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "meetings")
public class Meeting {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private String uuid;
	private String subject;
	
	@ManyToOne
    @JoinColumn(name="organisation_id")
	private Organisation organisation;

	@ManyToOne
    @JoinColumn(name="created_by")
	private MeetingUser createdBy;
	private Date createdTime;
	
	@ManyToOne
    @JoinColumn(name="updated_by")
	private MeetingUser updatedBy;
	private Date updatedTime;
	
	@ManyToOne
    @JoinColumn(name="organized_by")
	private MeetingUser organizedBy;
	
	private Date startTime;
	private Date endTime;
	private String description;
	
	private String projectCode;

	private String shortId;

	private String externalId;

	@OneToMany(mappedBy = "meeting")
	private Set<MeetingLabel> meetingLabels = new HashSet<>();

	@Column(columnDefinition = "varchar")
	@Enumerated(EnumType.STRING)
	private GuestMicrophone guestMicrophone;

	private boolean guestPinRequired;

	@OneToMany(mappedBy = "meeting")
	private Set<MeetingAdditionalInfo> meetingAdditionalInfo = new HashSet<>();

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
	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
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
	public MeetingUser getUpdatedByUser() {
		return updatedBy;
	}
	public void setUpdatedByUser(MeetingUser meetingUser) {
		this.updatedBy = meetingUser;
	}
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Set<MeetingLabel> getMeetingLabels() {
		return meetingLabels;
	}

	public void setMeetingLabels(Set<MeetingLabel> meetingLabels) {
		this.meetingLabels = meetingLabels;
	}

	public void addMeetingLabel(MeetingLabel meetingLabel) {
		meetingLabel.setMeeting(this);
		meetingLabels.add(meetingLabel);
	}

	public String getShortId() {
		return shortId;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public GuestMicrophone getGuestMicrophone() {
		return guestMicrophone;
	}

	public void setGuestMicrophone(GuestMicrophone guestMicrophone) {
		this.guestMicrophone = guestMicrophone;
	}

	public boolean getGuestPinRequired() {
		return guestPinRequired;
	}

	public void setGuestPinRequired(boolean guestPinRequired) {
		this.guestPinRequired = guestPinRequired;
	}

	public Set<MeetingAdditionalInfo> getMeetingAdditionalInfo() {
		return meetingAdditionalInfo;
	}

	public void setMeetingAdditionalInfo(Set<MeetingAdditionalInfo> meetingAdditionalInformation) {
		this.meetingAdditionalInfo = meetingAdditionalInformation;
	}

	public void addMeetingAdditionalInformation(MeetingAdditionalInfo meetingAdditionalInformation) {
		meetingAdditionalInformation.setMeeting(this);
		meetingAdditionalInfo.add(meetingAdditionalInformation);
	}
}
