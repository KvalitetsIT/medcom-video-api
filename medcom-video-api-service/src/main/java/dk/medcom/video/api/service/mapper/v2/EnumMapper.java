package dk.medcom.video.api.service.mapper.v2;

import dk.medcom.video.api.dao.entity.*;
import dk.medcom.video.api.service.model.*;

public class EnumMapper {
    public static VmrType modelToEntity(VmrTypeModel input) {
        return input != null ? VmrType.valueOf(input.toString()) : null;
    }

    public static ViewType modelToEntity(ViewTypeModel input) {
        return input != null ? ViewType.valueOf(input.toString()) : null;
    }

    public static VmrQuality modelToEntity(VmrQualityModel input) {
        return input != null ? VmrQuality.valueOf(input.toString()) : null;
    }

    public static DirectMedia modelToEntity(DirectMediaModel input) {
        return input != null ? DirectMedia.valueOf(input.toString()) : null;
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
