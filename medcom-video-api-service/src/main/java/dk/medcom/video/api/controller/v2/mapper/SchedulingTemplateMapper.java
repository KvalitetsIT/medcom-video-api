package dk.medcom.video.api.controller.v2.mapper;

import dk.medcom.video.api.controller.v2.SchedulingTemplateAdministrationControllerV2;
import dk.medcom.video.api.service.model.*;
import org.openapitools.model.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class SchedulingTemplateMapper {

    public static List<SchedulingTemplate> internalToExternal(List<SchedulingTemplateModel> input) {
        return input.stream().map(SchedulingTemplateMapper::internalToExternal).toList();
    }

    public static SchedulingTemplate internalToExternal(SchedulingTemplateModel input) {
        if (input == null) {
            return null;
        }
        var selfLink = new SchedulingTemplateLinksSelf().href(linkTo(methodOn(SchedulingTemplateAdministrationControllerV2.class).v2SchedulingTemplatesIdGet(input.id())).withRel("self").toUri());

        return new SchedulingTemplate()
                .id(input.id())
                .organisationId(input.organisationId())
                .conferencingSysId(input.conferencingSysId())
                .uriPrefix(input.uriPrefix())
                .uriDomain(input.uriDomain())
                .hostPinRequired(input.hostPinRequired())
                .hostPinRangeLow(input.hostPinRangeLow())
                .hostPinRangeHigh(input.hostPinRangeHigh())
                .guestPinRequired(input.guestPinRequired())
                .guestPinRangeLow(input.guestPinRangeLow())
                .guestPinRangeHigh(input.guestPinRangeHigh())
                .vMRAvailableBefore(input.vMRAvailableBefore())
                .maxParticipants(input.maxParticipants())
                .endMeetingOnEndTime(input.endMeetingOnEndTime())
                .uriNumberRangeLow(input.uriNumberRangeLow())
                .uriNumberRangeHigh(input.uriNumberRangeHigh())
                .ivrTheme(input.ivrTheme())
                .isDefaultTemplate(input.isDefaultTemplate())
                .isPoolTemplate(input.isPoolTemplate())
                .vmrType(EnumMapper.internalToExternal(input.vmrType()))
                .hostView(EnumMapper.internalToExternal(input.hostView()))
                .guestView(EnumMapper.internalToExternal(input.guestView()))
                .vmrQuality(EnumMapper.internalToExternal(input.vmrQuality()))
                .enableOverlayText(input.enableOverlayText())
                .guestsCanPresent(input.guestsCanPresent())
                .forcePresenterIntoMain(input.forcePresenterIntoMain())
                .forceEncryption(input.forceEncryption())
                .muteAllGuests(input.muteAllGuests())
                .customPortalGuest(input.customPortalGuest())
                .customPortalHost(input.customPortalHost())
                .returnUrl(input.returnUrl())
                .directMedia(EnumMapper.internalToExternal(input.directMedia()))
                .createdBy(internalToExternal(input.createdBy()))
                .updatedBy(internalToExternal(input.updatedBy()))
                .createdTime(input.createdTime())
                .updatedTime(input.updatedTime())
                .links(new SchedulingTemplateLinks().self(selfLink));
    }

    public static SchedulingTemplateRequestModel externalToInternal(SchedulingTemplateRequest input) {
        return new SchedulingTemplateRequestModel(
                input.getConferencingSysId(),
                input.getUriPrefix(),
                input.getUriDomain(),
                input.getHostPinRequired(),
                input.getHostPinRangeLow(),
                input.getHostPinRangeHigh(),
                input.getGuestPinRequired(),
                input.getGuestPinRangeLow(),
                input.getGuestPinRangeHigh(),
                input.getVmrAvailableBefore(),
                input.getMaxParticipants(),
                input.getEndMeetingOnEndTime(),
                input.getUriNumberRangeLow(),
                input.getUriNumberRangeHigh(),
                input.getIvrTheme(),
                input.getIsDefaultTemplate(),
                input.getIsPoolTemplate(),
                input.getCustomPortalGuest(),
                input.getCustomPortalHost(),
                input.getReturnUrl(),
                EnumMapper.externalToInternal(input.getVmrType()),
                EnumMapper.externalToInternal(input.getHostView()),
                EnumMapper.externalToInternal(input.getGuestView()),
                EnumMapper.externalToInternal(input.getVmrQuality()),
                input.getEnableOverlayText(),
                input.getGuestsCanPresent(),
                input.getForcePresenterIntoMain(),
                input.getForceEncryption(),
                input.getMuteAllGuests(),
                EnumMapper.externalToInternal(input.getDirectMedia()));
    }

    private static MeetingUser internalToExternal(MeetingUserModel input) {
        if (input == null) {
            return null;
        }
        return new MeetingUser()
                .email(input.email())
                .organisationId(input.organisationId());
    }
}
