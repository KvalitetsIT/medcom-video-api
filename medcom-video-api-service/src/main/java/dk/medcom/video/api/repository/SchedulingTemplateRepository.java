package dk.medcom.video.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingTemplate;

public interface SchedulingTemplateRepository extends CrudRepository<SchedulingTemplate, Long> {
	
	public List<SchedulingTemplate> findByOrganisationAndDeletedTimeIsNull(Organisation organisation);
	public List<SchedulingTemplate> findByOrganisationIsNullAndDeletedTimeIsNull();
	public List<SchedulingTemplate> findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Organisation organisation, boolean isDefaultTemplate);
	public SchedulingTemplate findByOrganisationAndIdAndDeletedTimeIsNull(Organisation organisation, Long id);

}
