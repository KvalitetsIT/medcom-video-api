package dk.medcom.video.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.Meeting;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

	public List<Meeting> findByOrganisationId(String organisationId);

	public Meeting findOneByUuid(String uuid);

}
