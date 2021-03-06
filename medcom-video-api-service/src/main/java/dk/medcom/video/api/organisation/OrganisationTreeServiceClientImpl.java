package dk.medcom.video.api.organisation;

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
                .path(organisationCode)
                .request()
                .get(new GenericType<OrganisationTree>(){});
    }
}
