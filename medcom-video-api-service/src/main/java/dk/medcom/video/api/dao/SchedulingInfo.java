package dk.medcom.video.api.dao;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "scheduling_info")
public class SchedulingInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String uuid;
	private Long hostPin; 		
	private Long guestPin;

	private int vMRAvailableBefore;			//how many minutes before meeting should the meeting room be available
	private Date vMRStartTime;          	//meeting startTime with vMRAvailableBefore subtracted
	private int maxParticipants;			//Locked when max is reached
	private boolean endMeetingOnEndTime;	//If true users are kicked from the meeting when it ends
	
	private String uriWithDomain;		
	private String uriWithoutDomain;	//uri before the @
	
	@ManyToOne
	@JoinColumn(name="scheduling_template_id")
	private SchedulingTemplate schedulingTemplate;
	
	private int provisionStatus;
	private Date provisionTimestamp;
	private String provisionVMRId;
	
	@OneToOne
	@JoinColumn(name="meetings_id")
	private Meeting meeting;
	
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
	public int getVMRAvailableBefore() {
		return vMRAvailableBefore;
	}
	public void setVMRAvailableBefore(int vMRAvailableBefore) {
		this.vMRAvailableBefore = vMRAvailableBefore;
	}
	public Date getvMRStartTime() {
		return vMRStartTime;
	}
	public void setvMRStartTime(Date vMRStartTime) {
		this.vMRStartTime = vMRStartTime;
	}
	public int getMaxParticipants() {
		return maxParticipants;
	}
	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
	public boolean getEndMeetingOnEndTime() {
		return endMeetingOnEndTime;
	}
	public void setEndMeetingOnEndTime(boolean endMeetingOnEndTime) {
		this.endMeetingOnEndTime = endMeetingOnEndTime;
	}
	public Meeting getMeeting() {
	return meeting;
	}
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}
	public String getUriWithDomain() {
		return uriWithDomain;
	}
	public void setUriWithDomain(String uriWithDomain) {
		this.uriWithDomain = uriWithDomain;
	}
	public String getUriWithoutDomain() {
		return uriWithoutDomain;
	}
	public void setUriWithoutDomain(String uriWithoutDomain) {
		this.uriWithoutDomain = uriWithoutDomain;
	}
	public SchedulingTemplate getSchedulingTemplate() {
		return schedulingTemplate;
	}
	public void setSchedulingTemplate(SchedulingTemplate schedulingTemplate) {
		this.schedulingTemplate = schedulingTemplate;
	}
	public int getProvisionStatus() {
		return provisionStatus;
	}
	public void setProvisionStatus(int provisionStatus) {
		this.provisionStatus = provisionStatus;
	}
	public Date getProvisionTimestamp() {
		return provisionTimestamp;
	}
	public void setProvisionTimestamp(Date provisionTimestamp) {
		this.provisionTimestamp = provisionTimestamp;
	}
	public String getProvisionVMRId() {
		return provisionVMRId;
	}
	public void setProvisionVMRId(String provisionVMRId) {
		this.provisionVMRId = provisionVMRId;
	}
	
}
