package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.MeetingAdditionalInfo;

public record AdditionalInformationModel(String key, String value) {
    public static AdditionalInformationModel from(MeetingAdditionalInfo meetingAdditionalInfo) {
        return new AdditionalInformationModel(meetingAdditionalInfo.getInfoKey(), meetingAdditionalInfo.getInfoValue());
    }
}
