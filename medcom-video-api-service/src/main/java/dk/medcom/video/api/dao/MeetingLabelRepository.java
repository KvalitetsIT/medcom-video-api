package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingLabel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingLabelRepository extends CrudRepository<MeetingLabel, Long> {
    List<MeetingLabel> findByMeeting(Meeting meeting);

    void deleteByMeeting(Meeting meeting);
}
