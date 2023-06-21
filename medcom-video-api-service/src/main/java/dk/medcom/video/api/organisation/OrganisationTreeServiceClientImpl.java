package dk.medcom.video.api.organisation;

import dk.medcom.video.api.organisation.model.OrganisationTree;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;

public class OrganisationTreeServiceClientImpl implements OrganisationTreeServiceClient {
    private final String endpoint;

    public OrganisationTreeServiceClientImpl(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public OrganisationTree getOrganisationTree(String organisationCode) {
        return ClientBuilder.newClient()
                .target(UriBuilder.fromPath(endpoint))
                .path("services")
                .path("organisationtree")
                .queryParam("organisationCode", organisationCode)
                .request()
                .get(new GenericType<OrganisationTree>(){});
    }
}
