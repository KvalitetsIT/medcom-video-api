package dk.medcom.vdx.organisation.service;

import dk.medcom.video.api.api.ProvisionStatus;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrganisationNameService {
    private final OrganisationRepository organisationRepository;
    private final SchedulingInfoRepository schedulingInfoRepository;

    public OrganisationNameService(OrganisationRepository organisationRepository, SchedulingInfoRepository schedulingInfoRepository) {
        this.organisationRepository = organisationRepository;
        this.schedulingInfoRepository = schedulingInfoRepository;
    }

    public Optional<Organisation> getOrganisationById(String organisationId) {
        return Optional.ofNullable(organisationRepository.findByOrganisationId(organisationId));
    }

    public Organisation getOrganisationByUriWithDomain(String uri) {
        SchedulingInfo schedulingInfo = schedulingInfoRepository.findOneByUriWithDomainAndProvisionStatusOk(uri, ProvisionStatus.PROVISIONED_OK);

        if (schedulingInfo == null){
            return null;
        }
        return schedulingInfo.getOrganisation();
    }
}
