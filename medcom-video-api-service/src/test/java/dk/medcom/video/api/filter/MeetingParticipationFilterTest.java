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
        var schedulingInfo = randomSchedulingInfo();

        var result = MeetingParticipationFilter.matches(meeting, null, null, null, null, null);

        assertTrue(result);
    }

    @Test
    public void testMatchesSubject() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();

        var result = MeetingParticipationFilter.matches(meeting, null, null, meeting.getSubject(), null, null);

        assertTrue(result);
    }

    @Test
    public void testDoesNotMatchSubject() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();

        var result = MeetingParticipationFilter.matches(meeting, null, null, "non-matching-subject", null, null);

        assertFalse(result);
    }

    @Test
    public void testMatchesSubjectSubstring() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();
        var partialSubject = meeting.getSubject().substring(0, meeting.getSubject().length() - 2);

        var result = MeetingParticipationFilter.matches(meeting, null, null, partialSubject, null, null);

        assertTrue(result);
    }

    @Test
    public void testMatchesOrganizedBy() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();

        var result = MeetingParticipationFilter.matches(meeting, null, null, null, meeting.getOrganizedByUser().getEmail(), null);

        assertTrue(result);
    }

    @Test
    public void testDoesNotMatchOrganizedBy() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();

        var result = MeetingParticipationFilter.matches(meeting, null, null, null, "non-matching@email.dk", null);

        assertFalse(result);
    }

    @Test
    public void testMatchesLabel() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();
        var existingLabel = meeting.getMeetingLabels().iterator().next().getLabel();

        var result = MeetingParticipationFilter.matches(meeting, null, null, null, null, existingLabel);

        assertTrue(result);
    }

    @Test
    public void testDoesNotMatchLabel() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();

        var result = MeetingParticipationFilter.matches(meeting, null, null, null, null, "non-matching-label");

        assertFalse(result);
    }

    @Test
    public void testMatchesWhenStartTimeWithinInterval() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, meetingStart.minusHours(1), meetingStart.plusHours(1), null, null, null);

        assertTrue(result);
    }

    @Test
    public void testDoesNotMatchWhenStartTimeBeforeInterval() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, meetingStart.plusHours(1), meetingStart.plusHours(2), null, null, null);

        assertFalse(result);
    }

    @Test
    public void testDoesNotMatchWhenStartTimeAfterInterval() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, meetingStart.minusHours(2), meetingStart.minusHours(1), null, null, null);

        assertFalse(result);
    }

    @Test
    public void testIgnoresStartTimeIntervalWhenOnlyFromGiven() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, meetingStart.plusHours(5), null, null, null, null);

        assertTrue(result);
    }

    @Test
    public void testIgnoresStartTimeIntervalWhenOnlyToGiven() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting, null, meetingStart.minusHours(5), null, null, null);

        assertTrue(result);
    }

    @Test
    public void testMatchesAllFiltersCombined() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();
        var existingLabel = meeting.getMeetingLabels().iterator().next().getLabel();
        var meetingStart = meeting.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();

        var result = MeetingParticipationFilter.matches(meeting,
                meetingStart.minusHours(1), meetingStart.plusHours(1),
                meeting.getSubject(), meeting.getOrganizedByUser().getEmail(), existingLabel);

        assertTrue(result);
    }

    @Test
    public void testDoesNotMatchWhenOneOfSeveralFiltersFails() {
        var meeting = randomMeeting();
        var schedulingInfo = randomSchedulingInfo();
        var existingLabel = meeting.getMeetingLabels().iterator().next().getLabel();

        var result = MeetingParticipationFilter.matches(meeting,
                null, null,
                meeting.getSubject(), "non-matching@email.dk", existingLabel);

        assertFalse(result);
    }
}