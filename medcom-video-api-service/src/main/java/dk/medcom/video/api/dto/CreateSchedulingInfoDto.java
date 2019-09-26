package dk.medcom.video.api.dto;

public class CreateSchedulingInfoDto {
	private String organizationId;
	private Long templateId;
	private String provisionVmrId;

	public String getProvisionVmrId() {
		return provisionVmrId;
	}

	public void setProvisionVmrId(String provisionVmrId) {
		this.provisionVmrId = provisionVmrId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
}
