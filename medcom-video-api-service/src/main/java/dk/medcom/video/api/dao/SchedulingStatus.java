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
import javax.persistence.Table;

@Entity
@Table(name = "scheduling_status")
public class SchedulingStatus {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private Date timeStamp;
	private int provisionStatus;
	
	@ManyToOne
    @JoinColumn(name="meetings_id")
	private Meeting meeting;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public int getProvisionStatus() {
		return provisionStatus;
	}
	public void setProvisionStatus(int provisionStatus) {
		this.provisionStatus = provisionStatus;
	}
	public Meeting getMeeting() {
	return meeting;
	}
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}
}
