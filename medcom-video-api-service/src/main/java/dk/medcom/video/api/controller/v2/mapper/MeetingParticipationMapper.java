package dk.medcom.video.api.controller.v2.mapper;

import dk.medcom.video.api.service.model.MeetingParticipationModel;
import org.openapitools.model.MeetingParticipation;

import java.util.List;

public class MeetingParticipationMapper {

    public static List<MeetingParticipation> internalToExternal(List<MeetingParticipationModel> input) {
        return input.stream().map(MeetingParticipationMapper::internalToExternal).toList();
    }

    public static MeetingParticipation internalToExternal(MeetingParticipationModel input) {
        if (input == null) {
            return null;
        }

        return new MeetingParticipation()
                .uuid(input.uuid())
                .subject(input.subject())
                .description(input.description())
                .startTime(input.startTime())
                .endTime(input.endTime())
                .createdBy(VideoMeetingMapper.internalToExternal(input.createdBy()))
                .updatedBy(VideoMeetingMapper.internalToExternal(input.updatedBy()))
                .updatedTime(input.updatedTime())
                .organizedBy(VideoMeetingMapper.internalToExternal(input.organizedBy()))
                .knownParticipants(input.knownParticipants())
                .participantRole(EnumMapper.internalToExternal(input.participantRole()))
                .pin(input.pin())
                .uriWithDomain(input.uriWithDomain())
                .shortLink(input.shortLink())
                .portalLink(input.portalLink());
    }
}