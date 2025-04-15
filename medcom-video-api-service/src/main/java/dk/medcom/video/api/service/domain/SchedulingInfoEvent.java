package dk.medcom.video.api.service.domain;

import java.time.Instant;

public class SchedulingInfoEvent {

    private MessageType messageType;
    private Long schedulingInfoIdentifier;

    private String uuid;
    private Long hostPin;
    private Long guestPin;
    private int VMRAvailableBefore;
    private Instant vMRStartTime;
    private String ivrTheme;
    private String uriWithoutDomain;
    private String uriDomain;
    private String uriWithDomain;
    private String organisationCode;
    private String portalLink;
    private int maxParticipants;
    private String vmrType;
    private String hostView;
    private String guestView;
    private boolean enableOverlayText;
    private boolean guestsCanPresent;
    private boolean forcePresenterIntoMain;
    private boolean forceEncryption;
    private boolean muteAllGuests;
    private String meetingUser;
    private Instant createdTime;
    private String customPortalGuest;
    private String customPortalHost;
    private String returnUrl;
    private boolean endMeetingOnEndTime;
    private String vmrQuality;
    private String ivrThemeProvisionId;
    private Instant meetingEndTime;
    private String directMedia;
    private String provisionVMRId;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setHostPin(Long hostPin) {
        this.hostPin = hostPin;
    }

    public Long getHostPin() {
        return hostPin;
    }

    public void setGuestPin(Long guestPin) {
        this.guestPin = guestPin;
    }

    public Long getGuestPin() {
        return guestPin;
    }

    public void setVMRAvailableBefore(int vmrAvailableBefore) {
        this.VMRAvailableBefore = vmrAvailableBefore;
    }

    public int getVMRAvailableBefore() {
        return VMRAvailableBefore;
    }

    public void setvMRStartTime(Instant vMRStartTime) {
        this.vMRStartTime = vMRStartTime;
    }

    public Instant getvMRStartTime() {
        return vMRStartTime;
    }

    public void setIvrTheme(String ivrTheme) {
        this.ivrTheme = ivrTheme;
    }

    public String getIvrTheme() {
        return ivrTheme;
    }

    public void setUriWithoutDomain(String uriWithoutDomain) {
        this.uriWithoutDomain = uriWithoutDomain;
    }

    public String getUriWithoutDomain() {
        return uriWithoutDomain;
    }

    public void setUriDomain(String uriDomain) {
        this.uriDomain = uriDomain;
    }

    public String getUriDomain() {
        return uriDomain;
    }

    public void setUriWithDomain(String uriWithDomain) {
        this.uriWithDomain = uriWithDomain;
    }

    public String getUriWithDomain() {
        return uriWithDomain;
    }

    public void setOrganisationCode(String organisationCode) {
        this.organisationCode = organisationCode;
    }

    public String getOrganisationCode() {
        return organisationCode;
    }

    public void setPortalLink(String portalLink) {
        this.portalLink = portalLink;
    }

    public String getPortalLink() {
        return portalLink;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setEndMeetingOnEndTime(boolean endMeetingOnEndTime) {
        this.endMeetingOnEndTime = endMeetingOnEndTime;
    }

    public boolean isEndMeetingOnEndTime() {
        return endMeetingOnEndTime;
    }

    public void setVmrType(String vmrType) {
        this.vmrType = vmrType;
    }

    public String getVmrType() {
        return vmrType;
    }

    public void setHostView(String hostView) {
        this.hostView = hostView;
    }

    public String getHostView() {
        return hostView;
    }

    public void setGuestView(String guestView) {
        this.guestView = guestView;
    }

    public String getGuestView() {
        return guestView;
    }

    public void setVmrQuality(String vmrQuality) {
        this.vmrQuality = vmrQuality;
    }

    public String getVmrQuality() {
        return vmrQuality;
    }

    public void setEnableOverlayText(boolean enableOverlayText) {
        this.enableOverlayText = enableOverlayText;
    }

    public boolean isEnableOverlayText() {
        return enableOverlayText;
    }

    public void setGuestsCanPresent(boolean guestsCanPresent) {
        this.guestsCanPresent = guestsCanPresent;
    }

    public boolean isGuestsCanPresent() {
        return guestsCanPresent;
    }

    public void setForcePresenterIntoMain(boolean forcePresenterIntoMain) {
        this.forcePresenterIntoMain = forcePresenterIntoMain;
    }

    public boolean isForcePresenterIntoMain() {
        return forcePresenterIntoMain;
    }

    public void setForceEncryption(boolean forceEncryption) {
        this.forceEncryption = forceEncryption;
    }

    public boolean isForceEncryption() {
        return forceEncryption;
    }

    public void setMuteAllGuests(boolean muteAllGuests) {
        this.muteAllGuests = muteAllGuests;
    }

    public boolean isMuteAllGuests() {
        return muteAllGuests;
    }

    public void setMeetingUser(String meetingUser) {
        this.meetingUser = meetingUser;
    }

    public String getMeetingUser() {
        return meetingUser;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCustomPortalGuest(String customPortalGuest) {
        this.customPortalGuest = customPortalGuest;
    }

    public String getCustomPortalGuest() {
        return customPortalGuest;
    }

    public void setCustomPortalHost(String customPortalHost) {
        this.customPortalHost = customPortalHost;
    }

    public String getCustomPortalHost() {
        return customPortalHost;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public void setIvrThemeProvisionId(String ivrThemeProvisionId) {
        this.ivrThemeProvisionId = ivrThemeProvisionId;
    }

    public String getIvrThemeProvisionId() {
        return ivrThemeProvisionId;
    }

    public Instant getMeetingEndTime() {
        return meetingEndTime;
    }

    public void setMeetingEndTime(Instant meetingEndTime) {
        this.meetingEndTime = meetingEndTime;
    }

    public Long getSchedulingInfoIdentifier() {
        return schedulingInfoIdentifier;
    }

    public void setSchedulingInfoIdentifier(Long schedulingInfoIdentifier) {
        this.schedulingInfoIdentifier = schedulingInfoIdentifier;
    }

    public String getDirectMedia() {
        return directMedia;
    }

    public void setDirectMedia(String directMedia) {
        this.directMedia = directMedia;
    }

    public String getProvisionVMRId() {
        return provisionVMRId;
    }

    public void setProvisionVMRId(String provisionVMRId) {
        this.provisionVMRId = provisionVMRId;
    }
}
