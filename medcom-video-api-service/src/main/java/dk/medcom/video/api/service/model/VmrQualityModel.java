package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.VmrQuality;

public enum VmrQualityModel {
    sd,
    hd,
    fullhd;

    public static VmrQualityModel from(VmrQuality vmrQuality) {
        if (vmrQuality == null) {
            return null;
        }
        return VmrQualityModel.valueOf(vmrQuality.toString());
    }
}
