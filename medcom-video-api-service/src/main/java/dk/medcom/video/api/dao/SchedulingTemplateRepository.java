package dk.medcom.video.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import dk.medcom.video.api.dao.entity.SchedulingTemplate;

public interface SchedulingTemplateRepository extends CrudRepository<SchedulingTemplate, Long> {
	List<SchedulingTemplate> findByOrganisationCodeAndDeletedTimeIsNull(String organisationCode);
	List<SchedulingTemplate> findByOrganisationCodeIsNullAndDeletedTimeIsNull();
	List<SchedulingTemplate> findByOrganisationCodeAndIsDefaultTemplateAndDeletedTimeIsNull(String organisationCode, boolean isDefaultTemplate);
	List<SchedulingTemplate> findByOrganisationCodeAndIsPoolTemplateAndDeletedTimeIsNull(String organisationCode, boolean isPoolTemplate);

	SchedulingTemplate findByOrganisationCodeAndIdAndDeletedTimeIsNull(String organisationCode, Long id);

	@Query(value = "SELECT st.* from scheduling_template st where st.organisation_code = ?1 and st.is_default_template = true and st.deleted_time is null", nativeQuery=true)
	List<SchedulingTemplate> findByOrganisationIdAndIsDefaultTemplateAndDeletedTimeIsNull(String organisationId);
}
