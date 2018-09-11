package dk.medcom.video.api.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

	private int vMRAvailableBefore;		//how many minutes before meeting should the meeting room be available
	private int maxParticipants;		//Locked when max is reached
	
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
	public int getMaxParticipants() {
		return maxParticipants;
	}
	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}
	public Meeting getMeeting() {
	return meeting;
	}
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}
}
