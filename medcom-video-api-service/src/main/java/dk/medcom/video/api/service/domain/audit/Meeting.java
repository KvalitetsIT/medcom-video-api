package dk.medcom.video.api.service.domain.audit;

import dk.medcom.video.api.api.GuestMicrophone;
import dk.medcom.video.api.dao.entity.MeetingAdditionalInfo;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public class Meeting {
    private Long id;
    private String uuid;
    private String subject;
    private String organisation;
    private String createdBy;
    private OffsetDateTime createdTime;
    private String updatedBy;
    private OffsetDateTime updatedTime;
    private String organizedBy;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String description;
    private String projectCode;
    private String shortId;
    private String externalId;
    private Set<String> meetingLabels = new HashSet<>();
    private GuestMicrophone guestMicrophone;
    private boolean guestPinRequired;
    private Set<MeetingAdditionalInfo> meetingAdditionalInfo = new HashSet<>();

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
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

    public String getOrganizedBy() {
        return organizedBy;
    }

    public void setOrganizedBy(String organizedBy) {
        this.organizedBy = organizedBy;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Set<String> getMeetingLabels() {
        return meetingLabels;
    }

    public void setMeetingLabels(Set<String> meetingLabels) {
        this.meetingLabels = meetingLabels;
    }

    public GuestMicrophone getGuestMicrophone() {
        return guestMicrophone;
    }

    public void setGuestMicrophone(GuestMicrophone guestMicrophone) {
        this.guestMicrophone = guestMicrophone;
    }

    public boolean isGuestPinRequired() {
        return guestPinRequired;
    }

    public void setGuestPinRequired(boolean guestPinRequired) {
        this.guestPinRequired = guestPinRequired;
    }

    public Set<MeetingAdditionalInfo> getAdditionalInfo() {
        return meetingAdditionalInfo;
    }

    public void setAdditionalInfo(Set<MeetingAdditionalInfo> meetingAdditionalInfo) {
        this.meetingAdditionalInfo = meetingAdditionalInfo;
    }
}
