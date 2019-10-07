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

            poolInfo.setSchedulingTemplates(getSchedulingTemplates(o));
            
            return poolInfo;
        }).collect(Collectors.toList());
    }

    private List<SchedulingTemplateDto> getSchedulingTemplates(Organisation o) {
        List<SchedulingTemplate> schedulingTemplates = schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(o, true);

        return mapSchedulingTemplate(schedulingTemplates);
    }

    private List<SchedulingTemplateDto> mapSchedulingTemplate(List<SchedulingTemplate> schedulingTemplates) {
        return schedulingTemplates.stream().map( s -> {
            try {
                return new SchedulingTemplateDto(s);
            } catch (PermissionDeniedException e) {
                throw new RuntimeException("Error mapping SchedulingTemplate.", e);
            }
        }).collect(Collectors.toList());
    }
}
