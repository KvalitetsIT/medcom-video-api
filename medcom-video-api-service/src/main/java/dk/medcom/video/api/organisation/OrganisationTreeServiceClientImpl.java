package dk.medcom.video.api.organisation;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

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
                .path(URLEncoder.encode(organisationCode, Charset.defaultCharset()))
                .request()
                .get(new GenericType<>() {
                });
    }
}
