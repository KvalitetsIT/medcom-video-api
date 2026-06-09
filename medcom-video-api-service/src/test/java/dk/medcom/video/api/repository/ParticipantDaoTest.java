package dk.medcom.video.api.repository;

import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.ParticipantDao;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.Participant;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipantDaoTest extends RepositoryTest {

    @Resource
    private ParticipantDao subject;

    @Resource
    private MeetingRepository meetingRepository;

    @Test
    public void testFindByMeeting() {
        Meeting meeting = meetingRepository.findById(1L).orElse(null);
        assertNotNull(meeting);

        List<Participant> result = subject.findByMeeting(meeting);

        assertEquals(2, result.size());
    }

    @Test
    public void testFindByMeetingNoParticipants() {
        Meeting meeting = meetingRepository.findById(3L).orElse(null);
        assertNotNull(meeting);

        List<Participant> result = subject.findByMeeting(meeting);

        assertEquals(0, result.size());
    }
}