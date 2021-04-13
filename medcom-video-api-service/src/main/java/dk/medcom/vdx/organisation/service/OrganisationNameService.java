package dk.medcom.vdx.organisation.service;

import dk.medcom.video.api.api.ProvisionStatus;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Map<String, Organisation> getOrganisationByUriWithDomain(List<String> uri) {
        List<SchedulingInfo> dbResult = schedulingInfoRepository.findAllByUriWithDomainAndProvisionStatusOk(uri, ProvisionStatus.PROVISIONED_OK);
        return dbResult.stream().collect(Collectors.toMap(SchedulingInfo::getUriWithDomain, SchedulingInfo::getOrganisation));
    }
}
