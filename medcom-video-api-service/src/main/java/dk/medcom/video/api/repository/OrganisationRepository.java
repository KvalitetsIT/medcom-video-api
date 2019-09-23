package dk.medcom.video.api.repository;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.Organisation;

import java.util.List;

public interface OrganisationRepository extends CrudRepository<Organisation, Long> {
	
	public Organisation findByOrganisationId(String organisationId);

	public List<Organisation> findByPoolSizeNotNull();
}
