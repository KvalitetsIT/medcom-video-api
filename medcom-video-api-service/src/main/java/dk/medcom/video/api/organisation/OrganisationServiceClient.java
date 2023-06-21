package dk.medcom.video.api.organisation;

import dk.medcom.video.api.organisation.model.Organisation;

import java.util.List;

public interface OrganisationServiceClient {
    Organisation getOrganisationByCode(String organisationCode);

    Organisation createOrganisation(String parentOrganisation, Organisation organisation);

    List<Organisation> getOrganisations();
}
