package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;

public interface MeetingUserService {
    MeetingUser getOrCreateCurrentMeetingUser() throws PermissionDeniedExceptionV2;

    MeetingUser getOrCreateCurrentMeetingUser(String email) throws PermissionDeniedExceptionV2;
}
