package dk.medcom.video.api.repository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import dk.medcom.video.api.dao.SchedulingStatus;

public interface SchedulingStatusRepository extends CrudRepository<SchedulingStatus, Long> {
	
	@Modifying
	@Transactional
//	@Query("DELETE FROM SchedulingStatus s WHERE s.meetingId = ?1")
//	public void deleteByMeetingId(Long meetingId);
	
	public Long deleteByMeetingId(Long meetingId);
}
