package dk.medcom.video.api.controller;

import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingLabel;
import dk.medcom.video.api.api.PatchMeetingDto;
import dk.medcom.video.api.api.UpdateMeetingDto;
import dk.medcom.video.api.service.MeetingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MeetingUpdateControllerTest {
    private MeetingUpdateController meetingUpdateController;
    private MeetingService meetingService;

    @Before
    public void setup() {
        meetingService = Mockito.mock(MeetingService.class);
        meetingUpdateController = new MeetingUpdateController(meetingService, "short_link_base_url");
    }

    @Test
    public void testPatchMeeting() throws NotAcceptableException, PermissionDeniedException, RessourceNotFoundException, NotValidDataException {
        UUID uuid = UUID.randomUUID();
        var input = new PatchMeetingDto();
        input.setDescription("DESCRIPTION");
        input.setLabels(Collections.singletonList("SOME_LABEL"));

        var meetingLabel = new MeetingLabel();
        meetingLabel.setLabel(input.getLabels().get(0));

        var meeting = new Meeting();
        meeting.setDescription(input.getDescription());
        meeting.getMeetingLabels().add(meetingLabel);

        Mockito.when(meetingService.patchMeeting(uuid, input)).thenReturn(meeting);

        var result = meetingUpdateController.patchMeeting(uuid, input);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(input.getDescription(), result.getContent().getDescription());
        assertEquals(1, result.getContent().getLabels().size());
        assertEquals(input.getLabels().get(0), result.getContent().getLabels().get(0));
    }

    @Test
    public void testUpdateMeeting() throws PermissionDeniedException, NotValidDataException, RessourceNotFoundException, NotAcceptableException {
        var now = Calendar.getInstance();
        var startTime = now.getTime();
        now.add(Calendar.HOUR, 2);
        var endTime = now.getTime();

        UUID uuid = UUID.randomUUID();
        var input = new UpdateMeetingDto();
        input.setEndTime(endTime);
        input.setStartTime(startTime);
        input.setSubject("SUBJECT");
        input.setDescription("DESCRIPTION");
        input.setOrganizedByEmail("EMAIL");
        input.setProjectCode("PROJECT_CODE");
        input.getLabels().add("SOME_LABEL");

        var meetingLabel = new MeetingLabel();
        meetingLabel.setLabel(input.getLabels().get(0));

        var meeting = new Meeting();
        meeting.setSubject(input.getSubject());
        meeting.setEndTime(input.getEndTime());
        meeting.setStartTime(input.getStartTime());
        meeting.setDescription(input.getDescription());
        meeting.setProjectCode(input.getProjectCode());
        meeting.getMeetingLabels().add(meetingLabel);

        Mockito.when(meetingService.updateMeeting(uuid.toString(), input)).thenReturn(meeting);

        var result = meetingUpdateController.updateMeeting(uuid.toString(), input);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(input.getEndTime(), result.getContent().getEndTime());
        assertEquals(input.getStartTime(), result.getContent().getStartTime());
        assertEquals(input.getSubject(), result.getContent().getSubject());
        assertEquals(input.getDescription(), result.getContent().getDescription());
        assertEquals(input.getProjectCode(), result.getContent().projectCode());
        assertEquals(1, result.getContent().getLabels().size());
        assertEquals(input.getLabels().get(0), result.getContent().getLabels().get(0));
    }
}
