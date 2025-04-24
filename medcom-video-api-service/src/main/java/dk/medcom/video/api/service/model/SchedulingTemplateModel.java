package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.SchedulingTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public record SchedulingTemplateModel(Long id,
                                      String organisationId,
                                      Long conferencingSysId,
                                      String uriPrefix,
                                      String uriDomain,
                                      boolean hostPinRequired,
                                      Long hostPinRangeLow,
                                      Long hostPinRangeHigh,
                                      boolean guestPinRequired,
                                      Long guestPinRangeLow,
                                      Long guestPinRangeHigh,
                                      int vMRAvailableBefore,
                                      int maxParticipants,
                                      boolean endMeetingOnEndTime,
                                      Long uriNumberRangeLow,
                                      Long uriNumberRangeHigh,
                                      String ivrTheme,
                                      boolean isDefaultTemplate,
                                      boolean isPoolTemplate,
                                      String customPortalGuest,
                                      String customPortalHost,
                                      String returnUrl,
                                      VmrTypeModel vmrType,
                                      ViewTypeModel hostView,
                                      ViewTypeModel guestView,
                                      VmrQualityModel vmrQuality,
                                      boolean enableOverlayText,
                                      boolean guestsCanPresent,
                                      boolean forcePresenterIntoMain,
                                      boolean forceEncryption,
                                      boolean muteAllGuests,
                                      DirectMediaModel directMedia,
                                      MeetingUserModel createdBy,
                                      MeetingUserModel updatedBy,
                                      OffsetDateTime createdTime,
                                      OffsetDateTime updatedTime) {
    public static SchedulingTemplateModel from(SchedulingTemplate schedulingTemplate) {
        return new SchedulingTemplateModel(schedulingTemplate.getId(),
                schedulingTemplate.getOrganisation().getOrganisationId(),
                schedulingTemplate.getConferencingSysId(),
                schedulingTemplate.getUriPrefix(),
                schedulingTemplate.getUriDomain(),
                schedulingTemplate.getHostPinRequired(),
                schedulingTemplate.getHostPinRangeLow(),
                schedulingTemplate.getHostPinRangeHigh(),
                schedulingTemplate.getGuestPinRequired(),
                schedulingTemplate.getGuestPinRangeLow(),
                schedulingTemplate.getGuestPinRangeHigh(),
                schedulingTemplate.getVMRAvailableBefore(),
                schedulingTemplate.getMaxParticipants(),
                schedulingTemplate.getEndMeetingOnEndTime(),
                schedulingTemplate.getUriNumberRangeLow(),
                schedulingTemplate.getUriNumberRangeHigh(),
                schedulingTemplate.getIvrTheme(),
                schedulingTemplate.getIsDefaultTemplate(),
                schedulingTemplate.getIsPoolTemplate(),
                schedulingTemplate.getCustomPortalGuest(),
                schedulingTemplate.getCustomPortalHost(),
                schedulingTemplate.getReturnUrl(),
                VmrTypeModel.from(schedulingTemplate.getVmrType()),
                ViewTypeModel.from(schedulingTemplate.getHostView()),
                ViewTypeModel.from(schedulingTemplate.getGuestView()),
                VmrQualityModel.from(schedulingTemplate.getVmrQuality()),
                schedulingTemplate.getEnableOverlayText(),
                schedulingTemplate.getGuestsCanPresent(),
                schedulingTemplate.getForcePresenterIntoMain(),
                schedulingTemplate.getForceEncryption(),
                schedulingTemplate.getMuteAllGuests(),
                DirectMediaModel.from(schedulingTemplate.getDirectMedia()),
                MeetingUserModel.from(schedulingTemplate.getCreatedBy()),
                MeetingUserModel.from(schedulingTemplate.getUpdatedBy()),
                mapDate(schedulingTemplate.getCreatedTime()),
                mapDate(schedulingTemplate.getUpdatedTime()));
    }

    private static OffsetDateTime mapDate(Date input) {
        return input != null ? OffsetDateTime.ofInstant(input.toInstant(), ZoneId.systemDefault()) : null;
    }
}
