package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.ParticipantRole;
import dk.medcom.video.api.dao.entity.ParticipantType;

public record CreateParticipantModel(
        ParticipantType type,
        String externalId,
        String organisation,
        ParticipantRole role) {
}