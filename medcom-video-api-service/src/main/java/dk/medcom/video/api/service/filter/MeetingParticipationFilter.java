package dk.medcom.video.api.service.filter;

import dk.medcom.video.api.dao.entity.Meeting;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class MeetingParticipationFilter {

    public static boolean matches(Meeting meeting,
                                  OffsetDateTime fromStartTime,
                                  OffsetDateTime toStartTime,
                                  String subject,
                                  String organizedBy,
                                  String label) {

        if (fromStartTime != null && toStartTime != null) {
            var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
            if (meetingStart.isBefore(fromStartTime) || meetingStart.isAfter(toStartTime)) {
                return false;
            }
        }

        if (subject != null && !meeting.getSubject().contains(subject)) {
            return false;
        }

        if (organizedBy != null && (meeting.getOrganizedByUser() == null || !organizedBy.equals(meeting.getOrganizedByUser().getEmail()))) {
            return false;
        }

        if (label != null) {
            var hasLabel = meeting.getMeetingLabels().stream()
                    .anyMatch(l -> label.equals(l.getLabel()));
            if (!hasLabel) {
                return false;
            }
        }

        return true;
    }
}