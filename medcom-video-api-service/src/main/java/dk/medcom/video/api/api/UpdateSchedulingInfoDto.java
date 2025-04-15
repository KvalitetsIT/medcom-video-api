package dk.medcom.video.api.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateSchedulingInfoDto {
	 
	@NotNull
	private ProvisionStatus provisionStatus;
	@Size(max=200, message="provision status description should have a maximum of 200 characters")
	private String provisionStatusDescription;
	private String provisionVmrId;

	public UpdateSchedulingInfoDto() {	
	}
	
	public ProvisionStatus getProvisionStatus() {
		return provisionStatus;
	}

	public void setProvisionStatus(ProvisionStatus provisionStatus) {
		this.provisionStatus = provisionStatus;
	}
	
	public String getProvisionStatusDescription() {
		return provisionStatusDescription;
	}

	public void setProvisionStatusDescription(String provisionStatusDescription) {
		this.provisionStatusDescription = provisionStatusDescription;
	}

	public String getProvisionVmrId() {
		return provisionVmrId;
	}

	public void setProvisionVmrId(String provisionVmrId) {
		this.provisionVmrId = provisionVmrId;
	}
	
}
