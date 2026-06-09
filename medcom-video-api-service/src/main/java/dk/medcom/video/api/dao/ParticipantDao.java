package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.Participant;

import java.util.List;
import java.util.Optional;

public interface ParticipantDao {
    Participant save(Participant participant);
    Optional<Participant> findById(Long id);
    List<Participant> findByMeeting(Meeting meeting);
    long count();
    void deleteById(Long id);
    void delete(Participant participant);
}