package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.MeetingAdditionalInfo;
import dk.medcom.video.api.dao.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingAdditionalInfoRepository extends JpaRepository<MeetingAdditionalInfo, Long> {
    void deleteByMeeting(Meeting meeting);
}
