package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.SchedulingInfo;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public record SchedulingInfoModel(UUID uuid,
                                  Long hostPin,
                                  Long guestPin,
                                  int vmrAvailableBefore,
                                  int maxParticipants,
                                  boolean endMeetingOnEndTime,
                                  String uriWithDomain,
                                  String uriWithoutDomain,
                                  ProvisionStatusModel provisionStatus,
                                  String provisionStatusDescription,
                                  String portalLink,
                                  String ivrTheme,
                                  OffsetDateTime provisionTimestamp,
                                  String provisionVmrId,
                                  VmrTypeModel vmrType,
                                  ViewTypeModel hostView,
                                  ViewTypeModel guestView,
                                  VmrQualityModel vmrQuality,
                                  boolean enableOverlayText,
                                  boolean guestsCanPresent,
                                  boolean forcePresenterIntoMain,
                                  boolean forceEncryption,
                                  boolean muteAllGuests,
                                  MeetingUserModel createdBy,
                                  MeetingUserModel updatedBy,
                                  OffsetDateTime createdTime,
                                  OffsetDateTime updatedTime,
                                  UUID reservationId,
                                  String customPortalGuest,
                                  String customPortalHost,
                                  String returnUrl,
                                  MeetingModel meetingDetails,
                                  DirectMediaModel directMedia,
                                  String shortLink,
                                  String shortlink) {
    public static SchedulingInfoModel from(SchedulingInfo schedulingInfo, String shortLinkBaseUrl) {
        return new SchedulingInfoModel(UUID.fromString(schedulingInfo.getUuid()),
                schedulingInfo.getHostPin(),
                schedulingInfo.getGuestPin(),
                schedulingInfo.getVMRAvailableBefore(),
                schedulingInfo.getMaxParticipants(),
                schedulingInfo.getEndMeetingOnEndTime(),
                schedulingInfo.getUriWithDomain(),
                schedulingInfo.getUriWithoutDomain(),
                ProvisionStatusModel.from(schedulingInfo.getProvisionStatus()),
                schedulingInfo.getProvisionStatusDescription(),
                schedulingInfo.getPortalLink(),
                schedulingInfo.getIvrTheme(),
                mapDate(schedulingInfo.getProvisionTimestamp()),
                schedulingInfo.getProvisionVMRId(),
                VmrTypeModel.from(schedulingInfo.getVmrType()),
                ViewTypeModel.from(schedulingInfo.getHostView()),
                ViewTypeModel.from(schedulingInfo.getGuestView()),
                VmrQualityModel.from(schedulingInfo.getVmrQuality()),
                schedulingInfo.getEnableOverlayText(),
                schedulingInfo.getGuestsCanPresent(),
                schedulingInfo.getForcePresenterIntoMain(),
                schedulingInfo.getForceEncryption(),
                schedulingInfo.getMuteAllGuests(),
                MeetingUserModel.from(schedulingInfo.getMeetingUser()),
                MeetingUserModel.from(schedulingInfo.getUpdatedByUser()),
                mapDate(schedulingInfo.getCreatedTime()),
                mapDate(schedulingInfo.getUpdatedTime()),
                schedulingInfo.getReservationId() != null ? UUID.fromString(schedulingInfo.getReservationId()) : null,
                schedulingInfo.getCustomPortalGuest(),
                schedulingInfo.getCustomPortalHost(),
                schedulingInfo.getReturnUrl(),
                MeetingModel.from(schedulingInfo.getMeeting(), shortLinkBaseUrl),
                DirectMediaModel.from(schedulingInfo.getDirectMedia()),
                schedulingInfo.getMeeting() != null ? shortLinkBaseUrl + schedulingInfo.getMeeting().getShortId() : null,
                schedulingInfo.getMeeting() != null ? shortLinkBaseUrl + schedulingInfo.getMeeting().getShortId() : null);
    }

    private static OffsetDateTime mapDate(Date input) {
        return input != null ? OffsetDateTime.ofInstant(input.toInstant(), ZoneId.systemDefault()) : null;
    }
}
