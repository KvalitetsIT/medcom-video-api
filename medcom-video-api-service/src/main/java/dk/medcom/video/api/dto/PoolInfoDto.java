package dk.medcom.video.api.dto;

import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class PoolInfoDto extends ResourceSupport {
    private String organizationId;
    private int desiredPoolSize;
    private int availablePoolSize;
    private List<SchedulingTemplateDto> schedulingTemplates;

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

    public List<SchedulingTemplateDto> getSchedulingTemplates() {
        return schedulingTemplates;
    }

    public void setSchedulingTemplates(List<SchedulingTemplateDto> schedulingTemplates) {
        this.schedulingTemplates = schedulingTemplates;
    }

    public int getAvailablePoolSize() {
        return availablePoolSize;
    }

    public void setAvailablePoolSize(int availablePoolSize) {
        this.availablePoolSize = availablePoolSize;
    }
}
