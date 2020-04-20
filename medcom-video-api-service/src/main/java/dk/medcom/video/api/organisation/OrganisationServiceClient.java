package dk.medcom.video.api.organisation;

import java.util.List;

public interface OrganisationServiceClient {
    Organisation getOrganisationByCode(String organisationCode);
    List<Organisation> getOrganisations();
}
