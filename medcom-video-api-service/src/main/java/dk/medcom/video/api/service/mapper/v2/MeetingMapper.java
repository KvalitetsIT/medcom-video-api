package dk.medcom.video.api.service.mapper.v2;

import dk.medcom.video.api.api.AdditionalInformationType;
import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.api.PatchMeetingDto;
import dk.medcom.video.api.api.UpdateMeetingDto;
import dk.medcom.video.api.service.model.AdditionalInformationModel;
import dk.medcom.video.api.service.model.CreateMeetingModel;
import dk.medcom.video.api.service.model.PatchMeetingModel;
import dk.medcom.video.api.service.model.UpdateMeetingModel;

import java.util.Date;
import java.util.List;

public class MeetingMapper {

    public static CreateMeetingDto modelToDto(CreateMeetingModel input) {
        var output = new CreateMeetingDto();
        output.setSubject(input.subject());
        output.setStartTime(Date.from(input.startTime().toInstant()));
        output.setEndTime(Date.from(input.endTime().toInstant()));
        output.setDescription(input.description());
        output.setProjectCode(input.projectCode());
        output.setSchedulingInfoReservationId(input.schedulingInfoReservationId());
        output.setOrganizedByEmail(input.organizedByEmail());
        if (input.maxParticipants() != null) {
            output.setMaxParticipants(input.maxParticipants());
        }
        output.setEndMeetingOnEndTime(input.endMeetingOnEndTime());
        output.setSchedulingTemplateId(input.schedulingTemplateId());
        output.setMeetingType(EnumMapper.modelToEntity(input.meetingType()));
        output.setUuid(input.uuid());
        output.setLabels(input.labels() != null ? input.labels() : List.of());
        output.setExternalId(input.externalId());
        output.setGuestMicrophone(EnumMapper.modelToEntity(input.guestMicrophone()));
        if (input.guestPinRequired() != null) {
            output.setGuestPinRequired(input.guestPinRequired());
        }
        output.setVmrType(EnumMapper.modelToEntity(input.vmrType()));
        output.setHostView(EnumMapper.modelToEntity(input.hostView()));
        output.setGuestView(EnumMapper.modelToEntity(input.guestView()));
        output.setVmrQuality(EnumMapper.modelToEntity(input.vmrQuality()));
        output.setEnableOverlayText(input.enableOverlayText());
        output.setGuestsCanPresent(input.guestsCanPresent());
        output.setForcePresenterIntoMain(input.forcePresenterIntoMain());
        output.setForceEncryption(input.forceEncryption());
        output.setMuteAllGuests(input.muteAllGuests());
        output.setUriWithoutDomain(input.uriWithoutDomain());
        output.setHostPin(input.hostPin());
        output.setGuestPin(input.guestPin());
        if (input.additionalInformation() != null) {
            output.setAdditionalInformation(modelToDto(input.additionalInformation()));
        }

        return output;
    }

    public static UpdateMeetingDto modelToDto(UpdateMeetingModel input) {
        var output = new UpdateMeetingDto();
        output.setSubject(input.subject());
        output.setStartTime(Date.from(input.startTime().toInstant()));
        output.setEndTime(Date.from(input.endTime().toInstant()));
        output.setDescription(input.description());
        output.setProjectCode(input.projectCode());
        output.setOrganizedByEmail(input.organizedByEmail());
        output.setLabels(input.labels() != null ? input.labels() : List.of());
        if (input.additionalInformation() != null) {
            output.setAdditionalInformation(modelToDto(input.additionalInformation()));
        }

        return output;
    }

    public static PatchMeetingDto modelToDto(PatchMeetingModel input) {
        var output = new PatchMeetingDto();
        if (input.subject() != null) {
            output.setSubject(input.subject());
        }
        if (input.startTime() != null) {
            output.setStartTime(Date.from(input.startTime().toInstant()));
        }
        if (input.endTime() != null) {
            output.setEndTime(Date.from(input.endTime().toInstant()));
        }
        if (input.description() != null) {
            output.setDescription(input.description());
        }
        if (input.projectCode() != null) {
            output.setProjectCode(input.projectCode());
        }
        if (input.organizedByEmail() != null) {
            output.setOrganizedByEmail(input.organizedByEmail());
        }
        if (input.labels() != null) {
            output.setLabels(input.labels());
        }
        if (input.guestMicrophone() != null) {
            output.setGuestMicrophone(EnumMapper.modelToEntity(input.guestMicrophone()));
        }
        if (input.guestPinRequired() != null) {
            output.setGuestPinRequired(input.guestPinRequired());
        }
        if (input.hostPin() != null) {
            output.setHostPin(input.hostPin());
        }
        if (input.guestPin() != null) {
            output.setGuestPin(input.guestPin());
        }
        if (input.additionalInformation() != null) {
            output.setAdditionalInformation(modelToDto(input.additionalInformation()));
        }

        return output;
    }

    private static List<AdditionalInformationType> modelToDto(List<AdditionalInformationModel> input) {
        return input.stream().map(x -> new AdditionalInformationType(x.key(), x.value())).toList();
    }
}
