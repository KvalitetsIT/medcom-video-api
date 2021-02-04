package dk.medcom.video.api.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;

public interface SchedulingTemplateRepository extends CrudRepository<SchedulingTemplate, Long> {
	List<SchedulingTemplate> findByOrganisationAndDeletedTimeIsNull(Organisation organisation);
	List<SchedulingTemplate> findByOrganisationIsNullAndDeletedTimeIsNull();
	List<SchedulingTemplate> findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Organisation organisation, boolean isDefaultTemplate);
	SchedulingTemplate findByOrganisationAndIdAndDeletedTimeIsNull(Organisation organisation, Long id);
}
