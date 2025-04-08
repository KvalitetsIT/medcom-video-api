package dk.medcom.video.api.service.mapper;

import dk.medcom.video.api.api.PatchMeetingDto;
import dk.medcom.video.api.api.UpdateMeetingDto;
import dk.medcom.video.api.controller.exceptions.NotValidDataErrors;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingLabel;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.service.domain.AdditionalInformationType;
import dk.medcom.video.api.service.domain.GuestMicrophone;
import dk.medcom.video.api.service.domain.UpdateMeeting;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class DomainMapper {
    public UpdateMeeting mapToUpdateMeeting(UpdateMeetingDto updateMeetingDto, Meeting meeting, SchedulingInfo schedulingInfo) {
        var updateMeeting = new UpdateMeeting();
        updateMeeting.setMeetingAdditionalInfo(mapAdditionalInformationType(updateMeetingDto.getAdditionalInformation()));
        updateMeeting.setLabels(updateMeetingDto.getLabels());
        updateMeeting.setSubject(updateMeetingDto.getSubject());
        updateMeeting.setEndTime(updateMeetingDto.getEndTime());
        updateMeeting.setStartTime(updateMeetingDto.getStartTime());
        updateMeeting.setDescription(updateMeetingDto.getDescription());
        updateMeeting.setProjectCode(updateMeetingDto.getProjectCode());
        updateMeeting.setOrganizedByEmail(updateMeetingDto.getOrganizedByEmail());
        if(meeting.getGuestMicrophone() != null) {
            updateMeeting.setGuestMicrophone(GuestMicrophone.valueOf(meeting.getGuestMicrophone().name()));
        }
        updateMeeting.setGuestPinRequired(meeting.getGuestPinRequired());
        if(schedulingInfo.getGuestPin() != null) {
            updateMeeting.setGuestPin(new BigDecimal(schedulingInfo.getGuestPin()));
        }
        if(schedulingInfo.getHostPin() != null) {
            updateMeeting.setHostPin(new BigDecimal(schedulingInfo.getHostPin()));
        }

        return updateMeeting;
    }

    public UpdateMeeting mapToUpdateMeeting(PatchMeetingDto patchMeetingDto, Meeting meeting, SchedulingInfo schedulingInfo) throws NotValidDataException {
        // Map data from database entity to domain model.
        var updateMeetingDto = new UpdateMeeting();
        updateMeetingDto.setOrganizedByEmail(meeting.getOrganizedByUser().getEmail());
        updateMeetingDto.setDescription(meeting.getDescription());
        updateMeetingDto.setSubject(meeting.getSubject());
        updateMeetingDto.setProjectCode(meeting.getProjectCode());
        updateMeetingDto.setEndTime(meeting.getEndTime());
        updateMeetingDto.setStartTime(meeting.getStartTime());
        updateMeetingDto.getLabels().addAll(meeting.getMeetingLabels().stream().map(MeetingLabel::getLabel).toList());
        updateMeetingDto.setGuestPinRequired(meeting.getGuestPinRequired());
        updateMeetingDto.setMeetingAdditionalInfo(meeting.getMeetingAdditionalInfo().stream().map(x -> new AdditionalInformationType(x.getInfoKey(), x.getInfoValue())).toList());
        if(schedulingInfo.getHostPin() != null) {
            updateMeetingDto.setHostPin(new BigDecimal(schedulingInfo.getHostPin()));
        }
        if(schedulingInfo.getGuestPin() != null) {
            updateMeetingDto.setGuestPin(new BigDecimal(schedulingInfo.getGuestPin()));
        }
        if(meeting.getGuestMicrophone() != null) {
            updateMeetingDto.setGuestMicrophone(GuestMicrophone.valueOf(meeting.getGuestMicrophone().name()));
        }

        // Overwrite data in domain model with input data.
        if(patchMeetingDto.isProjectIsSet()) {
            updateMeetingDto.setProjectCode(patchMeetingDto.getProjectCode());
        }
        if(patchMeetingDto.isOrganizedByEmailIsSet()) {
            updateMeetingDto.setOrganizedByEmail(patchMeetingDto.getOrganizedByEmail());
        }
        if(patchMeetingDto.getDescriptionIsSet()) {
            updateMeetingDto.setDescription(patchMeetingDto.getDescription());
        }
        if(patchMeetingDto.isSubjectIsSet()) {
            if(patchMeetingDto.getSubject() == null) {
                throw new NotValidDataException(NotValidDataErrors.NULL_VALUE, "Subject");
            }
            updateMeetingDto.setSubject(patchMeetingDto.getSubject());
        }
        if(patchMeetingDto.isEndTimeIsSet()) {
            if(patchMeetingDto.getEndTime() == null) {
                throw new NotValidDataException(NotValidDataErrors.NULL_VALUE, "EndTime");
            }
            updateMeetingDto.setEndTime(patchMeetingDto.getEndTime());
        }
        if(patchMeetingDto.isStartTimeIsSet()) {
            if(patchMeetingDto.getStartTime() == null) {
                throw new NotValidDataException(NotValidDataErrors.NULL_VALUE, "StartTime");
            }
            updateMeetingDto.setStartTime(patchMeetingDto.getStartTime());
        }
        if(patchMeetingDto.isLabelsIsSet()) {
            if(patchMeetingDto.getLabels() == null) {
                updateMeetingDto.setLabels(Collections.emptyList());
            }
            else {
                updateMeetingDto.setLabels(patchMeetingDto.getLabels());
            }
        }
		if(patchMeetingDto.isGuestMicrophoneSet()) {
            updateMeetingDto.setGuestMicrophone(GuestMicrophone.valueOf(patchMeetingDto.getGuestMicrophone().name()));
		}
		if(patchMeetingDto.isGuestPinRequiredSet()) {
            updateMeetingDto.setGuestPinRequired(patchMeetingDto.isGuestPinRequired());
		}
        if(patchMeetingDto.isGuestPinSet()) {
            if(patchMeetingDto.getGuestPin() == null) {
                updateMeetingDto.setGuestPin(null);
            }
            else {
                updateMeetingDto.setGuestPin(new BigDecimal(patchMeetingDto.getGuestPin()));
            }
        }
        if(patchMeetingDto.isHostPinSet()) {
            if(patchMeetingDto.getHostPin() == null) {
                updateMeetingDto.setHostPin(null);
            }
            else {
                updateMeetingDto.setHostPin(new BigDecimal(patchMeetingDto.getHostPin()));
            }
        }
        if (patchMeetingDto.isAdditionalInfoSet()) {
            if (patchMeetingDto.getAdditionalInformation() == null) {
                updateMeetingDto.setMeetingAdditionalInfo(Collections.emptyList());
            } else {
                updateMeetingDto.setMeetingAdditionalInfo(mapAdditionalInformationType(patchMeetingDto.getAdditionalInformation()));
            }
        }

        return updateMeetingDto;
    }

    private List<AdditionalInformationType> mapAdditionalInformationType(List<dk.medcom.video.api.api.AdditionalInformationType> additionalInformationType) {
        return additionalInformationType.stream().map(x -> new AdditionalInformationType(x.key(), x.value())).toList();
    }
}
