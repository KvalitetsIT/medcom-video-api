package dk.medcom.video.api.dao.entity;

import dk.medcom.video.api.api.*;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "scheduling_info")
public class SchedulingInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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

	private boolean poolOverflow;
	private boolean pool;

	@Column(columnDefinition = "varchar")
	@Enumerated(EnumType.STRING)
	private VmrType vmrType;				//type of the virtual meeting room.
	@Column(columnDefinition = "varchar")
	@Enumerated(EnumType.STRING)
	private ViewType hostView;				//the layout seen by Hosts.
	@Column(columnDefinition = "varchar")
	@Enumerated(EnumType.STRING)
	private ViewType guestView;				//the layout seen by Guests
	@Column(columnDefinition = "varchar")
	@Enumerated(EnumType.STRING)
	private VmrQuality vmrQuality;			//controls the maximum call quality for participants connecting to this service.
	private boolean enableOverlayText;		//if participant name overlays are enabled, the display names or aliases of all participants are shown in a text overlay along the bottom of their video image
	private boolean guestsCanPresent;		//controls whether the Guests in the conference are allowed to present content
	private boolean forcePresenterIntoMain;	//controls whether the Host who is presenting is locked into the main video position
	private boolean forceEncryption;
	private boolean muteAllGuests;			//controls whether to mute guests when they first join the conference
	
	@ManyToOne
	@JoinColumn(name="scheduling_template_id")
	private SchedulingTemplate schedulingTemplate;
	@Column(columnDefinition = "varchar")
	@Enumerated(EnumType.STRING)
	private ProvisionStatus provisionStatus;
	private String provisionStatusDescription;
	private Date provisionTimestamp;
	private String provisionVMRId;

	@ManyToOne
	@JoinColumn(name="organisation_id")
	private Organisation organisation;

	@OneToOne
	@JoinColumn(name="meetings_id")
	private Meeting meeting;
	
	private String portalLink;				//link to "borger-portal"
	private String ivrTheme;				//theme to use in Pexip
	
	@ManyToOne
    @JoinColumn(name="created_by")
	private MeetingUser createdBy;
	private Date createdTime;
	
	@ManyToOne
    @JoinColumn(name="updated_by")
	private MeetingUser updatedBy;
	private Date updatedTime;
	private String reservationId;
	private String uriDomain;
	private String customPortalGuest;
	private String customPortalHost;
	private String returnUrl;
	@Column(columnDefinition = "varchar")
	@Enumerated(EnumType.STRING)
	private DirectMedia directMedia;
	private boolean newProvisioner;				//using the new (true) or old (false) provisioner service

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
	public String getProvisionVMRId() {
		return provisionVMRId;
	}
	public void setProvisionVMRId(String provisionVMRId) {
		this.provisionVMRId = provisionVMRId;
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

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationId() {
        return reservationId;
    }

	public void setPoolOverflow(boolean poolOverflow) {
		this.poolOverflow = poolOverflow;
	}

	public boolean getPoolOverflow() {
		return poolOverflow;
	}

	public boolean isPool() {
		return pool;
	}

	public void setPool(boolean pool) {
		this.pool = pool;
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

	public void setUriDomain(String uriDomain) {
		this.uriDomain = uriDomain;
	}

	public String getUriDomain() {
		return uriDomain;
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

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public void setDirectMedia(DirectMedia directMedia) {
		this.directMedia = directMedia;
	}

	public DirectMedia getDirectMedia() {
		return directMedia;
	}

	public boolean isNewProvisioner() {
		return newProvisioner;
	}

	public void setNewProvisioner(boolean newProvisioner) {
		this.newProvisioner = newProvisioner;
	}
}
