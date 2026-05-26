package dk.medcom.video.api.controller.v2.mapper;

import dk.medcom.video.api.service.model.CreateParticipantModel;
import dk.medcom.video.api.service.model.ParticipantModel;
import dk.medcom.video.api.service.model.UpdateParticipantModel;
import org.openapitools.model.CreateParticipant;
import org.openapitools.model.Participant;
import org.openapitools.model.UpdateParticipant;

import java.util.List;

public class ParticipantMapper {

    public static List<CreateParticipantModel> externalToInternal(List<CreateParticipant> participants) {
        return participants.stream().map(p -> new CreateParticipantModel(EnumMapper.externalToInternal(p.getType()), p.getExternalId(), p.getOrganisation(), EnumMapper.externalToInternal(p.getRole()))).toList();
    }

    public static List<Participant> internalToExternal(List<ParticipantModel> participants){
        return participants.stream().map(ParticipantMapper::internalToExternal).toList();
    }

    public static Participant internalToExternal(ParticipantModel participantModel){
        var participant = new Participant();
        participant.setId(participantModel.id());
        participant.setExternalId(participantModel.externalId());
        participant.setRole(EnumMapper.internalToExternal(participantModel.role()));
        participant.setType(EnumMapper.internalToExternal(participantModel.type()));
        return participant;
    }
    public static UpdateParticipantModel externalToInternal(UpdateParticipant updateParticipant) {
        return new UpdateParticipantModel( EnumMapper.externalToInternal(updateParticipant.getRole()));
    }
}
