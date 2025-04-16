package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.ProvisionStatus;

public enum ProvisionStatusModel {
    AWAITS_PROVISION,
    STARTING_TO_PROVISION,
    PROVISION_PROBLEMS,
    PROVISIONED_OK,
    STARTING_TO_DEPROVISION,
    DEPROVISION_PROBLEMS,
    DEPROVISION_OK;

    public static ProvisionStatusModel from(ProvisionStatus provisionStatus) {
        return ProvisionStatusModel.valueOf(provisionStatus.toString());
    }
}
