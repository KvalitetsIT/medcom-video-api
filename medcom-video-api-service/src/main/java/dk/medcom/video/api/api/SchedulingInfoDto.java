package dk.medcom.video.api.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import dk.medcom.video.api.controller.SchedulingInfoController;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class SchedulingInfoDto extends RepresentationModel {
    private UUID reservationId;
	public String uuid;
	public Long hostPin; 		
	public Long guestPin;
	public int vmrAvailableBefore;
	public int maxParticipants;
	public boolean endMeetingOnEndTime;
	private String uriWithDomain;		
	private String uriWithoutDomain;
	private ProvisionStatus provisionStatus;
	private String provisionStatusDescription;
	private String portalLink;
	private String ivrTheme;

	private VmrType vmrType;
	private ViewType hostView;
	private ViewType guestView;
	private VmrQuality vmrQuality;
	private boolean enableOverlayText;
	private boolean guestsCanPresent;
	private boolean forcePresenterIntoMain;
	private boolean forceEncryption;
	private boolean muteAllGuests;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z")		//Date format should be: "2018-07-12T09:00:00
	private Date provisionTimestamp;
	private String provisionVmrId;

	public MeetingUserDto createdBy;
	public MeetingUserDto updatedBy;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z") 		//Date format should be: "2018-07-12T09:00:00
	public Date createdTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss Z") 		//Date format should be: "2018-07-12T09:00:00
	public Date updatedTime;
	private String shortLink;

	public MeetingDto meetingDetails;
	private String shortlink;
	private String customPortalGuest;
	private String customPortalHost;

	public SchedulingInfoDto() {	
	}
	
	public SchedulingInfoDto(SchedulingInfo schedulingInfo, String shortLinkBaseurl) {
		
		uuid = schedulingInfo.getUuid();
		hostPin = schedulingInfo.getHostPin();
		guestPin = schedulingInfo.getGuestPin();
		vmrAvailableBefore = schedulingInfo.getVMRAvailableBefore();
		maxParticipants = schedulingInfo.getMaxParticipants();
		endMeetingOnEndTime = schedulingInfo.getEndMeetingOnEndTime();
		uriWithDomain = schedulingInfo.getUriWithDomain();
		uriWithoutDomain = schedulingInfo.getUriWithoutDomain();
		provisionStatus = schedulingInfo.getProvisionStatus();
		provisionStatusDescription = schedulingInfo.getProvisionStatusDescription();
		portalLink = schedulingInfo.getPortalLink();
		ivrTheme = schedulingInfo.getIvrTheme();
		provisionTimestamp = schedulingInfo.getProvisionTimestamp();	
		provisionVmrId = schedulingInfo.getProvisionVMRId();
		if(schedulingInfo.getReservationId() != null) {
			reservationId = UUID.fromString(schedulingInfo.getReservationId());
		}

		vmrType = schedulingInfo.getVmrType();
		hostView = schedulingInfo.getHostView();
		guestView = schedulingInfo.getGuestView();
		vmrQuality = schedulingInfo.getVmrQuality();
		enableOverlayText = schedulingInfo.getEnableOverlayText();
		guestsCanPresent = schedulingInfo.getGuestsCanPresent();
		forcePresenterIntoMain = schedulingInfo.getForcePresenterIntoMain();
		forceEncryption = schedulingInfo.getForceEncryption();
		muteAllGuests = schedulingInfo.getMuteAllGuests();
		customPortalGuest = schedulingInfo.getCustomPortalGuest();
		customPortalHost = schedulingInfo.getCustomPortalHost();

		MeetingUser meetingUser = schedulingInfo.getMeetingUser();
		MeetingUserDto meetingUserDto = new MeetingUserDto(meetingUser);
		
		MeetingUser updatedByUser = schedulingInfo.getUpdatedByUser();
		MeetingUserDto updatedByUserDto = new MeetingUserDto(updatedByUser);

		createdBy = meetingUserDto;
		updatedBy = updatedByUserDto;
		createdTime = schedulingInfo.getCreatedTime();
		updatedTime = schedulingInfo.getUpdatedTime();
		
		Meeting meeting = schedulingInfo.getMeeting();
		if(meeting != null) {
			meetingDetails = new MeetingDto(meeting, shortLinkBaseurl);
			shortLink = shortLinkBaseurl + meeting.getShortId();
			shortlink = shortLink;
		}

		try {  
			Link selfLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(uuid)).withRel("self");
			add(selfLink);
			
		} catch (RessourceNotFoundException e) {
		}
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

	public int getVmrAvailableBefore() {
		return vmrAvailableBefore;
	}
 
	public void setVmrAvailableBefore(int vmrAvailableBefore) {
		this.vmrAvailableBefore = vmrAvailableBefore;
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

	public ProvisionStatus getProvisionStatus() {
		return provisionStatus;
	}

	public void setProvisionStatus(ProvisionStatus provisionStatus) {
		this.provisionStatus = provisionStatus;
	}
	public String getProvisionStatusDescription() {
		return provisionStatusDescription;
	}

	public void setProvisionStatusDescription(String provisionStatusDescription) {
		this.provisionStatusDescription = provisionStatusDescription;
	}

	public Date getProvisionTimestamp() {
		return provisionTimestamp;
	}

	public void setProvisionTimestamp(Date provisionTimestamp) {
		this.provisionTimestamp = provisionTimestamp;
	}

	public String getProvisionVmrId() {
		return provisionVmrId;
	}

	public void setProvisionVmrId(String provisionVmrId) {
		this.provisionVmrId = provisionVmrId;
	}
	
	public String getPortalLink() {
		return portalLink;
	}

	public void setPortalLink(String portalLink) {
		this.portalLink = portalLink;
	}
	
	public String getIvrTheme() {
		return ivrTheme;
	}

	public void setIvrTheme(String ivrTheme) {
		this.ivrTheme = ivrTheme;
	}

	public String getShortLink() {
		return shortLink;
	}

	public void setShortLink(String shortLink) {
		this.shortLink = shortLink;
	}

	public String getShortlink() {
		return shortlink;
	}

	public void setShortlink(String shortlink) {
		this.shortlink = shortlink;
	}

	public UUID getReservationId() {
		return reservationId;
	}

	public void setReservationId(UUID reservationId) {
		this.reservationId = reservationId;
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

	public boolean getEnableOverlayText() {
		return enableOverlayText;
	}

	public void setEnableOverlayText(boolean enableOverlayText) {
		this.enableOverlayText = enableOverlayText;
	}

	public boolean getGuestsCanPresent() {
		return guestsCanPresent;
	}

	public void setGuestsCanPresent(boolean guestsCanPresent) {
		this.guestsCanPresent = guestsCanPresent;
	}

	public boolean getForcePresenterIntoMain() {
		return forcePresenterIntoMain;
	}

	public void setForcePresenterIntoMain(boolean forcePresenterIntoMain) {
		this.forcePresenterIntoMain = forcePresenterIntoMain;
	}

	public boolean getForceEncryption() {
		return forceEncryption;
	}

	public void setForceEncryption(boolean forceEncryption) {
		this.forceEncryption = forceEncryption;
	}

	public boolean getMuteAllGuests() {
		return muteAllGuests;
	}

	public void setMuteAllGuests(boolean muteAllGuests) {
		this.muteAllGuests = muteAllGuests;
	}

	public String getCustomPortalGuest() {
		return customPortalGuest;
	}

	public void setCustomPortalGuest(String customPortalGuest) {
		this.customPortalGuest = customPortalGuest;
	}

	public String getCustomPortalHost() {
		return customPortalHost;
	}

	public void setCustomPortalHost(String customPortalHost) {
		this.customPortalHost = customPortalHost;
	}
}
