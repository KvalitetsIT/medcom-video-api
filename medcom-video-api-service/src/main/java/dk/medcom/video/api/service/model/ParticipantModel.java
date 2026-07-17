package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Participant;
import dk.medcom.video.api.dao.entity.ParticipantRole;
import dk.medcom.video.api.dao.entity.ParticipantType;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record ParticipantModel(
        Long id,
        UUID uuid,
        ParticipantType type,
        String externalId,
        String organisation,
        ParticipantRole role,
        OffsetDateTime createdTime,
        MeetingUserModel createdBy,
        OffsetDateTime updatedTime,
        MeetingUserModel updatedBy) {

    public static ParticipantModel from(Participant participant, MeetingUser createdByUser, MeetingUser updatedByUser) {
        return new ParticipantModel(
                participant.id(),
                participant.uuid(),
                participant.type(),
                participant.participantId(),
                participant.organisation(),
                participant.role(),
                mapDate(participant.createdAt()),
                MeetingUserModel.from(createdByUser),
                mapDate(participant.updatedAt()),
                MeetingUserModel.from(updatedByUser));
    }

    private static OffsetDateTime mapDate(java.time.LocalDateTime input) {
        return input != null ? input.atZone(ZoneId.systemDefault()).toOffsetDateTime() : null;
    }
}