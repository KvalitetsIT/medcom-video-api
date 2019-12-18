package dk.medcom.video.api.repository;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingLabel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingLabelRepository extends CrudRepository<MeetingLabel, Long> {
    List<MeetingLabel> findByMeeting(Meeting meeting);

    void deleteByMeeting(Meeting meeting);
}
