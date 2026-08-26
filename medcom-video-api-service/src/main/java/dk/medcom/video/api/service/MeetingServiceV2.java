package dk.medcom.video.api.service;

import dk.medcom.video.api.service.model.*;
import org.openapitools.model.MeetingParticipationList;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MeetingServiceV2 {
    List<MeetingModel> getMeetingsV2(OffsetDateTime fromStartTime, OffsetDateTime toStartTime);

    MeetingModel getMeetingByShortIdV2(String shortId);

    List<MeetingModel> getMeetingsBySubjectV2(String subject);

    List<MeetingModel> getMeetingsByOrganizedByV2(String organizedBy);

    List<MeetingModel> getMeetingsByUriWithDomainV2(String uriWithDomain);

    List<MeetingModel> searchMeetingsV2(String search, OffsetDateTime fromStartTime, OffsetDateTime toStartTime);

    MeetingModel getMeetingsByUriWithDomainSingleV2(String uriWithDomain);

    MeetingModel getMeetingsByUriWithoutDomainV2(String uriWithoutDomain);

    List<MeetingModel> getMeetingsByLabelV2(String label);

    MeetingModel getMeetingByUuidV2(UUID uuid);

    MeetingModel createMeetingV2(CreateMeetingModel createMeeting);

    MeetingModel updateMeetingV2(UUID uuid, UpdateMeetingModel updateMeeting);

    void deleteMeetingV2(UUID uuid);

    MeetingModel patchMeetingV2(UUID uuid, PatchMeetingModel patchMeeting);

    List<MeetingParticipationModel> getMeetingParticipations(String participantId,
                                                             OffsetDateTime fromStartTime,
                                                             OffsetDateTime toStartTime,
                                                             String subject,
                                                             String organizedBy,
                                                             String label);
}
