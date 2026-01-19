package dk.medcom.video.api.controller.v2.mapper;

import dk.medcom.video.api.controller.v2.VideoMeetingsControllerV2;
import dk.medcom.video.api.service.model.*;
import org.openapitools.model.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class VideoMeetingMapper {

    public static List<Meeting> internalToExternal(List<MeetingModel> input) {
        return input.stream().map(VideoMeetingMapper::internalToExternal).toList();
    }

    public static Meeting internalToExternal(MeetingModel input) {
        if (input == null) {
            return null;
        }

        var selfLink = new MeetingLinksSelf().href(linkTo(methodOn(VideoMeetingsControllerV2.class).v2MeetingsUuidGet(input.uuid())).withRel("self").toUri());

        return new Meeting()
                .subject(input.subject())
                .uuid(input.uuid())
                .createdBy(internalToExternal(input.createdBy()))
                .updatedBy(internalToExternal(input.updatedBy()))
                .organizedBy(internalToExternal(input.organizedBy()))
                .startTime(input.startTime())
                .endTime(input.endTime())
                .description(input.description())
                .projectCode(input.projectCode())
                .createdTime(input.createdTime())
                .updatedTime(input.updatedTime())
                .labels(input.labels())
                .shortId(input.shortId())
                .externalId(input.externalId())
                .shortLink(input.shortLink())
                .shortlink(input.shortlink())
                .guestMicrophone(EnumMapper.internalToExternal(input.guestMicrophone()))
                .guestPinRequired(input.guestPinRequired())
                .additionalInformation(input.additionalInformation().stream().map(VideoMeetingMapper::internalToExternal).toList())
                .links(new MeetingLinks().self(selfLink));
    }

    public static CreateMeetingModel externalToInternal(CreateMeeting input) {
        return new CreateMeetingModel(input.getSubject(),
                input.getStartTime(),
                input.getEndTime(),
                input.getDescription(),
                input.getProjectCode(),
                input.getSchedulingInfoReservationId(),
                input.getOrganizedByEmail(),
                input.getMaxParticipants(),
                input.getEndMeetingOnEndTime(),
                input.getSchedulingTemplateId(),
                EnumMapper.externalToInternal(input.getMeetingType()),
                input.getUuid(),
                input.getLabels(),
                input.getExternalId(),
                EnumMapper.externalToInternal(input.getGuestMicrophone()),
                input.getGuestPinRequired(),
                EnumMapper.externalToInternal(input.getVmrType()),
                EnumMapper.externalToInternal(input.getHostView()),
                EnumMapper.externalToInternal(input.getGuestView()),
                EnumMapper.externalToInternal(input.getVmrQuality()),
                input.getEnableOverlayText(),
                input.getGuestsCanPresent(),
                input.getForcePresenterIntoMain(),
                input.getForceEncryption(),
                input.getMuteAllGuests(),
                input.getUriWithoutDomain(),
                input.getHostPin(),
                input.getGuestPin(),
                externalToInternal(input.getAdditionalInformation()));
    }

    public static PatchMeetingModel externalToInternal(PatchMeeting input) {
        return new PatchMeetingModel(
                input.getSubject(),
                input.getStartTime(),
                input.getEndTime(),
                input.getDescription(),
                input.getProjectCode(),
                input.getOrganizedByEmail(),
                input.getLabels(),
                EnumMapper.externalToInternal(input.getGuestMicrophone()),
                input.getGuestPinRequired(),
                input.getHostPin(),
                input.getGuestPin(),
                externalToInternal(input.getAdditionalInformation()));
    }

    public static UpdateMeetingModel externalToInternal(UpdateMeeting input) {
        return new UpdateMeetingModel(input.getSubject(),
                input.getStartTime(),
                input.getEndTime(),
                input.getDescription(),
                input.getProjectCode(),
                input.getOrganizedByEmail(),
                input.getLabels(),
                externalToInternal(input.getAdditionalInformation()));
    }

    private static MeetingUser internalToExternal(MeetingUserModel input) {
        if (input == null) {
            return new MeetingUser();
        }
        return new MeetingUser()
                .organisationId(input.organisationId())
                .email(input.email());
    }

    private static List<AdditionalInformationModel> externalToInternal(List<AdditionalInformationType> input) {
        if (input == null) {
            return null;
        }
        return input.stream().map(x -> new AdditionalInformationModel(x.getKey(), x.getValue())).toList();
    }

    private static AdditionalInformationType internalToExternal(AdditionalInformationModel input) {
        if (input == null) {
            return null;
        }
        return new AdditionalInformationType(input.key(), input.value());
    }
}
