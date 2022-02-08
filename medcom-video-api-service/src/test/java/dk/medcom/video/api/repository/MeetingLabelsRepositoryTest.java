package dk.medcom.video.api.repository;

import dk.medcom.video.api.dao.MeetingLabelRepository;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingLabel;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Organisation;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class MeetingLabelsRepositoryTest extends RepositoryTest {
    @Resource
    private MeetingLabelRepository meetingLabelRepository;

    @Resource
    private MeetingRepository meetingRepository;

    @Resource
    private MeetingUserRepository meetingUserRepository;

    @Resource
    private OrganisationRepository organisationRepository;

    @Test
    public void tesFindAllLabels() {
        Meeting meeting = meetingRepository.findById(7L).orElse(null);

        List<MeetingLabel> meetingLabels = meetingLabelRepository.findByMeeting(meeting);

        assertEquals(2, meetingLabels.size());

        MeetingLabel firstMeetingLabel = meetingLabels.get(0);
        assertEquals("first label", firstMeetingLabel.getLabel());
        assertEquals(meeting.getId(), firstMeetingLabel.getMeeting().getId());

        MeetingLabel secondMeetingLabel = meetingLabels.get(1);
        assertEquals("second label", secondMeetingLabel.getLabel());
        assertEquals(meeting.getId(), secondMeetingLabel.getMeeting().getId());
    }

    @Test
    public void testSave() {
        String uuid = UUID.randomUUID().toString();
        Long meetingUserId = 101L;
        Long organisationId = 5L;
        String projectCode = "PROJECT1";

        Meeting meeting = new Meeting();
        meeting.setSubject("Test meeting");
        meeting.setUuid(uuid);

        Organisation organisation = organisationRepository.findById(organisationId).orElse(null);
        meeting.setOrganisation(organisation);

        MeetingUser meetingUser = meetingUserRepository.findById(meetingUserId).orElse(null);
        meeting.setMeetingUser(meetingUser);

        meeting.setOrganizedByUser(meetingUser);
        meeting.setUpdatedByUser(meetingUser);

        Calendar calendarStart = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,13,15, 0);
        meeting.setStartTime(calendarStart.getTime());

        Calendar calendarEnd = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,13,30, 0);
        meeting.setEndTime(calendarEnd.getTime());
        meeting.setDescription("Lang beskrivelse af, hvad der foregår");
        meeting.setProjectCode(projectCode);

        Calendar calendarCreate = new GregorianCalendar(2018, Calendar.SEPTEMBER, 1,13,30, 0);
        meeting.setCreatedTime(calendarCreate.getTime());
        meeting.setUpdatedTime(calendarCreate.getTime());
        meeting.setShortId(createShortId());


        // When
        meeting = meetingRepository.save(meeting);

        MeetingLabel meetingLabel = new MeetingLabel();
        meetingLabel.setLabel("this is a label");
        meetingLabel.setMeeting(meeting);
        MeetingLabel result = meetingLabelRepository.save(meetingLabel);

        assertNotNull(result);
        assertEquals(meetingLabel.getLabel(), result.getLabel());
        assertEquals(meeting.getId(), result.getMeeting().getId());
    }

    private String createShortId() {
        return UUID.randomUUID().toString().substring(1, 8);
    }

    @Test
    public void testDeleteById() {
        String uuid = UUID.randomUUID().toString();
        Long meetingUserId = 101L;
        Long organisationId = 5L;
        String projectCode = "PROJECT1";

        Meeting meeting = new Meeting();
        meeting.setSubject("Test meeting");
        meeting.setUuid(uuid);

        Organisation organisation = organisationRepository.findById(organisationId).orElse(null);
        meeting.setOrganisation(organisation);

        MeetingUser meetingUser = meetingUserRepository.findById(meetingUserId).orElse(null);
        meeting.setMeetingUser(meetingUser);

        meeting.setOrganizedByUser(meetingUser);
        meeting.setUpdatedByUser(meetingUser);

        Calendar calendarStart = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,13,15, 0);
        meeting.setStartTime(calendarStart.getTime());

        Calendar calendarEnd = new GregorianCalendar(2018, Calendar.NOVEMBER, 1,13,30, 0);
        meeting.setEndTime(calendarEnd.getTime());
        meeting.setDescription("Lang beskrivelse af, hvad der foregår");
        meeting.setProjectCode(projectCode);

        Calendar calendarCreate = new GregorianCalendar(2018, Calendar.SEPTEMBER, 1,13,30, 0);
        meeting.setCreatedTime(calendarCreate.getTime());
        meeting.setUpdatedTime(calendarCreate.getTime());
        meeting.setShortId(createShortId());
        // When
        meeting = meetingRepository.save(meeting);

        MeetingLabel firstMeetingLabel = new MeetingLabel();
        firstMeetingLabel.setLabel("this is a label");
        firstMeetingLabel.setMeeting(meeting);

        MeetingLabel secondMeetingLabel = new MeetingLabel();
        secondMeetingLabel.setLabel("this is a label");
        secondMeetingLabel.setMeeting(meeting);

        Iterable<MeetingLabel> labels = meetingLabelRepository.saveAll(Arrays.asList(firstMeetingLabel, secondMeetingLabel));
        AtomicInteger count = new AtomicInteger();
        labels.forEach(x -> count.getAndIncrement());
        assertEquals(2, count.get());

        meetingLabelRepository.deleteByMeeting(meeting);

        labels = meetingLabelRepository.findByMeeting(meeting);
        assertFalse(labels.iterator().hasNext());

    }
}
