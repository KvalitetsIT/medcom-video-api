package dk.medcom.video.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingTemplate;

public interface SchedulingTemplateRepository extends CrudRepository<SchedulingTemplate, Long> {
	
	public List<SchedulingTemplate> findByOrganisation(Organisation organisation);
	public List<SchedulingTemplate> findByOrganisationIsNull();

}
