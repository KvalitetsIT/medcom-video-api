package dk.medcom.video.api.dao;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Organisation;

public interface MeetingUserRepository extends CrudRepository<MeetingUser, Long> {
	MeetingUser findOneByOrganisationAndEmail(Organisation organisation, String email);
}
