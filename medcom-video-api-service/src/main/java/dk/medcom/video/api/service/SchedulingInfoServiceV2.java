package dk.medcom.video.api.service;

import dk.medcom.video.api.service.model.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface SchedulingInfoServiceV2 {
    List<SchedulingInfoModel> getSchedulingInfoV2(OffsetDateTime fromStartTime, OffsetDateTime toEndTime, ProvisionStatusModel provisionStatus);

    List<SchedulingInfoModel> getSchedulingInfoAwaitsProvisionV2();

    List<SchedulingInfoModel> getSchedulingInfoAwaitsDeProvisionV2();

    SchedulingInfoModel getSchedulingInfoByUuidV2(UUID uuid);

    SchedulingInfoModel createSchedulingInfoV2(CreateSchedulingInfoModel createSchedulingInfo);

    SchedulingInfoModel updateSchedulingInfoV2(UUID uuid, UpdateSchedulingInfoModel updateSchedulingInfo);

    SchedulingInfoModel reserveSchedulingInfoV2(VmrTypeModel vmrType,
                                                ViewTypeModel hostView,
                                                ViewTypeModel guestView,
                                                VmrQualityModel vmrQuality,
                                                Boolean enableOverlayText,
                                                Boolean guestsCanPresent,
                                                Boolean forcePresenterIntoMain,
                                                Boolean forceEncryption,
                                                Boolean muteAllGuests);

    SchedulingInfoModel getSchedulingInfoByReservationV2(UUID schedulingInfoReservationId);
}
