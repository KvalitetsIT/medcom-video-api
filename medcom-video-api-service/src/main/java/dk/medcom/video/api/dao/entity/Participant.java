package dk.medcom.video.api.dao.entity;

public record Participant(
        Long id,
        Long meetingId,
        String meetingUuid,
        ParticipantType type,
        String externalId,
        String organisation,
        ParticipantRole role
) { }