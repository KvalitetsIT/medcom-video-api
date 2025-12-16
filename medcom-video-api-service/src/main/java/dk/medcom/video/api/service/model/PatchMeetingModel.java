package dk.medcom.video.api.service.model;

import java.time.OffsetDateTime;
import java.util.List;

public record PatchMeetingModel(String subject,
                                OffsetDateTime startTime,
                                OffsetDateTime endTime,
                                String description,
                                String projectCode,
                                String organizedByEmail,
                                List<String> labels,
                                GuestMicrophoneModel guestMicrophone,
                                Boolean guestPinRequired,
                                Integer hostPin,
                                Integer guestPin,
                                List<AdditionalInformationModel> additionalInformation) {
}
