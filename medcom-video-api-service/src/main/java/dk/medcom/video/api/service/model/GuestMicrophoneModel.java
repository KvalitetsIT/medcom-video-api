package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.GuestMicrophone;

public enum GuestMicrophoneModel {
    off,
    on,
    muted;

    public static GuestMicrophoneModel from(GuestMicrophone guestMicrophone) {
        return guestMicrophone != null ? GuestMicrophoneModel.valueOf(guestMicrophone.toString()) : null;
    }
}
