package dk.medcom.video.api.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "scheduling_template")
public class SchedulingTemplate {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name="organisation_id")
	private Organisation organisation;
	
	private Long conferencingSysId;			//id on conference system
	private String uriPrefix;  				//prefix before uri @
	private String uriDomain;  				//domain after uri @
	private boolean hostPinRequired;
	private Long hostPinRangeLow; 			//when random generating
	private Long hostPinRangeHigh; 			//when random generating
	private boolean guestPinRequired;
	private Long guestPinRangeLow;			//when random generating
	private Long guestPinRangeHigh;			//when random generating
	private int vMRAvailableBefore;			//how many minutes before meeting should the meeting room be availabe
	private int maxParticipants;			//Locked when max i reached
	private boolean endMeetingOnEndTime;	//If true users are kicked from the meeting when it ends
	private Long uriNumberRangeLow;			//when random generating
	private Long uriNumberRangeHigh;		//when random generating
	private String ivrTheme;  				//theme to use in Pexip
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Organisation getOrganisation() {
		return organisation;
	}
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	public Long getConferencingSysId() {
		return conferencingSysId;
	}
	public void setConferencingSysId(Long conferencingSysId) {
		this.conferencingSysId = conferencingSysId;
	}
	public String getUriPrefix() {
		return uriPrefix;
	}
	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}
	public String getUriDomain() {
		return uriDomain;
	}
	public void setUriDomain(String uriDomain) {
		this.uriDomain = uriDomain;
	}
	public boolean getHostPinRequired() {
		return hostPinRequired;
	}
	public void setHostPinRequired(boolean hostPinRequired) {
		this.hostPinRequired = hostPinRequired;
	}
	public Long getHostPinRangeLow() {
		return hostPinRangeLow;
	}
	public void setHostPinRangeLow(Long hostPinRangeLow) {
		this.hostPinRangeLow = hostPinRangeLow;
	}
	public Long getHostPinRangeHigh() {
		return hostPinRangeHigh;
	}
	public void setHostPinRangeHigh(Long hostPinRangeHigh) {
		this.hostPinRangeHigh = hostPinRangeHigh;
	}
	public boolean getGuestPinRequired() {
		return guestPinRequired;
	}
	public void setGuestPinRequired(boolean guestPinRequired) {
		this.guestPinRequired = guestPinRequired;
	}
	public Long getGuestPinRangeLow() {
		return guestPinRangeLow;
	}
	public void setGuestPinRangeLow(Long guestPinRangeLow) {
		this.guestPinRangeLow = guestPinRangeLow;
	}
	public Long getGuestPinRangeHigh() {
		return guestPinRangeHigh;
	}
	public void setGuestPinRangeHigh(Long guestPinRangeHigh) {
		this.guestPinRangeHigh = guestPinRangeHigh;
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
	public boolean getEndMeetingOnEndTime() {
		return endMeetingOnEndTime;
	}
	public void setEndMeetingOnEndTime(boolean endMeetingOnEndTime) {
		this.endMeetingOnEndTime = endMeetingOnEndTime;
	}
	public Long getUriNumberRangeLow() {
		return uriNumberRangeLow;
	}
	public void setUriNumberRangeLow(Long uriNumberRangeLow) {
		this.uriNumberRangeLow = uriNumberRangeLow;
	}
	public Long getUriNumberRangeHigh() {
		return uriNumberRangeHigh;
	}
	public void setUriNumberRangeHigh(Long uriNumberRangeHigh) {
		this.uriNumberRangeHigh = uriNumberRangeHigh;
	}
	public String getIvrTheme() {
		return ivrTheme;
	}
	public void setIvrTheme(String ivrTheme) {
		this.ivrTheme = ivrTheme;
	}
	
	@Override
	public String toString() {
		return "SchedulingTemplate [id=" + id + ", organisation=" + organisation + ", conferencingSysId="
				+ conferencingSysId + ", uriPrefix=" + uriPrefix + ", uriDomain=" + uriDomain + ", hostPinRequired="
				+ hostPinRequired + ", hostPinRangeLow=" + hostPinRangeLow + ", hostPinRangeHigh=" + hostPinRangeHigh
				+ ", guestPinRequired=" + guestPinRequired + ", guestPinRangeLow=" + guestPinRangeLow
				+ ", guestPinRangeHigh=" + guestPinRangeHigh + ", vMRAvailableBefore=" + vMRAvailableBefore
				+ ", maxParticipants=" + maxParticipants + ", endMeetingOnEndTime=" + endMeetingOnEndTime
				+ ", uriNumberRangeLow=" + uriNumberRangeLow + ", uriNumberRangeHigh=" + uriNumberRangeHigh
				+ ", ivrTheme=" + ivrTheme + "]";
	}
}
