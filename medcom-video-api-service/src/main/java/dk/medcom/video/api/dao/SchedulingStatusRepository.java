package dk.medcom.video.api.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import dk.medcom.video.api.dao.entity.SchedulingStatus;

public interface SchedulingStatusRepository extends CrudRepository<SchedulingStatus, Long> {

	@Modifying
	@Transactional
	Long deleteByMeetingId(Long meetingId);
}
