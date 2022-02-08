package dk.medcom.video.api.api;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class CreateMeetingDtoTest {
    @Before
    public void adjustTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
    }

    @Test
    public void deserializeDateFields() throws IOException {
        String input = "{\n" +
                "    \"subject\": \"this is subject\", \n" +
                "    \"startTime\": \"2019-12-11T16:01:11 +0100\",\n" +
                "    \"endTime\": \"2019-12-11T17:01:11 +0100\", \n" +
                "    \"description\": \"this is description\",\n" +
                "    \"projectCode\": \"this is projectCode\",\n" +
                "    \"organizedByEmail\": \"email\",\n" +
                "    \"maxParticipants\": 10,\n" +
                "    \"endMeetingOnEndTime\": true,\n" +
                "    \"schedulingTemplateId\": 11,\n" +
                "    \"meetingType\": \"POOL\",\n" +
                "    \"uuid\": \"123e4567-e89b-12d3-a456-426655440000\"\n" +
                "}";

        CreateMeetingDto createMeetingDto = new ObjectMapper().readValue(input, CreateMeetingDto.class);

        assertEquals("this is subject", createMeetingDto.getSubject());
        assertEquals(new GregorianCalendar(2019, Calendar.DECEMBER, 11, 16, 1, 11).getTime(), createMeetingDto.getStartTime());
        assertEquals(new GregorianCalendar(2019, Calendar.DECEMBER, 11, 17, 1, 11).getTime(), createMeetingDto.getEndTime());
        assertEquals("this is description", createMeetingDto.getDescription());
        assertEquals("this is projectCode", createMeetingDto.getProjectCode());
        assertEquals("email", createMeetingDto.getOrganizedByEmail());
        assertEquals(10, createMeetingDto.getMaxParticipants());
        assertEquals(true, createMeetingDto.isEndMeetingOnEndTime());
        assertEquals(11, createMeetingDto.getSchedulingTemplateId().longValue());
        assertEquals(MeetingType.POOL, createMeetingDto.getMeetingType());
        assertEquals(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426655440000"), createMeetingDto.getUuid());
    }

    @Test
    public void deserializeDateFieldsSecondFormat() throws IOException {
        String input = "{\n" +
                "    \"subject\": \"this is subject\", \n" +
                "    \"startTime\": \"2019-12-11T16:01:11+0100\",\n" +
                "    \"endTime\": \"2019-12-11T17:01:11+0100\", \n" +
                "    \"description\": \"this is description\",\n" +
                "    \"projectCode\": \"this is projectCode\",\n" +
                "    \"organizedByEmail\": \"email\",\n" +
                "    \"maxParticipants\": 10,\n" +
                "    \"endMeetingOnEndTime\": true,\n" +
                "    \"schedulingTemplateId\": 11,\n" +
                "    \"meetingType\": \"POOL\",\n" +
                "    \"uuid\": \"123e4567-e89b-12d3-a456-426655440000\"\n" +
                "}";

        CreateMeetingDto createMeetingDto = new ObjectMapper().readValue(input, CreateMeetingDto.class);

        assertEquals("this is subject", createMeetingDto.getSubject());
        assertEquals(new GregorianCalendar(2019, Calendar.DECEMBER, 11, 16, 1, 11).getTime(), createMeetingDto.getStartTime());
        assertEquals(new GregorianCalendar(2019, Calendar.DECEMBER, 11, 17, 1, 11).getTime(), createMeetingDto.getEndTime());
        assertEquals("this is description", createMeetingDto.getDescription());
        assertEquals("this is projectCode", createMeetingDto.getProjectCode());
        assertEquals("email", createMeetingDto.getOrganizedByEmail());
        assertEquals(10, createMeetingDto.getMaxParticipants());
        assertEquals(true, createMeetingDto.isEndMeetingOnEndTime());
        assertEquals(11, createMeetingDto.getSchedulingTemplateId().longValue());
        assertEquals(MeetingType.POOL, createMeetingDto.getMeetingType());
        assertEquals(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426655440000"), createMeetingDto.getUuid());
    }


    @Test(expected = JsonMappingException.class)
    public void errorOnInvalidFormat() throws IOException {
        String input = "{\n" +
                "    \"subject\": \"this is subject\", \n" +
                "    \"startTime\": \"2019-12-11T16:01:11+x100\",\n" +
                "    \"endTime\": \"2019-12-11T17:01:11+0100\", \n" +
                "    \"description\": \"this is description\",\n" +
                "    \"projectCode\": \"this is projectCode\",\n" +
                "    \"organizedByEmail\": \"email\",\n" +
                "    \"maxParticipants\": 10,\n" +
                "    \"endMeetingOnEndTime\": true,\n" +
                "    \"schedulingTemplateId\": 11,\n" +
                "    \"meetingType\": \"POOL\",\n" +
                "    \"uuid\": \"123e4567-e89b-12d3-a456-426655440000\"\n" +
                "}";

        CreateMeetingDto createMeetingDto = new ObjectMapper().readValue(input, CreateMeetingDto.class);

        assertEquals("this is subject", createMeetingDto.getSubject());
        assertEquals(new GregorianCalendar(2019, Calendar.DECEMBER, 11, 16, 1, 11).getTime(), createMeetingDto.getStartTime());
        assertEquals(new GregorianCalendar(2019, Calendar.DECEMBER, 11, 17, 1, 11).getTime(), createMeetingDto.getEndTime());
        assertEquals("this is description", createMeetingDto.getDescription());
        assertEquals("this is projectCode", createMeetingDto.getProjectCode());
        assertEquals("email", createMeetingDto.getOrganizedByEmail());
        assertEquals(10, createMeetingDto.getMaxParticipants());
        assertEquals(true, createMeetingDto.isEndMeetingOnEndTime());
        assertEquals(11, createMeetingDto.getSchedulingTemplateId().longValue());
        assertEquals(MeetingType.POOL, createMeetingDto.getMeetingType());
        assertEquals(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426655440000"), createMeetingDto.getUuid());
    }
}
