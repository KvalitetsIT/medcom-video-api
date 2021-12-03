package dk.medcom.video.api.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class CreateMeetingDto {

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

	private UUID schedulingInfoReservationId;

	private String organizedByEmail;

	public int maxParticipants;

	public Boolean endMeetingOnEndTime;

	private Long schedulingTemplateId;

	private MeetingType meetingType = MeetingType.NORMAL;

	private UUID uuid;

	private List<String> labels = new ArrayList<>();

	@Size(max = 200, message = "externalId should have a maximum of 200 characters.")
	private String externalId;

	private GuestMicrophone guestMicrophone;

	private boolean guestPinRequired;

	private VmrType vmrType;
	private ViewType hostView;
	private ViewType guestView;
	private VmrQuality vmrQuality;
	private Boolean enableOverlayText;
	private Boolean guestsCanPresent;
	private Boolean forcePresenterIntoMain;
	private Boolean forceEncryption;
	private Boolean muteAllGuests;

	@Size(max = 100, message = "uriWithoutDomain should have a maximum length of 100 characters.")
	private String uriWithoutDomain;

	@Max(value = 999999999, message = "hostPin should not be larger than 999999999.")
	@Min(value = 1000, message = "hostPin should not be less than 1000.")
	private Integer hostPin;

	@Max(value = 999999999, message = "guestPin should not be larger than 999999999.")
	@Min(value = 1000, message = "guestPin should not be less than 1000.")
	private Integer guestPin;

	public void setDefaults(){
        if (this.vmrType == null){
            this.vmrType = VmrType.conference;
        }
		if (this.hostView == null){
            this.hostView = ViewType.one_main_seven_pips;
        }
        if (this.guestView == null ){
            this.guestView = ViewType.one_main_seven_pips;
        }
        if (this.vmrQuality == null){
            this.vmrQuality = VmrQuality.hd;
        }
        if (this.enableOverlayText == null){
            this.enableOverlayText = true;
        }
        if (this.guestsCanPresent == null){
            this.guestsCanPresent = true;
        }
		if (this.forcePresenterIntoMain == null){
            this.forcePresenterIntoMain = true;
        }
        if (this.forceEncryption == null ){
            this.forceEncryption = false;
        }
        if (this.muteAllGuests ==null){
            this.muteAllGuests = false;
        }
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public int getMaxParticipants() {
		return maxParticipants;
	}

	public void setMaxParticipants(int maxParticipants) {
		this.maxParticipants = maxParticipants;
	}

	public Boolean isEndMeetingOnEndTime() {
		return endMeetingOnEndTime;
	}

	public void setEndMeetingOnEndTime(Boolean endMeetingOnEndTime) {
		this.endMeetingOnEndTime = endMeetingOnEndTime;
	}

	public Long getSchedulingTemplateId() {
		return schedulingTemplateId;
	}

	public void setSchedulingTemplateId(Long schedulingTemplateId) {
		this.schedulingTemplateId = schedulingTemplateId;
	}


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

	public MeetingType getMeetingType() {
		return meetingType;
	}

	public void setMeetingType(MeetingType meetingType) {
		if(meetingType != null) {
			this.meetingType = meetingType;
		}
	}

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
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

	public UUID getSchedulingInfoReservationId() {
		return schedulingInfoReservationId;
	}

	public void setSchedulingInfoReservationId(UUID schedulingInfoReservationId) {
		this.schedulingInfoReservationId = schedulingInfoReservationId;
	}

	public boolean getGuestPinRequired() {
		return guestPinRequired;
	}

	public void setGuestPinRequired(boolean guestPinRequired) {
		this.guestPinRequired = guestPinRequired;
	}

	public VmrType getVmrType() {
		return vmrType;
	}

	public void setVmrType(VmrType vmrType) {
		this.vmrType = vmrType;
	}

	public ViewType getHostView() {
		return hostView;
	}

	public void setHostView(ViewType hostView) {
		this.hostView = hostView;
	}

	public ViewType getGuestView() {
		return guestView;
	}

	public void setGuestView(ViewType guestView) {
		this.guestView = guestView;
	}

	public VmrQuality getVmrQuality() {
		return vmrQuality;
	}

	public void setVmrQuality(VmrQuality vmrQuality) {
		this.vmrQuality = vmrQuality;
	}

	public Boolean getEnableOverlayText() {
		return enableOverlayText;
	}

	public void setEnableOverlayText(Boolean enableOverlayText) {
		this.enableOverlayText = enableOverlayText;
	}

	public Boolean getGuestsCanPresent() {
		return guestsCanPresent;
	}

	public void setGuestsCanPresent(Boolean guestsCanPresent) {
		this.guestsCanPresent = guestsCanPresent;
	}

	public Boolean getForcePresenterIntoMain() {
		return forcePresenterIntoMain;
	}

	public void setForcePresenterIntoMain(Boolean forcePresenterIntoMain) {
		this.forcePresenterIntoMain = forcePresenterIntoMain;
	}

	public Boolean getForceEncryption() {
		return forceEncryption;
	}

	public void setForceEncryption(Boolean forceEncryption) {
		this.forceEncryption = forceEncryption;
	}

	public Boolean getMuteAllGuests() {
		return muteAllGuests;
	}

	public void setMuteAllGuests(Boolean muteAllGuests) {
		this.muteAllGuests = muteAllGuests;
	}

	public String getUriWithoutDomain() {
		return uriWithoutDomain;
	}

	public void setUriWithoutDomain(String uriWithoutDomain) {
		this.uriWithoutDomain = uriWithoutDomain;
	}

	public Integer getGuestPin() {
		return guestPin;
	}

	public void setGuestPin(Integer guestPin) {
		this.guestPin = guestPin;
	}

	public Integer getHostPin() {
		return hostPin;
	}

	public void setHostPin(Integer hostPin) {
		this.hostPin = hostPin;
	}
}
