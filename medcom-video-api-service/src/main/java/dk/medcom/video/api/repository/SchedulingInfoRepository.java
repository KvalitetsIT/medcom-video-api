package dk.medcom.video.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.SchedulingInfo;

public interface SchedulingInfoRepository extends CrudRepository<SchedulingInfo, Long> {
	
	List<SchedulingInfo> findAll();
	
	public SchedulingInfo findOneByUuid(String uuid);
}
