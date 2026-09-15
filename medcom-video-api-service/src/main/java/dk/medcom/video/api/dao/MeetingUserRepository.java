package dk.medcom.video.api.dao;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.entity.MeetingUser;

public interface MeetingUserRepository extends CrudRepository<MeetingUser, Long> {
	MeetingUser findOneByOrganisationCodeAndEmail(String organisationCode, String email);
}
