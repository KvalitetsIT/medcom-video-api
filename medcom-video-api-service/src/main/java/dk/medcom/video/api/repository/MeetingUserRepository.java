package dk.medcom.video.api.repository;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.dao.Organisation;

public interface MeetingUserRepository extends CrudRepository<MeetingUser, Long> {

	public MeetingUser findOneByOrganisationAndEmail(Organisation organisation, String email);

}
