package dk.medcom.video.api.service.domain;

import dk.medcom.video.api.api.GuestMicrophone;
import dk.medcom.video.api.api.PatchMeetingDto;
import dk.medcom.video.api.api.UpdateMeetingDto;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.service.DomainMapper;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DomainMapperTest {
    @Test public void testMapFromUpdateMeetingDto2UpdateMeeting() {
        // GIVEN
        var meeting = new Meeting();
        meeting.setGuestMicrophone(GuestMicrophone.muted);
        meeting.setGuestPinRequired(true);

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setHostPin(1234L);
        schedulingInfo.setGuestPin(4321L);

        var input = new UpdateMeetingDto();
        input.setDescription("DESCRIPTION");
        input.setEndTime(new Date());
        input.setStartTime(new Date());
        input.setProjectCode("PROJECT_CODE");
        input.setOrganizedByEmail("ORG EMAIL");
        input.setSubject("SUBJECT");
        input.getLabels().add("LABEL");

        // WHEN
        var service = new DomainMapper();
        var result = service.mapToUpdateMeeting(input, meeting, schedulingInfo);

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
        assertEquals(schedulingInfo.getHostPin().longValue(), result.getHostPin().longValue());
        assertEquals(schedulingInfo.getGuestPin().longValue(), result.getGuestPin().longValue());
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

        var schedulingInfo = new SchedulingInfo();

        var input = new PatchMeetingDto();
        input.setDescription("DESCRIPTION");
        input.setEndTime(new Date());
        input.setStartTime(new Date());
        input.setProjectCode("PROJECT_CODE");
        input.setOrganizedByEmail("ORG EMAIL");
        input.setSubject("SUBJECT");
        input.setLabels(Collections.singletonList("LABEL"));
        input.setGuestMicrophone(GuestMicrophone.muted);
        input.setGuestPinRequired(true);
        input.setHostPin(1234);
        input.setGuestPin(4321);

        // WHEN
        var service = new DomainMapper();
        var result = service.mapToUpdateMeeting(input, meeting, schedulingInfo);

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
        assertEquals(input.getHostPin().intValue(), result.getHostPin().intValue());
        assertEquals(input.getGuestPin().intValue(), result.getGuestPin().intValue());
    }

    @Test
    public void testGuestMicrophoneAndPinNotSet() throws NotValidDataException {
        // GIVEN
        var meetingUser = new MeetingUser();
        meetingUser.setEmail("EMAIL");

        var schedulingInfo = new SchedulingInfo();

        var meeting = new Meeting();
        meeting.setGuestMicrophone(GuestMicrophone.muted);
        meeting.setGuestPinRequired(true);
        meeting.setOrganizedByUser(meetingUser);

        var input = new PatchMeetingDto();
        input.setDescription("DESCRIPTION");

        // WHEN
        var service = new DomainMapper();
        var result = service.mapToUpdateMeeting(input, meeting, schedulingInfo);

        // THEN
        assertEquals(dk.medcom.video.api.service.domain.GuestMicrophone.muted, result.getGuestMicrophone());
        assertTrue(result.isGuestPinRequired());
    }

    @Test
    public void testPinNotSet() throws NotValidDataException {
        // GIVEN
        var meetingUser = new MeetingUser();
        meetingUser.setEmail("EMAIL");

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setHostPin(1234L);
        schedulingInfo.setGuestPin(4321L);

        var meeting = new Meeting();
        meeting.setGuestMicrophone(GuestMicrophone.muted);
        meeting.setGuestPinRequired(true);
        meeting.setOrganizedByUser(meetingUser);

        var input = new PatchMeetingDto();

        // WHEN
        var service = new DomainMapper();
        var result = service.mapToUpdateMeeting(input, meeting, schedulingInfo);

        // THEN
        assertEquals(schedulingInfo.getHostPin().longValue(), result.getHostPin().longValue());
        assertEquals(schedulingInfo.getGuestPin().longValue(), result.getGuestPin().longValue());
    }
}
