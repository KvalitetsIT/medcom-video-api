package dk.medcom.video.api.service.model;

import java.time.OffsetDateTime;
import java.util.List;

public record UpdateMeetingModel(String subject,
                                 OffsetDateTime startTime,
                                 OffsetDateTime endTime,
                                 String description,
                                 String projectCode,
                                 String organizedByEmail,
                                 List<String> labels,
                                 List<AdditionalInformationModel> additionalInformation) {
}
