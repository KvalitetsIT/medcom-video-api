package dk.medcom.video.api.service.model;

public record UpdateSchedulingInfoModel(ProvisionStatusModel provisionStatus,
                                        String provisionStatusDescription,
                                        String provisionVmrId) {
}
