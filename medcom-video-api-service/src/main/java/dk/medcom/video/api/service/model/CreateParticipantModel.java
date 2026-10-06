package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.ParticipantRole;
import dk.medcom.video.api.dao.entity.ParticipantType;

public record CreateParticipantModel(
        ParticipantType type,
        String participantId,
        String name,
        String organisation,
        ParticipantRole role) {
}