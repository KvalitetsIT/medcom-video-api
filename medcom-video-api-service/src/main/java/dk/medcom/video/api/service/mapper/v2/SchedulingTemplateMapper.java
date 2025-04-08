package dk.medcom.video.api.service.mapper.v2;

import dk.medcom.video.api.api.CreateSchedulingTemplateDto;
import dk.medcom.video.api.api.MeetingUserDto;
import dk.medcom.video.api.api.SchedulingTemplateDto;
import dk.medcom.video.api.api.UpdateSchedulingTemplateDto;
import dk.medcom.video.api.service.model.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public class SchedulingTemplateMapper {
    public static SchedulingTemplateModel dtoToModel(SchedulingTemplateDto input) {
        if (input == null) {
            return null;
        }
        return new SchedulingTemplateModel(
                input.getTemplateId(),
                input.getOrganisationId(),
                input.getConferencingSysId(),
                input.getUriPrefix(),
                input.getUriDomain(),
                input.isHostPinRequired(),
                input.getHostPinRangeLow(),
                input.getHostPinRangeHigh(),
                input.isGuestPinRequired(),
                input.getGuestPinRangeLow(),
                input.getGuestPinRangeHigh(),
                input.getvMRAvailableBefore(),
                input.getMaxParticipants(),
                input.isEndMeetingOnEndTime(),
                input.getUriNumberRangeLow(),
                input.getUriNumberRangeHigh(),
                input.getIvrTheme(),
                input.getIsDefaultTemplate(),
                input.getIsPoolTemplate(), input.getCustomPortalGuest(), input.getCustomPortalHost(), input.getReturnUrl(),
                VmrTypeModel.from(input.getVmrType()),
                ViewTypeModel.from(input.getHostView()),
                ViewTypeModel.from(input.getGuestView()),
                VmrQualityModel.from(input.getVmrQuality()),
                input.isEnableOverlayText(),
                input.isGuestsCanPresent(),
                input.isForcePresenterIntoMain(),
                input.isForceEncryption(),
                input.isMuteAllGuests(),
                DirectMediaModel.from(input.getDirectMedia()),
                dtoToModel(input.getCreatedBy()),
                dtoToModel(input.updatedBy),
                mapDate(input.getCreatedTime()),
                mapDate(input.getUpdatedTime()));
    }

    public static CreateSchedulingTemplateDto modelToDto(SchedulingTemplateRequestModel input) {
        var output = new CreateSchedulingTemplateDto();
        output.setConferencingSysId(input.conferencingSysId());
        output.setUriPrefix(input.uriPrefix());
        output.setUriDomain(input.uriDomain());
        output.setHostPinRequired(input.hostPinRequired());
        output.setHostPinRangeLow(input.hostPinRangeLow());
        output.setHostPinRangeHigh(input.hostPinRangeHigh());
        output.setGuestPinRequired(input.guestPinRequired());
        output.setGuestPinRangeLow(input.guestPinRangeLow());
        output.setGuestPinRangeHigh(input.guestPinRangeHigh());
        output.setvMRAvailableBefore(input.vMRAvailableBefore());
        output.setMaxParticipants(input.maxParticipants());
        output.setEndMeetingOnEndTime(input.endMeetingOnEndTime());
        output.setUriNumberRangeLow(input.uriNumberRangeLow());
        output.setUriNumberRangeHigh(input.uriNumberRangeHigh());
        output.setVmrType(EnumMapper.modelToEntity(input.vmrType()));
        output.setHostView(EnumMapper.modelToEntity(input.hostView()));
        output.setGuestView(EnumMapper.modelToEntity(input.guestView()));
        output.setVmrQuality(EnumMapper.modelToEntity(input.vmrQuality()));
        output.setEnableOverlayText(input.enableOverlayText());
        output.setGuestsCanPresent(input.guestsCanPresent());
        output.setForcePresenterIntoMain(input.forcePresenterIntoMain());
        output.setForceEncryption(input.forceEncryption());
        output.setMuteAllGuests(input.muteAllGuests());
        output.setCustomPortalGuest(input.customPortalGuest());
        output.setCustomPortalHost(input.customPortalHost());
        output.setReturnUrl(input.returnUrl());
        output.setDirectMedia(EnumMapper.modelToEntity(input.directMedia()));
        output.setIvrTheme(input.ivrTheme());
        output.setIsDefaultTemplate(input.isDefaultTemplate());
        output.setIsPoolTemplate(input.isPoolTemplate());

        return output;
    }

    public static UpdateSchedulingTemplateDto modelToDtoUpdate(SchedulingTemplateRequestModel input) {
        var output = new UpdateSchedulingTemplateDto();
        output.setConferencingSysId(input.conferencingSysId());
        output.setUriPrefix(input.uriPrefix());
        output.setUriDomain(input.uriDomain());
        output.setHostPinRequired(input.hostPinRequired());
        output.setHostPinRangeLow(input.hostPinRangeLow());
        output.setHostPinRangeHigh(input.hostPinRangeHigh());
        output.setGuestPinRequired(input.guestPinRequired());
        output.setGuestPinRangeLow(input.guestPinRangeLow());
        output.setGuestPinRangeHigh(input.guestPinRangeHigh());
        output.setvMRAvailableBefore(input.vMRAvailableBefore());
        output.setMaxParticipants(input.maxParticipants());
        output.setEndMeetingOnEndTime(input.endMeetingOnEndTime());
        output.setUriNumberRangeLow(input.uriNumberRangeLow());
        output.setUriNumberRangeHigh(input.uriNumberRangeHigh());
        output.setVmrType(EnumMapper.modelToEntity(input.vmrType()));
        output.setHostView(EnumMapper.modelToEntity(input.hostView()));
        output.setGuestView(EnumMapper.modelToEntity(input.guestView()));
        output.setVmrQuality(EnumMapper.modelToEntity(input.vmrQuality()));
        output.setEnableOverlayText(input.enableOverlayText());
        output.setGuestsCanPresent(input.guestsCanPresent());
        output.setForcePresenterIntoMain(input.forcePresenterIntoMain());
        output.setForceEncryption(input.forceEncryption());
        output.setMuteAllGuests(input.muteAllGuests());
        output.setCustomPortalGuest(input.customPortalGuest());
        output.setCustomPortalHost(input.customPortalHost());
        output.setReturnUrl(input.returnUrl());
        output.setDirectMedia(EnumMapper.modelToEntity(input.directMedia()));
        output.setIvrTheme(input.ivrTheme());
        output.setIsDefaultTemplate(input.isDefaultTemplate());
        output.setIsPoolTemplate(input.isPoolTemplate());

        return output;
    }

    private static MeetingUserModel dtoToModel(MeetingUserDto input) {
        return new MeetingUserModel(input.organisationId, input.email);
    }

    private static OffsetDateTime mapDate(Date input) {
        return input != null ? OffsetDateTime.ofInstant(input.toInstant(), ZoneId.systemDefault()) : null;
    }
}
