package dk.medcom.video.api.organisation;

import dk.medcom.video.api.dao.OrganisationRepository;

import java.util.ArrayList;
import java.util.List;

public class OrganisationDatabaseStrategy implements OrganisationStrategy {
    private OrganisationRepository organisationRepository;

    public OrganisationDatabaseStrategy(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    @Override
    public Organisation findOrganisationByCode(String organisationCode) {
        dk.medcom.video.api.dao.entity.Organisation organisation = organisationRepository.findByOrganisationId(organisationCode);

        if(organisation != null) {
            Organisation returnOrganisation = new Organisation();
            returnOrganisation.setPoolSize(organisation.getPoolSize());
            returnOrganisation.setCode(organisation.getOrganisationId());

            return returnOrganisation;
        }

        return null;
    }

    @Override
    public Integer getPoolSizeForOrganisation(String userOrganisation) {
        dk.medcom.video.api.dao.entity.Organisation organisation = organisationRepository.findByOrganisationId(userOrganisation);

        return organisation != null ? organisation.getPoolSize() : null;
    }

    @Override
    public List<Organisation> findByPoolSizeNotNull() {
        List<dk.medcom.video.api.dao.entity.Organisation> organisations = organisationRepository.findByPoolSizeNotNull();

        List<Organisation> returnOrgs = new ArrayList<>();
        organisations.forEach( x -> {
            Organisation organisation = new Organisation();
            organisation.setCode(x.getOrganisationId());
            organisation.setPoolSize(x.getPoolSize());

            returnOrgs.add(organisation);
        });

        return returnOrgs;
    }
}
