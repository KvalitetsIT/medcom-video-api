package dk.medcom.video.api.repository;

//import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.MeetingUser;

public interface MeetingUserRepository extends CrudRepository<MeetingUser, Long> {

	//public List<Meeting> findByOrganisationId(String organisationId);

	public MeetingUser findOneByOrganisationIdAndEmail(String organisationId, String email);
	//TODO: kontroller metodenavn for auto sql

}
