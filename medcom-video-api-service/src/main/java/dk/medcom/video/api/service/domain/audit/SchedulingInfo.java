package dk.medcom.video.api.service.domain.audit;

import java.time.OffsetDateTime;

public class SchedulingInfo {
    private Long id;
    private String uuid;
    private Long hostPin;
    private Long guestPin;
    private int vMRAvailableBefore;
    private OffsetDateTime vMRStartTime;
    private int maxParticipants;
    private boolean endMeetingOnEndTime;
    private String uriWithDomain;
    private String uriWithoutDomain;
    private boolean poolOverflow;
    private boolean pool;
    private String vmrType;
    private String hostView;
    private String guestView;
    private String vmrQuality;
    private boolean enableOverlayText;
    private boolean guestsCanPresent;
    private boolean forcePresenterIntoMain;
    private boolean forceEncryption;
    private boolean muteAllGuests;
    private Long schedulingTemplate;
    private String provisionStatus;
    private String provisionStatusDescription;
    private OffsetDateTime provisionTimestamp;
    private String provisionVMRId;
    private String organisation;
    private String portalLink;
    private String ivrTheme;
    private String createdBy;
    private OffsetDateTime createdTime;
    private String updatedBy;
    private OffsetDateTime updatedTime;
    private String reservationId;
    private String directMedia;

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

    public int getvMRAvailableBefore() {
        return vMRAvailableBefore;
    }

    public void setvMRAvailableBefore(int vMRAvailableBefore) {
        this.vMRAvailableBefore = vMRAvailableBefore;
    }

    public OffsetDateTime getvMRStartTime() {
        return vMRStartTime;
    }

    public void setvMRStartTime(OffsetDateTime vMRStartTime) {
        this.vMRStartTime = vMRStartTime;
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

    public boolean isPoolOverflow() {
        return poolOverflow;
    }

    public void setPoolOverflow(boolean poolOverflow) {
        this.poolOverflow = poolOverflow;
    }

    public boolean isPool() {
        return pool;
    }

    public void setPool(boolean pool) {
        this.pool = pool;
    }

    public String getVmrType() {
        return vmrType;
    }

    public void setVmrType(String vmrType) {
        this.vmrType = vmrType;
    }

    public String getHostView() {
        return hostView;
    }

    public void setHostView(String hostView) {
        this.hostView = hostView;
    }

    public String getGuestView() {
        return guestView;
    }

    public void setGuestView(String guestView) {
        this.guestView = guestView;
    }

    public String getVmrQuality() {
        return vmrQuality;
    }

    public void setVmrQuality(String vmrQuality) {
        this.vmrQuality = vmrQuality;
    }

    public boolean isEnableOverlayText() {
        return enableOverlayText;
    }

    public void setEnableOverlayText(boolean enableOverlayText) {
        this.enableOverlayText = enableOverlayText;
    }

    public boolean isGuestsCanPresent() {
        return guestsCanPresent;
    }

    public void setGuestsCanPresent(boolean guestsCanPresent) {
        this.guestsCanPresent = guestsCanPresent;
    }

    public boolean isForcePresenterIntoMain() {
        return forcePresenterIntoMain;
    }

    public void setForcePresenterIntoMain(boolean forcePresenterIntoMain) {
        this.forcePresenterIntoMain = forcePresenterIntoMain;
    }

    public boolean isForceEncryption() {
        return forceEncryption;
    }

    public void setForceEncryption(boolean forceEncryption) {
        this.forceEncryption = forceEncryption;
    }

    public boolean isMuteAllGuests() {
        return muteAllGuests;
    }

    public void setMuteAllGuests(boolean muteAllGuests) {
        this.muteAllGuests = muteAllGuests;
    }

    public Long getSchedulingTemplate() {
        return schedulingTemplate;
    }

    public void setSchedulingTemplate(Long schedulingTemplate) {
        this.schedulingTemplate = schedulingTemplate;
    }

    public String getProvisionStatus() {
        return provisionStatus;
    }

    public void setProvisionStatus(String provisionStatus) {
        this.provisionStatus = provisionStatus;
    }

    public String getProvisionStatusDescription() {
        return provisionStatusDescription;
    }

    public void setProvisionStatusDescription(String provisionStatusDescription) {
        this.provisionStatusDescription = provisionStatusDescription;
    }

    public OffsetDateTime getProvisionTimestamp() {
        return provisionTimestamp;
    }

    public void setProvisionTimestamp(OffsetDateTime provisionTimestamp) {
        this.provisionTimestamp = provisionTimestamp;
    }

    public String getProvisionVMRId() {
        return provisionVMRId;
    }

    public void setProvisionVMRId(String provisionVMRId) {
        this.provisionVMRId = provisionVMRId;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(OffsetDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public OffsetDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(OffsetDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public void setDirectMedia(String directMedia) {
        this.directMedia = directMedia;
    }

    public String getDirectMedia() {
        return directMedia;
    }
}
