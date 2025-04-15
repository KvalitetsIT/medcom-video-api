package dk.medcom.video.api.api;

import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

public class PoolInfoDto extends RepresentationModel {
    private String organizationId;
    private int desiredPoolSize;
    private int availablePoolSize;
    private SchedulingTemplateDto schedulingTemplate;
    private List<SchedulingInfo> schedulingInfoList;

    public int getDesiredPoolSize() {
        return desiredPoolSize;
    }

    public void setDesiredPoolSize(int desiredPoolSize) {
        this.desiredPoolSize = desiredPoolSize;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public SchedulingTemplateDto getSchedulingTemplate() {
        return schedulingTemplate;
    }

    public void setSchedulingTemplate(SchedulingTemplateDto schedulingTemplate) {
        this.schedulingTemplate = schedulingTemplate;
    }

    public int getAvailablePoolSize() {
        return availablePoolSize;
    }

    public void setAvailablePoolSize(int availablePoolSize) {
        this.availablePoolSize = availablePoolSize;
    }

    public List<SchedulingInfo> getSchedulingInfoList() {
        return schedulingInfoList;
    }

    public void setSchedulingInfoList(List<SchedulingInfo> schedulingInfoList) {
        this.schedulingInfoList = schedulingInfoList;
    }
}
