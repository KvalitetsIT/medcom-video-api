package dk.medcom.video.api.organisation;

import dk.medcom.video.api.organisation.model.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.UriBuilder;
import java.util.List;

public class OrganisationServiceClientImpl implements OrganisationServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationServiceClientImpl.class);
    private final String endpoint;

    public OrganisationServiceClientImpl(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public Organisation getOrganisationByCode(String organisationCode) {
        return ClientBuilder.newClient()
                .target(UriBuilder.fromPath(endpoint))
                .path("organisation")
                .queryParam("organisationCode", organisationCode)
                .request()
                .get(Organisation.class);
    }

    @Override
    public Organisation createOrganisation(String parentOrganisation, Organisation organisation) {
        try {
            return ClientBuilder.newClient()
                    .target(UriBuilder.fromPath(endpoint))
                    .path("organisation")
                    .queryParam("parent_code", parentOrganisation)
                    .request()
                    .post(Entity.json(organisation), Organisation.class);
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
                .get(new GenericType<>() {
                });
    }
}
