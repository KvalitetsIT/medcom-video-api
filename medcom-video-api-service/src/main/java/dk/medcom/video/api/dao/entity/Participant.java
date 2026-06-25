package dk.medcom.video.api.dao.entity;

import java.util.UUID;

public record Participant(
        Long id,
        UUID uuid,
        Long meetingId,
        String meetingUuid,
        ParticipantType type,
        String externalId,
        String organisation,
        ParticipantRole role
) { }