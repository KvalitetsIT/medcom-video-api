package dk.medcom.video.api.dao.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "scheduling_template")
public class SchedulingTemplate {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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
	private boolean isDefaultTemplate;		//an organisation can have one default template
	private boolean isPoolTemplate;				//an organisation can have one or zero pool templates
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
	@Column(columnDefinition = "varchar")
	@Enumerated(EnumType.STRING)
	private DirectMedia directMedia;
	@ManyToOne
    @JoinColumn(name="created_by")
	private MeetingUser createdBy;
	private Date createdTime;
	
	@ManyToOne
    @JoinColumn(name="updated_by")
	private MeetingUser updatedBy;
	private Date updatedTime;

	@ManyToOne
    @JoinColumn(name="deleted_by")
	private MeetingUser deletedBy;
	private Date deletedTime;
	private String customPortalGuest;
	private String customPortalHost;
	private String returnUrl;

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
	public boolean getIsDefaultTemplate() {
		return isDefaultTemplate;
	}
	public void setIsDefaultTemplate(boolean isDefaultTemplate) {
		this.isDefaultTemplate = isDefaultTemplate;
	}
	public boolean getIsPoolTemplate() {
		return isPoolTemplate;
	}
	public void setIsPoolTemplate(boolean isPoolTemplate) {
		this.isPoolTemplate = isPoolTemplate;
	}
	public MeetingUser getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(MeetingUser createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public MeetingUser getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(MeetingUser updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	public MeetingUser getDeletedBy() {
		return deletedBy;
	}
	public void setDeletedBy(MeetingUser deletedBy) {
		this.deletedBy = deletedBy;
	}
	public Date getDeletedTime() {
		return deletedTime;
	}
	public void setDeletedTime(Date deletedTime) {
		this.deletedTime = deletedTime;
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
				+ ", ivrTheme=" + ivrTheme + ", isDefaultTemplate=" + isDefaultTemplate + ", isPoolTemplate="
				+ isPoolTemplate + ", createdBy=" + createdBy + ", createdTime=" + createdTime + ", updatedBy="
				+ updatedBy + ", updatedTime=" + updatedTime + ", deletedBy=" + deletedBy + ", deletedTime="
				+ deletedTime + "]";
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

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public DirectMedia getDirectMedia() {
		return directMedia;
	}

	public void setDirectMedia(DirectMedia directMedia) {
		this.directMedia = directMedia;
	}
}
