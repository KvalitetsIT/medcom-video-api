package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.Participant;
import dk.medcom.video.api.dao.entity.ParticipantRole;
import dk.medcom.video.api.dao.entity.ParticipantType;

public record ParticipantModel(
        Long id,
        ParticipantType type,
        String externalId,
        String organisation,
        ParticipantRole role) {

    public static ParticipantModel from(Participant participant) {
        return new ParticipantModel(
                participant.getId(),
                participant.getType(),
                participant.getExternalId(),
                participant.getOrganisation(),
                participant.getRole());
    }
}