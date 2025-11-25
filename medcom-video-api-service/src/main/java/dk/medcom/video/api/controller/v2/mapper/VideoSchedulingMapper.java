package dk.medcom.video.api.controller.v2.mapper;

import dk.medcom.video.api.controller.v2.VideoSchedulingInformationControllerV2;
import dk.medcom.video.api.service.model.*;
import org.openapitools.model.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class VideoSchedulingMapper {

    public static List<SchedulingInfo> internalToExternal(List<SchedulingInfoModel> input) {
        return input.stream().map(VideoSchedulingMapper::internalToExternal).toList();
    }

    public static SchedulingInfo internalToExternal(SchedulingInfoModel input) {
        var selfLink = new SchedulingInfoLinksSelf().href(linkTo(methodOn(VideoSchedulingInformationControllerV2.class).v2SchedulingInfoUuidGet(input.uuid())).withRel("self").toUri());

        return new SchedulingInfo()
                .reservationId(input.reservationId())
                .uuid(input.uuid())
                .hostPin(input.hostPin() != null ? input.hostPin().intValue() : null)
                .guestPin(input.guestPin() != null ? input.guestPin().intValue() : null)
                .vmrAvailableBefore(input.vmrAvailableBefore())
                .maxParticipants(input.maxParticipants())
                .endMeetingOnEndTime(input.endMeetingOnEndTime())
                .uriWithDomain(input.uriWithDomain())
                .uriWithoutDomain(input.uriWithoutDomain())
                .provisionStatus(EnumMapper.internalToExternal(input.provisionStatus()))
                .provisionStatusDescription(input.provisionStatusDescription())
                .portalLink(input.portalLink())
                .ivrTheme(input.ivrTheme())
                .vmrType(EnumMapper.internalToExternal(input.vmrType()))
                .hostView(EnumMapper.internalToExternal(input.hostView()))
                .guestView(EnumMapper.internalToExternal(input.guestView()))
                .vmrQuality(EnumMapper.internalToExternal(input.vmrQuality()))
                .enableOverlayText(input.enableOverlayText())
                .guestsCanPresent(input.guestsCanPresent())
                .forcePresenterIntoMain(input.forcePresenterIntoMain())
                .forceEncryption(input.forceEncryption())
                .muteAllGuests(input.muteAllGuests())
                .directMedia(EnumMapper.internalToExternal(input.directMedia()))
                .provisionTimestamp(input.provisionTimestamp())
                .provisionVmrId(input.provisionVmrId())
                .createdBy(internalToExternal(input.createdBy()))
                .updatedBy(internalToExternal(input.updatedBy()))
                .createdTime(input.createdTime())
                .updatedTime(input.updatedTime())
                .shortLink(input.shortLink())
                .meetingDetails(VideoMeetingMapper.internalToExternal(input.meetingDetails()))
                .shortlink(input.shortlink())
                .customPortalGuest(input.customPortalGuest())
                .customPortalHost(input.customPortalHost())
                .returnUrl(input.returnUrl())
                .links(new SchedulingInfoLinks().self(selfLink));
    }

    public static CreateSchedulingInfoModel externalToInternal(CreateSchedulingInfo input) {
        return new CreateSchedulingInfoModel(input.getOrganizationId(), input.getSchedulingTemplateId());
    }

    public static UpdateSchedulingInfoModel externalToInternal(UpdateSchedulingInfo input) {
        return new UpdateSchedulingInfoModel(
                EnumMapper.externalToInternal(input.getProvisionStatus()),
                input.getProvisionStatusDescription(),
                input.getProvisionVmrId());
    }

    private static MeetingUser internalToExternal(MeetingUserModel input) {
        if (input == null) {
            return null;
        }
        return new MeetingUser()
                .organisationId(input.organisationId())
                .email(input.email());
    }
}
