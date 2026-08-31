package dk.medcom.video.api.filter;

import dk.medcom.video.api.service.filter.MeetingParticipationFilter;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static dk.medcom.video.api.service.impl.v2.HelperMethods.randomMeeting;
import static dk.medcom.video.api.service.impl.v2.HelperMethods.randomSchedulingInfo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MeetingParticipationFilterTest {

    @Test
    public void testMatchesWhenNoFiltersGiven() {
        var meeting = randomMeeting();

        var result = MeetingParticipationFilter.matches(meeting, null, null);

        assertTrue(result);
    }


    @Test
    public void testMatchesWhenStartTimeWithinInterval() {
        var meeting = randomMeeting();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, meetingStart.minusHours(1), meetingStart.plusHours(1));

        assertTrue(result);
    }

    @Test
    public void testDoesNotMatchWhenStartTimeBeforeInterval() {
        var meeting = randomMeeting();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, meetingStart.plusHours(1), meetingStart.plusHours(2));

        assertFalse(result);
    }

    @Test
    public void testDoesNotMatchWhenStartTimeAfterInterval() {
        var meeting = randomMeeting();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, meetingStart.minusHours(2), meetingStart.minusHours(1));

        assertFalse(result);
    }

    @Test
    public void testIgnoresStartTimeIntervalWhenOnlyFromGiven() {
        var meeting = randomMeeting();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, meetingStart.plusHours(5), null);

        assertTrue(result);
    }

    @Test
    public void testIgnoresStartTimeIntervalWhenOnlyToGiven() {
        var meeting = randomMeeting();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, null, meetingStart.minusHours(5));

        assertTrue(result);
    }
}