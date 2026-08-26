package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface SchedulingInfoRepository extends CrudRepository<SchedulingInfo, Long> {
	List<SchedulingInfo> findAll();

	@Query(value = "select * from scheduling_info where uuid = ?1 for update", nativeQuery = true)
	SchedulingInfo findOneByUuid(String uuid);

	SchedulingInfo findOneByUriWithoutDomainAndUriDomain(String UriWithoutDomain, String uriDomain);
	
	@Query("SELECT s FROM SchedulingInfo s INNER JOIN s.meeting m WHERE ((s.vMRStartTime > ?1 and s.vMRStartTime < ?2) OR (m.endTime > ?1 and m.endTime < ?2)) AND s.provisionStatus = ?3")
	List<SchedulingInfo> findAllWithinAdjustedTimeIntervalAndStatus(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus);

	@Query("SELECT s FROM SchedulingInfo s INNER JOIN s.meeting m WHERE ((s.vMRStartTime > ?1 and s.vMRStartTime < ?2) OR (m.endTime > ?1 and m.endTime < ?2)) AND s.provisionStatus = ?3 AND s.organisation.organisationId IN (?4)")
	List<SchedulingInfo> findAllWithinAdjustedTimeIntervalAndStatusAndOrganisations(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus, Set<String> organisationIds);

	@Query("SELECT s FROM SchedulingInfo s INNER JOIN s.meeting m WHERE s.vMRStartTime <= ?1 AND m.endTime >= ?1 AND s.provisionStatus = ?2")
	List<SchedulingInfo> findAllWithinStartAndEndTimeLessThenAndStatus(Date fromStartTime, ProvisionStatus provisionStatus);

	@Query("SELECT s FROM SchedulingInfo s INNER JOIN s.meeting m WHERE m.endTime < ?1 AND s.provisionStatus = ?2")
	List<SchedulingInfo> findAllWithinEndTimeLessThenAndStatus(Date toEndTime, ProvisionStatus provisionStatus);

	@Query(value = """
            SELECT * FROM scheduling_info s \
             WHERE (s.organisation_id = :organisationId\s
               AND s.provision_status = :provisionStatus\s
               AND s.meetings_id IS NULL\s
               AND ifnull(:vmrType, '__UNDEFINED__') in ('__UNDEFINED__', s.vmr_type)\s
               AND ifnull(:hostView, '__UNDEFINED__') in ('__UNDEFINED__', s.host_view)\s
               AND ifnull(:guestView, '__UNDEFINED__') in ('__UNDEFINED__', s.guest_view)\s
               AND ifnull(:vmrQuality, '__UNDEFINED__') in ('__UNDEFINED__', s.vmr_quality)\s
               AND ifnull(:callType, '__UNDEFINED__') in ('__UNDEFINED__', s.call_type)\s
               AND ifnull(:enableOverlayText, -1) in (-1, s.enable_overlay_text)
               AND ifnull(:guestsCanPresent, -1) in (-1, s.guests_can_present)
               AND ifnull(:forcePresenterIntoMain, -1) in (-1, s.force_presenter_into_main)
               AND ifnull(:forceEncryption, -1) in (-1, s.force_encryption)
               AND ifnull(:muteAllGuests, -1) in (-1, s.mute_all_guests))
               and s.reservation_id is null\s
               and s.provision_timestamp < :provisionTimestampOlderThen LIMIT 1 FOR UPDATE""", nativeQuery = true)
	List<SchedulingInfo> findByMeetingIsNullAndOrganisationAndProvisionStatus(
			@Param("organisationId") Long organisationId,
			@Param("provisionStatus") String provisionStatus,
			@Param("provisionTimestampOlderThen") Date provisionTimestampOlderThen,
			@Param("vmrType") String vmrType,
			@Param("hostView") String hostView,
			@Param("guestView") String guestView,
			@Param("vmrQuality") String vmrQuality,
			@Param("callType") String callType,
			@Param("enableOverlayText") Boolean enableOverlayText,
			@Param("guestsCanPresent") Boolean guestsCanPresent,
			@Param("forcePresenterIntoMain") Boolean forcePresenterIntoMain,
			@Param("forceEncryption") Boolean forceEncryption,
			@Param("muteAllGuests") Boolean muteAllGuests
	);

    List<SchedulingInfo> findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus provisionStatus);

	@Query(value = "SELECT * FROM scheduling_info s WHERE s.reservation_id = ?1 and meetings_id is null for update", nativeQuery=true)
    SchedulingInfo findOneByReservationId(String reservationId);

	@Query("SELECT s FROM SchedulingInfo s WHERE s.provisionStatus = ?2 AND s.uriWithDomain IN (?1)")
	List<SchedulingInfo> findAllByUriWithDomainAndProvisionStatusOk(List<String> uri, ProvisionStatus provisionStatus);

	SchedulingInfo findOneByMeeting(Meeting meeting);
}

