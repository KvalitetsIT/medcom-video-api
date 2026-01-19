package dk.medcom.video.api.service.mapper.v2;

import dk.medcom.video.api.dao.entity.*;
import dk.medcom.video.api.service.model.*;

public class EnumMapper {
    public static VmrType modelToEntity(VmrTypeModel input) {
        return VmrType.valueOf(input.toString());
    }

    public static ViewType modelToEntity(ViewTypeModel input) {
        return ViewType.valueOf(input.toString());
    }

    public static VmrQuality modelToEntity(VmrQualityModel input) {
        return input != null ? VmrQuality.valueOf(input.toString()) : null;
    }

    public static DirectMedia modelToEntity(DirectMediaModel input) {
        return DirectMedia.valueOf(input.toString());
    }

    public static GuestMicrophone modelToEntity(GuestMicrophoneModel input) {
        return input != null ? GuestMicrophone.valueOf(input.toString()) : null;
    }

    public static MeetingType modelToEntity(MeetingTypeModel input) {
        return MeetingType.valueOf(input.toString());
    }

    public static ProvisionStatus modelToEntity(ProvisionStatusModel input) {
        return ProvisionStatus.valueOf(input.toString());
    }
}
