package dk.medcom.video.api.service;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.entity.MeetingUser;

public interface MeetingUserService {
    MeetingUser getOrCreateCurrentMeetingUser() throws PermissionDeniedException;

    MeetingUser getOrCreateCurrentMeetingUser(String email) throws PermissionDeniedException;
}
