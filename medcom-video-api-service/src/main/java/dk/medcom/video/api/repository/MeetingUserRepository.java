package dk.medcom.video.api.repository;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.MeetingUser;

public interface MeetingUserRepository extends CrudRepository<MeetingUser, Long> {

	public MeetingUser findOneByOrganisationIdAndEmail(String organisationId, String email);

}
