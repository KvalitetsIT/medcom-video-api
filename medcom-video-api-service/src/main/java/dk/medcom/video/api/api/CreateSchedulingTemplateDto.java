package dk.medcom.video.api.api;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.hateoas.RepresentationModel;

public class CreateSchedulingTemplateDto extends RepresentationModel {
	@NotNull
	private Long conferencingSysId;
	
	@NotNull
	@Size(max=100, message="uriPrefix should have a maximum of 100 characters")
	private String uriPrefix;
	
	@NotNull
	@Size(max=100, message="uriDomain should have a maximum of 100 characters")
	private String uriDomain;
	
	@NotNull
	private boolean hostPinRequired;
	
	private Long hostPinRangeLow;
	
	private Long hostPinRangeHigh;
	
	@NotNull
	private boolean guestPinRequired;
	
	private Long guestPinRangeLow;
	
	private Long guestPinRangeHigh;
	
	//@NotNull - even though required by database not required in call. Will have the value 0 when not included in call.
	private int vMRAvailableBefore;
	
	//@NotNull - even though required by database not required in call. Will have the value 0 when not included in call.
	private int maxParticipants;
	
	//@NotNull - even though required by database not required in call. Will have the value false when not included in call.
	private boolean endMeetingOnEndTime;
	
	@NotNull
	private Long uriNumberRangeLow;
	
	@NotNull
	private Long uriNumberRangeHigh;
	
	@Size(max=100, message="ivrThme should have a maximum of 100 characters")
	private String ivrTheme;
	
	private boolean isDefaultTemplate;

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

	public boolean isHostPinRequired() {
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

	public boolean isGuestPinRequired() {
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

	public int getvMRAvailableBefore() {
		return vMRAvailableBefore;
	}

	public void setvMRAvailableBefore(int vMRAvailableBefore) {
		this.vMRAvailableBefore = vMRAvailableBefore;
	}

	public int getMaxParticipants() {
		return maxParticipants;
	}

	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}

	public boolean isEndMeetingOnEndTime() {
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
	
	public boolean getIsDefaultTemplate() {
		return isDefaultTemplate;
	}

	public void setIsDefaultTemplate(boolean isDefaultTemplate) {
		this.isDefaultTemplate = isDefaultTemplate;
	}
}