package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.MeetingUser;

public record MeetingUserModel(String organisationId,
                               String email) {
    public static MeetingUserModel from(MeetingUser meetingUser) {
        if (meetingUser == null) {
            return null;
        }
        return new MeetingUserModel(
                meetingUser.getOrganisation().getOrganisationId(),
                meetingUser.getEmail());
    }
}
