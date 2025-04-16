package dk.medcom.video.api.service;

import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.api.SchedulingTemplateDto;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.dao.entity.PoolInfoEntity;
import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.organisation.OrganisationStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PoolInfoServiceImpl implements PoolInfoService {
    private final OrganisationStrategy organisationStrategy;
    private final SchedulingInfoRepository schedulingInfoRepository;
    private final SchedulingTemplateRepository schedulingTemplateRepository;
    private final OrganisationRepository organisationRepository;
    private final PoolInfoRepository poolInfoRepository;

    public PoolInfoServiceImpl(OrganisationRepository organisationRepository, SchedulingInfoRepository schedulingInfoRepository, SchedulingTemplateRepository schedulingTemplateRepository, OrganisationStrategy organisationStrategy, PoolInfoRepository poolInfoRepository) {
        this.organisationStrategy = organisationStrategy;
        this.schedulingInfoRepository = schedulingInfoRepository;
        this.schedulingTemplateRepository = schedulingTemplateRepository;
        this.organisationRepository = organisationRepository;
        this.poolInfoRepository = poolInfoRepository;
    }

    @Override
    public List<PoolInfoEntity> getAllPoolInfo() {
    	return poolInfoRepository.getPoolInfos();
    }

    @Override
    public List<PoolInfoDto> getPoolInfo() {
        List<Organisation> organizations = organisationStrategy.findByPoolSizeNotNull();
        List<SchedulingInfo> schedulingInfos = schedulingInfoRepository.findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK);

        return mapPoolInfo(organizations, schedulingInfos);
    }

    private List<PoolInfoDto> mapPoolInfo(List<Organisation> organizations, List<SchedulingInfo> schedulingInfos) {
        return organizations.stream().map( o -> {
            PoolInfoDto poolInfo = new PoolInfoDto();
            poolInfo.setDesiredPoolSize(o.getPoolSize());
            poolInfo.setOrganizationId(o.getCode());

            poolInfo.setAvailablePoolSize((int) schedulingInfos.stream().filter(x -> x.getOrganisation().getOrganisationId().equals(o.getCode())).count());

            poolInfo.setSchedulingTemplate(getSchedulingTemplate(o));

            poolInfo.setSchedulingInfoList(schedulingInfos.stream().filter(x -> x.getOrganisation().getOrganisationId().equals(o.getCode())).sorted(Comparator.comparing(SchedulingInfo::getCreatedTime).reversed()).toList());

            return poolInfo;
        }).collect(Collectors.toList());
    }

    private SchedulingTemplateDto getSchedulingTemplate(Organisation o) {
        dk.medcom.video.api.dao.entity.Organisation org = organisationRepository.findByOrganisationId(o.getCode());
        if(org != null) {
            List<SchedulingTemplate> schedulingTemplatesPool = schedulingTemplateRepository.findByOrganisationAndIsPoolTemplateAndDeletedTimeIsNull(org, true);
            if (schedulingTemplatesPool != null && !schedulingTemplatesPool.isEmpty()) {
                return mapSchedulingTemplate(schedulingTemplatesPool.getFirst());
            }

            List<SchedulingTemplate> schedulingTemplatesDefault = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(org, true);

            if(schedulingTemplatesDefault != null && !schedulingTemplatesDefault.isEmpty()) {
                return mapSchedulingTemplate(schedulingTemplatesDefault.getFirst());
            }
        }

        return null;
    }

    private SchedulingTemplateDto mapSchedulingTemplate(SchedulingTemplate schedulingTemplate) {
        try {
            return new SchedulingTemplateDto(schedulingTemplate);
        } catch (PermissionDeniedException e) {
            throw new RuntimeException("Error mapping SchedulingTemplate.", e);
        }
    }
}
