package dk.medcom.video.api.dto;

import javax.validation.constraints.Max;

import dk.medcom.video.api.dao.SchedulingInfo;

public class UpdateSchedulingInfoDto {
	 
	@Max(6)
	private int provisionStatus;
	private String provisionVmrId;

	public UpdateSchedulingInfoDto() {	
	}
	
	public int getProvisionStatus() {
		return provisionStatus;
	}

	public void setProvisionStatus(int provisionStatus) {
		this.provisionStatus = provisionStatus;
	}

	public String getProvisionVmrId() {
		return provisionVmrId;
	}

	public void setProvisionVmrId(String provisionVmrId) {
		this.provisionVmrId = provisionVmrId;
	}
	
}
