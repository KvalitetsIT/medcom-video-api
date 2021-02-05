package dk.medcom.video.api.service.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateMeeting {
    private String subject;
    private Date startTime;
    private Date endTime;
    private String description;
    private String projectCode;
    private String organizedByEmail;
    private List<String> labels = new ArrayList<>();
    private GuestMicrophone guestMicrophone;
    private boolean GuestPinRequired;

    public String getSubject() {
        return subject;
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

    public String getOrganizedByEmail() {
        return organizedByEmail;
    }

    public void setOrganizedByEmail(String organizedByEmail) {
        this.organizedByEmail = organizedByEmail;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public GuestMicrophone getGuestMicrophone() {
        return guestMicrophone;
    }

    public void setGuestMicrophone(GuestMicrophone guestMicrophone) {
        this.guestMicrophone = guestMicrophone;
    }

    public boolean isGuestPinRequired() {
        return GuestPinRequired;
    }

    public void setGuestPinRequired(boolean guestPinRequired) {
        GuestPinRequired = guestPinRequired;
    }
}
