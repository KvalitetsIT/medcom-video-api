package dk.medcom.video.api.dao.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record Participant(
        Long id,
        UUID uuid,
        Long meetingId,
        UUID meetingUuid,
        ParticipantType type,
        String participantId,
        String organisationId,
        ParticipantRole role,
        LocalDateTime createdAt,
        Long createdBy,
        LocalDateTime updatedAt,
        Long updatedBy
) {}