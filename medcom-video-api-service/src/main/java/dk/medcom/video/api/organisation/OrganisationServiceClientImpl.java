package dk.medcom.video.api.organisation;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

public class OrganisationServiceClientImpl implements OrganisationServiceClient {

    private String endpoint;

    public OrganisationServiceClientImpl(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public Organisation getOrganisationByCode(String organisationCode) {
        return ClientBuilder.newClient()
                .target(UriBuilder.fromPath(endpoint))
                .path("organisation")
                .path(organisationCode)
                .request()
                .get(Organisation.class);
    }

    public List<Organisation> getOrganisations() {
        return ClientBuilder.newClient()
                .target(UriBuilder.fromPath(endpoint))
                .path("organisation")
                .request()
                .get(new GenericType<List<Organisation>>(){});
    }
}
