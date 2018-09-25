//TODO Lene: Er det 3 ProvisionStatus klasser korrekt navngivet og placeret i pakke?
package dk.medcom.video.api.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProvisionStatus {
	AWAITS_PROVISION(0),
	STARTING_TO_PROVISION(1),
	PROVISION_PROBLEMS(2),
	PROVISIONED_OK(3),
	STARTING_TO_DEPROVISION(4),
	DEPROVISION_PROBLEMS(5),
	DEPROVISION_OK(6);
	
	private final int value;
	
	private ProvisionStatus(int value) {
		this.value = value;
	}
	@JsonValue //Handles correct output body
	public int getValue() {
		return value;
	}
	
    public static ProvisionStatus getProvisionStatus(Integer id) {
        if (id == null) {
            return null;
        }
 
        for (ProvisionStatus provisionStatus : ProvisionStatus.values()) {
            if (id.equals(provisionStatus.getValue())) {
                return provisionStatus;
            }
        }
 
        return null;
    }
	
}