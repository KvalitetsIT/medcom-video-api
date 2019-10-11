package dk.medcom.video.api.repository;

import java.util.Date;
import java.util.List;

import dk.medcom.video.api.dao.Organisation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.ProvisionStatus;


public interface SchedulingInfoRepository extends CrudRepository<SchedulingInfo, Long> {
	
	List<SchedulingInfo> findAll();
	
	public SchedulingInfo findOneByUuid(String uuid);
	
	public SchedulingInfo findOneByUriWithoutDomain(String UriWithoutDomain);
	
	@Query("SELECT s FROM SchedulingInfo s INNER JOIN s.meeting m WHERE ((s.vMRStartTime > ?1 and s.vMRStartTime < ?2) OR (m.endTime > ?1 and m.endTime < ?2)) AND s.provisionStatus = ?3")
	public List<SchedulingInfo> findAllWithinAdjustedTimeIntervalAndStatus(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus);

	List<SchedulingInfo> findByMeetingIsNullAndOrganisationAndProvisionStatus(Organisation organisation, ProvisionStatus provisionStatus);

    List<SchedulingInfo> findByMeetingIsNullAndProvisionStatus(ProvisionStatus provisionStatus);
}
