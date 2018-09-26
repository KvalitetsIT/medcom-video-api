//TODO: Database indexes matching requests?
package dk.medcom.video.api.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.Organisation;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

	public List<Meeting> findByOrganisationAndStartTimeBetween( Organisation organisation, Date startTime, Date endTime);

	public Meeting findOneByUuid(String uuid);

}
