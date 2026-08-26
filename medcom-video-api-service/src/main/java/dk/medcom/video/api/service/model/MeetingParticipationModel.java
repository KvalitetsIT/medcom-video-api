package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.ParticipantRole;
import dk.medcom.video.api.dao.entity.SchedulingInfo;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public record MeetingParticipationModel(UUID uuid,
                                        String subject,
                                        String description,
                                        OffsetDateTime startTime,
                                        OffsetDateTime endTime,
                                        MeetingUserModel createdBy,
                                        MeetingUserModel updatedBy,
                                        OffsetDateTime updatedTime,
                                        MeetingUserModel organizedBy,
                                        int knownParticipants,
                                        ParticipantRole participantRole,
                                        String pin,
                                        String uriWithDomain,
                                        String shortLink,
                                        String portalLink) {
    public static MeetingParticipationModel from(Meeting meeting,
                                                 SchedulingInfo schedulingInfo,
                                                 int knownParticipants,
                                                 ParticipantRole participantRole,
                                                 String pin,
                                                 String shortLinkBaseUrl) {
        if (meeting == null) {
            return null;
        }
        return new MeetingParticipationModel(
                UUID.fromString(meeting.getUuid()),
                meeting.getSubject(),
                meeting.getDescription(),
                mapDate(meeting.getStartTime()),
                mapDate(meeting.getEndTime()),
                MeetingUserModel.from(meeting.getMeetingUser()),
                MeetingUserModel.from(meeting.getUpdatedByUser()),
                mapDate(meeting.getUpdatedTime()),
                MeetingUserModel.from(meeting.getOrganizedByUser()),
                knownParticipants,
                participantRole,
                pin,
                schedulingInfo != null ? schedulingInfo.getUriWithDomain() : null,
                shortLinkBaseUrl + meeting.getShortId(),
                schedulingInfo != null ? schedulingInfo.getPortalLink() : null);
    }

    private static OffsetDateTime mapDate(Date input) {
        return input != null ? OffsetDateTime.ofInstant(input.toInstant(), ZoneId.systemDefault()) : null;
    }
}