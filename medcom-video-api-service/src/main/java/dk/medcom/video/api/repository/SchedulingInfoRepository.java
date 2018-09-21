package dk.medcom.video.api.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.SchedulingInfo;


public interface SchedulingInfoRepository extends CrudRepository<SchedulingInfo, Long> {
	
	List<SchedulingInfo> findAll();
	
	public SchedulingInfo findOneByUuid(String uuid);
	
	public SchedulingInfo findOneByUriWithoutDomain(String UriWithoutDomain);
	
	//@Query("SELECT s FROM SchedulingInfo s JOIN s.meeting m WHERE FUNCTION(DATE_ADD(m.startTime, INTERVAL 10 MINUTE)) > ?1 AND m.endTime < ?2 AND s.provisionStatus = ?3)")
//	@Query(value = "SELECT s.* FROM scheduling_info s INNER JOIN meetings m on s.meetings_id = m.id "
//			+ "WHERE (DATE_SUB(m.start_time, INTERVAL 30 MINUTE)) > ?1 AND m.end_time < ?2 AND s.provision_status = ?3", nativeQuery = true)
	//@Query(value = "SELECT s.* FROM scheduling_info s INNER JOIN meetings m on s.meetings_id = m.id WHERE (DATE_SUB(m.start_time, INTERVAL s.vmravailable_before MINUTE)) > ?1 AND m.end_time < ?2 AND s.provision_status = ?3", nativeQuery = true)
	@Query(value = "SELECT s.* FROM scheduling_info s INNER JOIN meetings m on s.meetings_id = m.id "
			+ "WHERE (((DATE_SUB(m.start_time, INTERVAL s.vmravailable_before MINUTE)) > ?1  AND (DATE_SUB(m.start_time, INTERVAL s.vmravailable_before MINUTE)) < ?2) OR "
			+ "(m.end_time > ?1 AND m.end_time < ?2 )) "
			+ "AND s.provision_status = ?3", nativeQuery = true)
	public List<SchedulingInfo> findAllWithinAdjustedTimeIntervalAndStatus(Date fromStartTime, Date toEndTime, int provisionStatus);
	
}
