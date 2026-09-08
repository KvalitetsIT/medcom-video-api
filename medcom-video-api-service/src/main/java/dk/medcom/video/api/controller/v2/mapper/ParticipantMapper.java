package dk.medcom.video.api.controller.v2.mapper;

import dk.medcom.video.api.service.model.CreateParticipantModel;
import dk.medcom.video.api.service.model.MeetingUserModel;
import dk.medcom.video.api.service.model.ParticipantModel;
import dk.medcom.video.api.service.model.UpdateParticipantModel;
import org.openapitools.model.CreateParticipant;
import org.openapitools.model.MeetingUser;
import org.openapitools.model.Participant;
import org.openapitools.model.UpdateParticipant;

import java.util.List;

public class ParticipantMapper {
    public static List<CreateParticipantModel> externalToInternal(List<CreateParticipant> participants) {
        return participants.stream().map(p -> new CreateParticipantModel(EnumMapper.externalToInternal(p.getType()), p.getParticipantId(), p.getOrganisationId(), EnumMapper.externalToInternal(p.getRole()))).toList();
    }

    public static List<Participant> internalToExternal(List<ParticipantModel> participants){
        return participants.stream().map(ParticipantMapper::internalToExternal).toList();
    }

    public static Participant internalToExternal(ParticipantModel participantModel){
        var participant = new Participant();
        participant.setUuid(participantModel.uuid());
        participant.setParticipantId(participantModel.externalId());
        participant.setRole(EnumMapper.internalToExternal(participantModel.role()));
        participant.setOrganisationId(participantModel.organisation());
        participant.setType(EnumMapper.internalToExternal(participantModel.type()));
        participant.setCreatedTime(participantModel.createdTime());
        participant.setUpdatedTime(participantModel.updatedTime());
        participant.setCreatedBy(internalToExternal(participantModel.createdBy()));
        participant.setUpdatedBy(internalToExternal(participantModel.updatedBy()));
        return participant;
    }

    public static UpdateParticipantModel externalToInternal(UpdateParticipant updateParticipant) {
        return new UpdateParticipantModel(EnumMapper.externalToInternal(updateParticipant.getRole()));
    }

    private static MeetingUser internalToExternal(MeetingUserModel input) {
        if (input == null) {
            return new MeetingUser();
        }
        return new MeetingUser()
                .organisationId(input.organisationId())
                .email(input.email());
    }
}