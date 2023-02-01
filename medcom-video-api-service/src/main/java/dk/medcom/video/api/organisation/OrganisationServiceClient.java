package dk.medcom.video.api.organisation;

import java.util.List;

public interface OrganisationServiceClient {
    Organisation getOrganisationByCode(String organisationCode);

    Organisation getOrganisationByCode(String organisationCode, boolean createFromTemplate);

    List<Organisation> getOrganisations();
}
