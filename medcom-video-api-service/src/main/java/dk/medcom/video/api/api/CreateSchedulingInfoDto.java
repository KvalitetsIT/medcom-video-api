package dk.medcom.video.api.api;

public class CreateSchedulingInfoDto {
	private String organizationId;
	private Long schedulingTemplateId;

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public Long getSchedulingTemplateId() {
		return schedulingTemplateId;
	}

	public void setSchedulingTemplateId(Long schedulingTemplateId) {
		this.schedulingTemplateId = schedulingTemplateId;
	}
}
