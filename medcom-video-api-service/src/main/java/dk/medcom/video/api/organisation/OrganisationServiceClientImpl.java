package dk.medcom.video.api.organisation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

public class OrganisationServiceClientImpl implements OrganisationServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationServiceClientImpl.class);
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

    @Override
    public Organisation getOrganisationByCode(String organisationCode, boolean createFromTemplate) {
        try {
            return ClientBuilder.newClient()
                    .target(UriBuilder.fromPath(endpoint))
                    .path("organisation")
                    .queryParam("from_template", createFromTemplate)
                    .path(organisationCode)
                    .request()
                    .get(Organisation.class);
        }
        catch(NotFoundException e) {
            logger.info("Organisation not found");
            return null;
        }
    }

    public List<Organisation> getOrganisations() {
        return ClientBuilder.newClient()
                .target(UriBuilder.fromPath(endpoint))
                .path("organisation")
                .request()
                .get(new GenericType<List<Organisation>>(){});
    }
}
