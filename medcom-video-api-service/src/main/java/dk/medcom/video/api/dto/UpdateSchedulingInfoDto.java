package dk.medcom.video.api.dto;

public class UpdateSchedulingInfoDto {
	 
	private ProvisionStatus provisionStatus;
	private String provisionVmrId;

	public UpdateSchedulingInfoDto() {	
	}
	
	public ProvisionStatus getProvisionStatus() {
		return provisionStatus;
	}

	public void setProvisionStatus(ProvisionStatus provisionStatus) {
		this.provisionStatus = provisionStatus;
	}

	public String getProvisionVmrId() {
		return provisionVmrId;
	}

	public void setProvisionVmrId(String provisionVmrId) {
		this.provisionVmrId = provisionVmrId;
	}
	
}
