package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.repository.OrganisationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrganisationNameService {
    private OrganisationRepository organisationRepository;

    public OrganisationNameService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    public Optional<Organisation> getOrganisationById(String organisationId) {
        return Optional.ofNullable(organisationRepository.findByOrganisationId(organisationId));
    }
}
