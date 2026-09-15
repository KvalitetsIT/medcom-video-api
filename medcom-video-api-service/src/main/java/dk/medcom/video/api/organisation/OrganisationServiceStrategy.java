package dk.medcom.video.api.organisation;

import dk.medcom.video.api.organisation.model.Organisation;

import java.util.List;
import java.util.stream.Collectors;

public class OrganisationServiceStrategy implements OrganisationStrategy {
    private final OrganisationServiceClientV2 organisationServiceClientV2;
    private final OrganisationServiceClient organisationServiceClient;

    public OrganisationServiceStrategy(OrganisationServiceClientV2 organisationServiceClientV2, OrganisationServiceClient organisationServiceClient) {
        this.organisationServiceClientV2 = organisationServiceClientV2;
        this.organisationServiceClient = organisationServiceClient;
    }

    @Override
    public Organisation findOrganisationByCode(String organisationCode) {
        return organisationServiceClientV2.getOrganisationByCode(organisationCode);
    }

    @Override
    public Integer getPoolSizeForOrganisation(String code) {
        Organisation organisation = organisationServiceClientV2.getOrganisationByCode(code);
        if(organisation == null) {
            return null;
        }

        return organisation.getPoolSize() == 0 ? null : organisation.getPoolSize();
    }

    @Override
    public List<Organisation> findByPoolSizeNotNull() {
        List<Organisation> organisationList = organisationServiceClient.getOrganisations();

        return organisationList.stream().filter(x -> x.getPoolSize() != null && x.getPoolSize() > 0).collect(Collectors.toList());
    }
}
