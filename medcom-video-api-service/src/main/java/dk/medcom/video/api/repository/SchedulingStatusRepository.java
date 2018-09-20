package dk.medcom.video.api.repository;


import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.SchedulingStatus;

public interface SchedulingStatusRepository extends CrudRepository<SchedulingStatus, Long> {
	
}
