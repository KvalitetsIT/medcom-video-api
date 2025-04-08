package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingLabel;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record MeetingModel(String subject,
                           UUID uuid,
                           MeetingUserModel createdBy,
                           MeetingUserModel updatedBy,
                           MeetingUserModel organizedBy,
                           OffsetDateTime startTime,
                           OffsetDateTime endTime,
                           String description,
                           String projectCode,
                           OffsetDateTime createdTime,
                           OffsetDateTime updatedTime,
                           String shortId,
                           String shortlink,
                           String shortLink,
                           String externalId,
                           GuestMicrophoneModel guestMicrophone,
                           boolean guestPinRequired,
                           List<String> labels,
                           List<AdditionalInformationModel> additionalInformation) {
    public static MeetingModel from(Meeting meeting, String shortLinkBaseUrl) {
        if (meeting == null) {
            return null;
        }

        return new MeetingModel(
                meeting.getSubject(),
                UUID.fromString(meeting.getUuid()),
                MeetingUserModel.from(meeting.getMeetingUser()),
                MeetingUserModel.from(meeting.getUpdatedByUser()),
                MeetingUserModel.from(meeting.getOrganizedByUser()),
                mapDate(meeting.getStartTime()),
                mapDate(meeting.getEndTime()),
                meeting.getDescription(),
                meeting.getProjectCode(),
                mapDate(meeting.getCreatedTime()),
                mapDate(meeting.getUpdatedTime()),
                meeting.getShortId(),
                shortLinkBaseUrl + meeting.getShortId(),
                shortLinkBaseUrl + meeting.getShortId(),
                meeting.getExternalId(),
                GuestMicrophoneModel.from(meeting.getGuestMicrophone()),
                meeting.getGuestPinRequired(),
                meeting.getMeetingLabels().stream().map(MeetingLabel::getLabel).toList(),
                meeting.getMeetingAdditionalInfo().stream().map(AdditionalInformationModel::from).toList());
    }

    private static OffsetDateTime mapDate(Date input) {
        return input != null ? OffsetDateTime.ofInstant(input.toInstant(), ZoneId.systemDefault()) : null;
    }
}
