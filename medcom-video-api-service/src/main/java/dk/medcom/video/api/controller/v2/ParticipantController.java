package dk.medcom.video.api.controller.v2;

import jakarta.validation.Valid;
import org.openapitools.api.ParticipantsApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public class ParticipantController  implements ParticipantsApi  {
    @Override
    public ResponseEntity<List<SimpleMeeting>> v2MeetingDetailsGet(String participantOrganisation) {
        return null;
    }

    @Override
    public ResponseEntity<List<SimpleMeeting>> v2MeetingDetailsPost(CitizenParticipant citizenParticipant) {
        return null;
    }

    @Override
    public ResponseEntity<List<Participant>> v2MeetingsUuidParticipantsGet(UUID uuid) {
        return null;
    }

    @Override
    public ResponseEntity<Void> v2MeetingsUuidParticipantsIdDelete(UUID uuid, Long id) {
        return null;
    }

    @Override
    public ResponseEntity<Participant> v2MeetingsUuidParticipantsIdPut(UUID uuid, Long id, Participant participant) {
        return null;
    }

    @Override
    public ResponseEntity<List<Participant>> v2MeetingsUuidParticipantsPost(UUID uuid, List<@Valid Participant> participant) {
        return null;
    }
}
