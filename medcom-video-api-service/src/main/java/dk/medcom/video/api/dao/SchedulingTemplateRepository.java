package dk.medcom.video.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;

public interface SchedulingTemplateRepository extends CrudRepository<SchedulingTemplate, Long> {
	List<SchedulingTemplate> findByOrganisationAndDeletedTimeIsNull(Organisation organisation);
	List<SchedulingTemplate> findByOrganisationIsNullAndDeletedTimeIsNull();
	List<SchedulingTemplate> findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Organisation organisation, boolean isDefaultTemplate);
	List<SchedulingTemplate> findByOrganisationAndIsPoolTemplateAndDeletedTimeIsNull(Organisation organisation, boolean isPoolTemplate);

	SchedulingTemplate findByOrganisationAndIdAndDeletedTimeIsNull(Organisation organisation, Long id);

	@Query(value = "SELECT st.* from scheduling_template st, organisation o where st.organisation_id = o.id and o.organisation_id = ?1 and st.is_default_template = true and st.deleted_time is null", nativeQuery=true)
	List<SchedulingTemplate> findByOrganisationIdAndIsDefaultTemplateAndDeletedTimeIsNull(String organisationId);
}
