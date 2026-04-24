package dk.medcom.video.api.organisation;

import dk.medcom.video.api.organisation.model.OrganisationTree;

public interface OrganisationTreeServiceClient {
    OrganisationTree getOrganisationTree(String organisationCode);
}
