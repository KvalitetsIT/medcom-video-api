package dk.medcom.video.api.service.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CreateMeetingModel(String subject,
                                 OffsetDateTime startTime,
                                 OffsetDateTime endTime,
                                 String description,
                                 String projectCode,
                                 UUID schedulingInfoReservationId,
                                 String organizedByEmail,
                                 Integer maxParticipants,
                                 Boolean endMeetingOnEndTime,
                                 Long schedulingTemplateId,
                                 MeetingTypeModel meetingType,
                                 UUID uuid,
                                 List<String> labels,
                                 String externalId,
                                 GuestMicrophoneModel guestMicrophone,
                                 Boolean guestPinRequired,
                                 VmrTypeModel vmrType,
                                 ViewTypeModel hostView,
                                 ViewTypeModel guestView,
                                 VmrQualityModel vmrQuality,
                                 boolean enableOverlayText,
                                 boolean guestsCanPresent,
                                 boolean forcePresenterIntoMain,
                                 boolean forceEncryption,
                                 boolean muteAllGuests,
                                 String uriWithoutDomain,
                                 Integer hostPin,
                                 Integer guestPin,
                                 List<AdditionalInformationModel> additionalInformation) {
}
