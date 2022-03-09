package dk.medcom.video.api.dao;

import dk.medcom.video.api.api.ProvisionStatus;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public interface SchedulingInfoRepository extends CrudRepository<SchedulingInfo, Long> {
	List<SchedulingInfo> findAll();

	@Query(value = "select * from scheduling_info where uuid = ?1 for update", nativeQuery = true)
	SchedulingInfo findOneByUuid(String uuid);

	SchedulingInfo findOneByUriWithoutDomainAndUriDomain(String UriWithoutDomain, String uriDomain);
	
	@Query("SELECT s FROM SchedulingInfo s INNER JOIN s.meeting m WHERE ((s.vMRStartTime > ?1 and s.vMRStartTime < ?2) OR (m.endTime > ?1 and m.endTime < ?2)) AND s.provisionStatus = ?3")
	List<SchedulingInfo> findAllWithinAdjustedTimeIntervalAndStatus(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus);

	@Query("SELECT s FROM SchedulingInfo s INNER JOIN s.meeting m WHERE s.vMRStartTime <= ?1 AND m.endTime >= ?1 AND s.provisionStatus = ?2")
	List<SchedulingInfo> findAllWithinStartAndEndTimeLessThenAndStatus(Date fromStartTime, ProvisionStatus provisionStatus);

	@Query("SELECT s FROM SchedulingInfo s INNER JOIN s.meeting m WHERE m.endTime < ?1 AND s.provisionStatus = ?2")
	List<SchedulingInfo> findAllWithinEndTimeLessThenAndStatus(Date toEndTime, ProvisionStatus provisionStatus);

	@Query(value = "SELECT s.id FROM scheduling_info s WHERE (s.organisation_id = ?1 AND s.provision_status = ?2 AND s.meetings_id IS NULL AND s.vmr_type = ?4 AND s.host_view = ?5 AND s.guest_view = ?6 AND s.vmr_quality = ?7 AND s.enable_overlay_text = ?8 AND s.guests_can_present = ?9 AND s.force_presenter_into_main = ?10 AND s.force_encryption = ?11 AND s.mute_all_guests = ?12) and reservation_id is null and provision_timestamp < ?3 LIMIT 1 FOR UPDATE", nativeQuery=true)
	List<BigInteger> findByMeetingIsNullAndOrganisationAndProvisionStatus(Long organisationId, String provisionStatus, Date provisionTimestampOlderThen, String vmrType, String hostView, String guestView, String vmrQuality, Boolean enableOverlayText, Boolean guestsCanPresent, Boolean forcePresenterIntoMain, Boolean forceEncryption, Boolean muteAllGuests);

    List<SchedulingInfo> findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus provisionStatus);

	@Query(value = "SELECT * FROM scheduling_info s WHERE s.reservation_id = ?1 and meetings_id is null for update", nativeQuery=true)
    SchedulingInfo findOneByReservationId(String reservationId);

	@Query("SELECT s FROM SchedulingInfo s WHERE s.provisionStatus = ?2 AND s.uriWithDomain IN (?1)")
	List<SchedulingInfo> findAllByUriWithDomainAndProvisionStatusOk(List<String> uri, ProvisionStatus provisionStatus);
}
