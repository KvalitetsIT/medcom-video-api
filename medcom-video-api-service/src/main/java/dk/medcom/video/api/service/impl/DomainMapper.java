package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.api.PatchMeetingDto;
import dk.medcom.video.api.api.UpdateMeetingDto;
import dk.medcom.video.api.controller.exceptions.NotValidDataErrors;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingLabel;
import dk.medcom.video.api.service.domain.GuestMicrophone;
import dk.medcom.video.api.service.domain.UpdateMeeting;

import java.util.Collections;
import java.util.stream.Collectors;

public class DomainMapper {
    public UpdateMeeting mapToUpdateMeeting(UpdateMeetingDto updateMeetingDto, Meeting meeting) {
        var updateMeeting = new UpdateMeeting();
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

        return updateMeeting;
    }

    public UpdateMeeting mapToUpdateMeeting(PatchMeetingDto patchMeetingDto, Meeting meeting) throws NotValidDataException {
        // Map data from database entity to domain model.
        var updateMeetingDto = new UpdateMeeting();
        updateMeetingDto.setOrganizedByEmail(meeting.getOrganizedByUser().getEmail());
        updateMeetingDto.setDescription(meeting.getDescription());
        updateMeetingDto.setSubject(meeting.getSubject());
        updateMeetingDto.setProjectCode(meeting.getProjectCode());
        updateMeetingDto.setEndTime(meeting.getEndTime());
        updateMeetingDto.setStartTime(meeting.getStartTime());
        updateMeetingDto.getLabels().addAll(meeting.getMeetingLabels().stream().map(MeetingLabel::getLabel).collect(Collectors.toList()));
        updateMeetingDto.setGuestMicrophone(GuestMicrophone.valueOf(meeting.getGuestMicrophone().name()));
        updateMeetingDto.setGuestPinRequired(meeting.getGuestPinRequired());
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

		return updateMeetingDto;
    }
}
