package dk.medcom.video.api.service.domain;

import dk.medcom.video.api.api.GuestMicrophone;
import dk.medcom.video.api.api.PatchMeetingDto;
import dk.medcom.video.api.api.UpdateMeetingDto;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.service.DomainMapperService;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DomainMapperServiceTest {
    @Test public void testMapFromUpdateMeetingDto2UpdateMeeting() {
        // GIVEN
        var meeting = new Meeting();
        meeting.setGuestMicrophone(GuestMicrophone.muted);
        meeting.setGuestPinRequired(true);

        var input = new UpdateMeetingDto();
        input.setDescription("DESCRIPTION");
        input.setEndTime(new Date());
        input.setStartTime(new Date());
        input.setProjectCode("PROJECT_CODE");
        input.setOrganizedByEmail("ORG EMAIL");
        input.setSubject("SUBJECT");
        input.getLabels().add("LABEL");

        // WHEN
        var service = new DomainMapperService();
        var result = service.mapToUpdateMeeting(input, meeting);

        // THEN
        assertEquals(input.getDescription(), result.getDescription());
        assertEquals(input.getEndTime(), result.getEndTime());
        assertEquals(input.getStartTime(), result.getStartTime());
        assertEquals(input.getProjectCode(), result.getProjectCode());
        assertEquals(input.getOrganizedByEmail(), result.getOrganizedByEmail());
        assertEquals(input.getSubject(), result.getSubject());
        assertEquals(input.getLabels(), result.getLabels());
        assertEquals(meeting.getGuestMicrophone().toString(), result.getGuestMicrophone().toString());
        assertEquals(meeting.getGuestPinRequired(), result.isGuestPinRequired());
    }

    @Test
    public void testMapFromPatchMeetingDto2UpdateMeeting() throws NotValidDataException {
        // GIVEN
        var meetingUser = new MeetingUser();
        meetingUser.setEmail("EMAIL");

        var meeting = new Meeting();
        meeting.setGuestMicrophone(GuestMicrophone.on);
        meeting.setGuestPinRequired(false);
        meeting.setOrganizedByUser(meetingUser);

        var input = new PatchMeetingDto();
        input.setDescription("DESCRIPTION");
        input.setEndTime(new Date());
        input.setStartTime(new Date());
        input.setProjectCode("PROJECT_CODE");
        input.setOrganizedByEmail("ORG EMAIL");
        input.setSubject("SUBJECT");
        input.setLabels(Arrays.asList("LABEL"));
        input.setGuestMicrophone(GuestMicrophone.muted);
        input.setGuestPinRequired(true);

        // WHEN
        var service = new DomainMapperService();
        var result = service.mapToUpdateMeeting(input, meeting);

        // THEN
        assertEquals(input.getDescription(), result.getDescription());
        assertEquals(input.getEndTime(), result.getEndTime());
        assertEquals(input.getStartTime(), result.getStartTime());
        assertEquals(input.getProjectCode(), result.getProjectCode());
        assertEquals(input.getOrganizedByEmail(), result.getOrganizedByEmail());
        assertEquals(input.getSubject(), result.getSubject());
        assertEquals(input.getLabels(), result.getLabels());
        assertEquals(input.getGuestMicrophone().name(), result.getGuestMicrophone().toString());
        assertEquals(input.isGuestPinRequired(), result.isGuestPinRequired());
    }
}
