package dk.medcom.video.api.service;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.PoolInfoDto;
import dk.medcom.video.api.dto.SchedulingTemplateDto;
import dk.medcom.video.api.repository.OrganisationRepository;
import dk.medcom.video.api.repository.SchedulingInfoRepository;
import dk.medcom.video.api.repository.SchedulingTemplateRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PoolInfoService {
    private OrganisationRepository organizationRepository;
    private SchedulingInfoRepository schedulingInfoRepository;
    private SchedulingTemplateRepository schedulingTemplateRepository;

    PoolInfoService(OrganisationRepository organizationRepository, SchedulingInfoRepository schedulingInfoRepository, SchedulingTemplateRepository schedulingTemplateRepository) {
        this.organizationRepository = organizationRepository;
        this.schedulingInfoRepository = schedulingInfoRepository;
        this.schedulingTemplateRepository = schedulingTemplateRepository;
    }

    public List<PoolInfoDto> getPoolInfo() {
        List<Organisation> organizations = organizationRepository.findByPoolSizeNotNull();
        List<SchedulingInfo> schedulingInfos = schedulingInfoRepository.findByMeetingIsNull();

        return mapPoolInfo(organizations, schedulingInfos);
    }

    private List<PoolInfoDto> mapPoolInfo(List<Organisation> organizations, List<SchedulingInfo> schedulingInfos) {
        return organizations.stream().map( o -> {
            PoolInfoDto poolInfo = new PoolInfoDto();
            poolInfo.setDesiredPoolSize(o.getPoolSize());
            poolInfo.setOrganizationId(o.getOrganisationId());

            poolInfo.setAvailablePoolSize((int) schedulingInfos.stream().filter(x -> x.getOrganisation().getId().longValue() == o.getId()).count());

            poolInfo.setSchedulingTemplate(getSchedulingTemplate(o));
            
            return poolInfo;
        }).collect(Collectors.toList());
    }

    private SchedulingTemplateDto getSchedulingTemplate(Organisation o) {
        List<SchedulingTemplate> schedulingTemplates = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(o, true);

        if(schedulingTemplates != null && schedulingTemplates.size() > 0) {
            return mapSchedulingTemplate(schedulingTemplates.get(0));
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
