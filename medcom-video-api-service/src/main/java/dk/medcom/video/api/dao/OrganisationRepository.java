package dk.medcom.video.api.dao;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.entity.Organisation;

import java.util.List;

public interface OrganisationRepository extends CrudRepository<Organisation, Long> {
	
	Organisation findByOrganisationId(String organisationId);
	List<Organisation> findByPoolSizeNotNull();
}
