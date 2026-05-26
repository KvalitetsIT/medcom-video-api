package dk.medcom.video.api.service;

import dk.medcom.video.api.service.model.ParticipantModel;
import dk.medcom.video.api.service.model.CreateParticipantModel;
import dk.medcom.video.api.service.model.UpdateParticipantModel;

import java.util.List;
import java.util.UUID;


public interface ParticipantService {
    List<ParticipantModel> getParticipants(UUID meetingUuid);

    List<ParticipantModel> createParticipants(UUID meetingUuid, List<CreateParticipantModel> createParticipantModel);

    void deleteParticipant(UUID meetingUuid, Long participantId);

    ParticipantModel updateParticipant(UUID uuid, Long id, UpdateParticipantModel updateParticipant);
}