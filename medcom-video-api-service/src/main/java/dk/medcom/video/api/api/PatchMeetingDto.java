package dk.medcom.video.api.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;

public class PatchMeetingDto {

    @Size(max=100, message="subject should have a maximum of 100 characters")
    private String subject;
    @JsonIgnore
    private boolean subjectIsSet;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date startTime;
    @JsonIgnore
    private boolean startTimeIsSet;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date endTime;
    @JsonIgnore
    private boolean endTimeIsSet;

    @Size(max=500, message="description should have a maximum of 500 characters")
    private String description;
    @JsonIgnore
    private boolean descriptionIsSet;

    @Size(max=100, message="Project Code should have a maximum of 100 characters")
    private String projectCode;
    @JsonIgnore
    private boolean projectIsSet;

    private String organizedByEmail;
    @JsonIgnore
    private boolean organizedByEmailIsSet;

    private List<String> labels;
    @JsonIgnore
    private boolean labelsIsSet;

    private GuestMicrophone guestMicrophone;
    @JsonIgnore
    private boolean guestMicrophoneSet;

    private boolean guestPinRequired;
    @JsonIgnore
    private boolean guestPinRequiredSet;

    private Integer hostPin;
    @JsonIgnore
    private boolean hostPinSet;

    private Integer guestPin;
    @JsonIgnore
    private boolean guestPinSet;

    private List<AdditionalInformationType> additionalInformation;
    @JsonIgnore
    private boolean additionalInfoSet;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
        this.subjectIsSet = true;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
        this.startTimeIsSet = true;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
        this.endTimeIsSet = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.descriptionIsSet = true;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
        this.projectIsSet = true;
    }

    public String getOrganizedByEmail() {
        return organizedByEmail;
    }

    public void setOrganizedByEmail(String organizedByEmail) {
        this.organizedByEmail = organizedByEmail;
        this.organizedByEmailIsSet = true;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
        this.labelsIsSet = true;
    }

    public boolean isSubjectIsSet() {
        return subjectIsSet;
    }

    public boolean isStartTimeIsSet() {
        return startTimeIsSet;
    }

    public boolean getDescriptionIsSet() {
        return descriptionIsSet;
    }

    public boolean isProjectIsSet() {
        return projectIsSet;
    }

    public boolean isOrganizedByEmailIsSet() {
        return organizedByEmailIsSet;
    }

    public boolean isLabelsIsSet() {
        return labelsIsSet;
    }

    public boolean isEndTimeIsSet() {
        return endTimeIsSet;
    }

    public GuestMicrophone getGuestMicrophone() {
        return guestMicrophone;
    }

    public void setGuestMicrophone(GuestMicrophone guestMicrophone) {
        this.guestMicrophone = guestMicrophone;
        this.guestMicrophoneSet = true;
    }

    public boolean isGuestMicrophoneSet() {
        return guestMicrophoneSet;
    }

    public boolean isGuestPinRequired() {
        return guestPinRequired;
    }

    public boolean isGuestPinRequiredSet() {
        return guestPinRequiredSet;
    }

    public void setGuestPinRequired(boolean guestPinRequired) {
        this.guestPinRequired = guestPinRequired;
        this.guestPinRequiredSet = true;
    }

    public Integer getGuestPin() {
        return guestPin;
    }

    public void setGuestPin(Integer guestPin) {
        this.guestPin = guestPin;
        this.guestPinSet = true;
    }

    public Integer getHostPin() {
        return hostPin;
    }

    public void setHostPin(Integer hostPin) {
        this.hostPin = hostPin;
        this.hostPinSet = true;
    }

    public boolean isHostPinSet() {
        return hostPinSet;
    }

    public boolean isGuestPinSet() {
        return guestPinSet;
    }

    public List<AdditionalInformationType> getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(List<AdditionalInformationType> additionalInformation) {
        this.additionalInformation = additionalInformation;
        this.additionalInfoSet = true;
    }

    public boolean isAdditionalInfoSet() {
        return additionalInfoSet;
    }
}
