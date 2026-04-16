package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.VmrType;

public enum VmrTypeModel {
    conference,
    lecture;

    public static VmrTypeModel from(VmrType vmrType) {
        if (vmrType == null) {
            return null;
        }
        return VmrTypeModel.valueOf(vmrType.toString());
    }
}

