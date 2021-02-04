package dk.medcom.video.api.service;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.api.ProvisionStatus;
import dk.medcom.video.api.api.SchedulingTemplateDto;
import dk.medcom.video.api.entity.PoolInfoEntity;
import dk.medcom.video.api.organisation.Organisation;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PoolInfoService {
    private OrganisationStrategy organisationStrategy;
    private SchedulingInfoRepository schedulingInfoRepository;
    private SchedulingTemplateRepository schedulingTemplateRepository;
    private OrganisationRepository organisationRepository;
    private PoolInfoRepository poolInfoRepository;

    PoolInfoService(OrganisationRepository organisationRepository, SchedulingInfoRepository schedulingInfoRepository, SchedulingTemplateRepository schedulingTemplateRepository, OrganisationStrategy organisationStrategy, PoolInfoRepository poolInfoRepository) {
        this.organisationStrategy = organisationStrategy;
        this.schedulingInfoRepository = schedulingInfoRepository;
        this.schedulingTemplateRepository = schedulingTemplateRepository;
        this.organisationRepository = organisationRepository;
        this.poolInfoRepository = poolInfoRepository;
    }

    public List<PoolInfoEntity> getAllPoolInfo() {
    	return poolInfoRepository.getPoolInfos();
    }
    
    public List<PoolInfoDto> getPoolInfo() {
    	
        List<Organisation> organizations = organisationStrategy.findByPoolSizeNotNull();
        List<SchedulingInfo> schedulingInfos = schedulingInfoRepository.findByMeetingIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK);

        return mapPoolInfo(organizations, schedulingInfos);
    }

    private List<PoolInfoDto> mapPoolInfo(List<Organisation> organizations, List<SchedulingInfo> schedulingInfos) {
        return organizations.stream().map( o -> {
            PoolInfoDto poolInfo = new PoolInfoDto();
            poolInfo.setDesiredPoolSize(o.getPoolSize());
            poolInfo.setOrganizationId(o.getCode());

            poolInfo.setAvailablePoolSize((int) schedulingInfos.stream().filter(x -> x.getOrganisation().getOrganisationId().equals(o.getCode())).count());

            poolInfo.setSchedulingTemplate(getSchedulingTemplate(o));
            
            return poolInfo;
        }).collect(Collectors.toList());
    }

    private SchedulingTemplateDto getSchedulingTemplate(Organisation o) {
        dk.medcom.video.api.dao.entity.Organisation org = organisationRepository.findByOrganisationId(o.getCode());
        if(org != null) {
            List<SchedulingTemplate> schedulingTemplates = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(org, true);

            if(schedulingTemplates != null && schedulingTemplates.size() > 0) {
                return mapSchedulingTemplate(schedulingTemplates.get(0));
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
