package dk.medcom.video.api.organisation;

import dk.medcom.video.api.organisation.model.OrganisationTree;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.UriBuilder;

public class OrganisationTreeServiceClientImpl implements OrganisationTreeServiceClient {
    private final WebTarget baseTarget;

    public OrganisationTreeServiceClientImpl(String endpoint) {
        this.baseTarget = ClientBuilder.newClient()
                .target(UriBuilder.fromPath(endpoint));
    }

    @Override
    public OrganisationTree getOrganisationTree(String organisationCode) {
        return baseTarget
                .path("services")
                .path("organisationtree")
                .queryParam("organisationCode", organisationCode)
                .request()
                .get(OrganisationTree.class);
    }
}
