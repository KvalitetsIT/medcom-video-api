package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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

	@Query(value = "SELECT * FROM scheduling_info s \n" +
			" WHERE (s.organisation_id = ?1 \n" +
			"   AND s.provision_status = ?2 \n" +
			"   AND s.meetings_id IS NULL \n" +
			"   AND ifnull(?4, '__UNDEFINED__') in ('__UNDEFINED__', s.vmr_type) \n" +
			"   AND ifnull(?5, '__UNDEFINED__') in ('__UNDEFINED__', s.host_view) \n" +
			"   AND ifnull(?6, '__UNDEFINED__') in ('__UNDEFINED__', s.guest_view) \n" +
			"   AND ifnull(?7, '__UNDEFINED__') in ('__UNDEFINED__', s.vmr_quality) \n" +
			"   AND ifnull(?8, -1) in (-1, s.enable_overlay_text)\n" +
			"   AND ifnull(?9, -1) in (-1, s.guests_can_present)\n" +
			"   AND ifnull(?10, -1) in (-1, s.force_presenter_into_main)\n" +
			"   AND ifnull(?11, -1) in (-1, s.force_encryption)\n" +
			"   AND ifnull(?12, -1) in (-1, s.mute_all_guests))\n" +
			"   and reservation_id is null \n" +
			"   and provision_timestamp < ?3 LIMIT 1 FOR UPDATE", nativeQuery=true)
	List<SchedulingInfo> findByMeetingIsNullAndOrganisationAndProvisionStatus(Long organisationId, String provisionStatus, Date provisionTimestampOlderThen, String vmrType, String hostView, String guestView, String vmrQuality, Boolean enableOverlayText, Boolean guestsCanPresent, Boolean forcePresenterIntoMain, Boolean forceEncryption, Boolean muteAllGuests);

    List<SchedulingInfo> findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus provisionStatus);

	@Query(value = "SELECT * FROM scheduling_info s WHERE s.reservation_id = ?1 and meetings_id is null for update", nativeQuery=true)
    SchedulingInfo findOneByReservationId(String reservationId);

	@Query("SELECT s FROM SchedulingInfo s WHERE s.provisionStatus = ?2 AND s.uriWithDomain IN (?1)")
	List<SchedulingInfo> findAllByUriWithDomainAndProvisionStatusOk(List<String> uri, ProvisionStatus provisionStatus);
}
