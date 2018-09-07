package dk.medcom.video.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.Organisation;

public interface OrganisationRepository extends CrudRepository<Organisation, Long> {
	
	public List<Organisation> findByOrganisationId(String organisationId);

}
