package dk.medcom.video.api.service.domain.audit;

import java.util.List;

public class ParticipantSearch {
    private String meetingUuid;
    private String searchParticipantId;
    private String type;
    private String organisation;
    private String performedBy;
    private int resultCount;
    private List<String> resultIdentifiers;

    public String getMeetingUuid() {
        return meetingUuid;
    }

    public void setMeetingUuid(String meetingUuid) {
        this.meetingUuid = meetingUuid;
    }

    public String getSearchParticipantId() {
        return searchParticipantId;
    }

    public void setSearchParticipantId(String searchParticipantId) {
        this.searchParticipantId = searchParticipantId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public List<String> getResultIdentifiers() {
        return resultIdentifiers;
    }

    public void setResultIdentifiers(List<String> resultIdentifiers) {
        this.resultIdentifiers = resultIdentifiers;
    }
}
