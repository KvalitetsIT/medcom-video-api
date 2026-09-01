package dk.medcom.video.api.service.filter;

import dk.medcom.video.api.dao.entity.Meeting;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class MeetingParticipationFilter {

    public static boolean matches(Meeting meeting,
                                  OffsetDateTime fromStartTime,
                                  OffsetDateTime toStartTime) {

        if (fromStartTime != null && toStartTime != null) {
            var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
            return !meetingStart.isBefore(fromStartTime) && !meetingStart.isAfter(toStartTime);
        }

        return true;
    }
}