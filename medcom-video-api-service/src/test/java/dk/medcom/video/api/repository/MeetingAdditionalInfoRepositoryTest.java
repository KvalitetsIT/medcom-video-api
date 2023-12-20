package dk.medcom.video.api.repository;

import dk.medcom.video.api.dao.MeetingAdditionalInfoRepository;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.MeetingAdditionalInfo;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Organisation;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class MeetingAdditionalInfoRepositoryTest extends RepositoryTest {

    @Resource
    private MeetingAdditionalInfoRepository additionalInfoRepository;

    @Resource
    private MeetingRepository meetingRepository;

    @Resource
    private MeetingUserRepository meetingUserRepository;

    @Resource
    private OrganisationRepository organisationRepository;

    @Test
    public void testSaveAllAndDelete() {
        // Given
        var meeting = setupMeeting();
        meeting = meetingRepository.save(meeting);

        Set<MeetingAdditionalInfo> meetingAdditionalInfos = new HashSet<>();
        meetingAdditionalInfos.add(createAdditionalInfo("key one", "value one", meeting));
        meetingAdditionalInfos.add(createAdditionalInfo("key two", "value two", meeting));

        // When - saveAll
        var result = additionalInfoRepository.saveAll(meetingAdditionalInfos); // Ensures that SQL does not throw an exception.

        // Then
        assertNotNull(result);

        AtomicInteger count = new AtomicInteger();
        result.forEach(x -> count.getAndIncrement());
        assertEquals(2, count.get());

        var additionalInformation = new ArrayList<MeetingAdditionalInfo>();
        result.forEach(additionalInformation::add);

        var meetingAdditionalInfoOne = additionalInformation.stream().filter(x -> x.getInfoKey().equals("key one")).findFirst();
        assertTrue(meetingAdditionalInfoOne.isPresent());
        assertEquals("value one", meetingAdditionalInfoOne.get().getInfoValue());
        assertEquals(meeting.getId(), meetingAdditionalInfoOne.get().getMeeting().getId());

        var meetingAdditionalInfoTwo = additionalInformation.stream().filter(x -> x.getInfoKey().equals("key two")).findFirst();
        assertTrue(meetingAdditionalInfoTwo.isPresent());
        assertEquals("value two", meetingAdditionalInfoTwo.get().getInfoValue());
        assertEquals(meeting.getId(), meetingAdditionalInfoTwo.get().getMeeting().getId());

        // When - delete
        additionalInfoRepository.deleteByMeeting(meeting);

        // Then
        var additionalInfoOne = additionalInfoRepository.findById(meetingAdditionalInfoOne.get().getId());
        assertTrue(additionalInfoOne.isEmpty());
        var additionalInfoTwo = additionalInfoRepository.findById(meetingAdditionalInfoTwo.get().getId());
        assertTrue(additionalInfoTwo.isEmpty());
    }

    private String createShortId() {
        return UUID.randomUUID().toString().substring(1, 8);
    }

    private MeetingAdditionalInfo createAdditionalInfo(String key, String value, Meeting meeting) {
        var additionalInfo = new MeetingAdditionalInfo();
        additionalInfo.setMeeting(meeting);
        additionalInfo.setInfoKey(key);
        additionalInfo.setInfoValue(value);

        return additionalInfo;
    }

    private Meeting setupMeeting() {
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

        meeting.setStartTime(new Date());

        meeting.setEndTime(new Date());
        meeting.setProjectCode(projectCode);

        meeting.setCreatedTime(new Date());
        meeting.setUpdatedTime(new Date());
        meeting.setShortId(createShortId());

        return meeting;
    }

}
