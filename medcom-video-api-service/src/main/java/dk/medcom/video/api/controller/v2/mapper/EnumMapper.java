package dk.medcom.video.api.controller.v2.mapper;

import dk.medcom.video.api.service.model.*;
import org.openapitools.model.*;

public class EnumMapper {
    public static VmrType internalToExternal(VmrTypeModel input) {
        return input != null ? VmrType.fromValue(input.toString()) : null;
    }

    public static VmrTypeModel externalToInternal(VmrType input) {
        return VmrTypeModel.valueOf(input.toString());
    }

    public static ViewType internalToExternal(ViewTypeModel input) {
        return input != null ? ViewType.fromValue(input.toString()) : null;
    }

    public static ViewTypeModel externalToInternal(ViewType input) {
        return ViewTypeModel.valueOf(input.toString());
    }

    public static VmrQuality internalToExternal(VmrQualityModel input) {
        return input != null ? VmrQuality.fromValue(input.toString()) : null;
    }

    public static VmrQualityModel externalToInternal(VmrQuality input) {
        return input != null ? VmrQualityModel.valueOf(input.toString()) : null;
    }

    public static DirectMedia internalToExternal(DirectMediaModel input) {
        return DirectMedia.fromValue(input.toString());
    }

    public static DirectMediaModel externalToInternal(DirectMedia input) {
        return DirectMediaModel.valueOf(input.toString());
    }

    public static ProvisionStatus internalToExternal(ProvisionStatusModel input) {
        return ProvisionStatus.valueOf(input.toString());
    }

    public static ProvisionStatusModel externalToInternal(ProvisionStatus input) {
        return ProvisionStatusModel.valueOf(input.toString());
    }

    public static GuestMicrophone internalToExternal(GuestMicrophoneModel input) {
        return input != null ? GuestMicrophone.fromValue(input.toString()) : null;
    }

    public static GuestMicrophoneModel externalToInternal(GuestMicrophone input) {
        return input != null ? GuestMicrophoneModel.valueOf(input.toString()) : null;
    }

    public static MeetingTypeModel externalToInternal(MeetingType input) {
        return MeetingTypeModel.valueOf(input.toString());
    }
}
