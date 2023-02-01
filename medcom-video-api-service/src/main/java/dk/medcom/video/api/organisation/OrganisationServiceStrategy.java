package dk.medcom.video.api.organisation;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class OrganisationServiceStrategy implements OrganisationStrategy {
    private final OrganisationServiceClient client;
    private String endpoint;

    public OrganisationServiceStrategy(String endpoint) {
        this.endpoint = endpoint;
        client = new OrganisationServiceClientImpl(endpoint);
    }
    @Override
    public Organisation findOrganisationByCode(String organisationCode) {
        try {
            return client.getOrganisationByCode(organisationCode);
        }
        catch(NotFoundException e) {
            return null;
        }
    }

    @Override
    public Integer getPoolSizeForOrganisation(String code) {
        try {
            Organisation organisation = client.getOrganisationByCode(code);
            return organisation.getPoolSize() == 0 ? null : organisation.getPoolSize();
        }
        catch(NotFoundException e) {
            return null;
        }
    }

    @Override
    public List<Organisation> findByPoolSizeNotNull() {
        List<Organisation> organisationList = client.getOrganisations();

        return organisationList.stream().filter(x -> x.getPoolSize() != null && x.getPoolSize() > 0).collect(Collectors.toList());
    }
}
