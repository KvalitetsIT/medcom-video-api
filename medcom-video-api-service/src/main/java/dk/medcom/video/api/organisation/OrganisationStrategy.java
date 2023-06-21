package dk.medcom.video.api.organisation;

import dk.medcom.video.api.organisation.model.Organisation;

import java.util.List;

public interface OrganisationStrategy {
    Organisation findOrganisationByCode(String organisationCode);

    Integer getPoolSizeForOrganisation(String userOrganisation);

    List<Organisation> findByPoolSizeNotNull();
}
