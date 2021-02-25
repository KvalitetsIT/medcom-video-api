package dk.medcom.vdx.organisation.service;

import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.OrganisationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrganisationNameService {
    private final OrganisationRepository organisationRepository;

    public OrganisationNameService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    public Optional<Organisation> getOrganisationById(String organisationId) {
        return Optional.ofNullable(organisationRepository.findByOrganisationId(organisationId));
    }
}
