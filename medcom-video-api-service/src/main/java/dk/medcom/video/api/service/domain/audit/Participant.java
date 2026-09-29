package dk.medcom.video.api.service.domain.audit;

public class Participant {
    private String uuid;
    private String meetingUuid;
    private String type;
    private String role;
    private String organisation;
    private String performedBy;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMeetingUuid() {
        return meetingUuid;
    }

    public void setMeetingUuid(String meetingUuid) {
        this.meetingUuid = meetingUuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }
}
