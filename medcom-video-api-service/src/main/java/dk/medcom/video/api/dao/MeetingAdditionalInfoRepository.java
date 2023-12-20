package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.MeetingAdditionalInfo;
import dk.medcom.video.api.dao.entity.Meeting;
import org.springframework.data.repository.CrudRepository;

public interface MeetingAdditionalInfoRepository extends CrudRepository<MeetingAdditionalInfo, Long> {
    void deleteByMeeting(Meeting meeting);
}
