package dk.medcom.video.api.controller;

import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.MeetingDto;
import dk.medcom.video.api.dto.MeetingUserDto;
import dk.medcom.video.api.service.MeetingService;

@RestController
public class MeetingController {

	@Autowired
	MeetingService meetingService;
	
	@RequestMapping(value = "/meetings", method = RequestMethod.GET)
	public MeetingDto[] getMeetings() {

		List<Meeting> meetings = meetingService.getMeetings();
		List<MeetingDto> meetingDtos = new LinkedList<MeetingDto>();
		for (Meeting meeting : meetings) {
			meetingDtos.add(convert(meeting));
		}
		return meetingDtos.toArray(new MeetingDto[meetingDtos.size()]);
	}

	@RequestMapping(value = "/meeting/{uuid}", method = RequestMethod.GET)
	public MeetingDto getMeetingByUUID(@PathVariable("uuid") String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		Meeting meeting = meetingService.getMeetingByUuid(uuid);
		return convert(meeting);
	}

	@RequestMapping(value = "/meeting", method = RequestMethod.POST)
	public MeetingDto createMeeting(@Valid @RequestBody CreateMeetingDto createMeetingDto) throws RessourceNotFoundException {

		Meeting meeting = meetingService.createMeeting(createMeetingDto);
		return convert(meeting);
	}
	
	public MeetingDto convert(Meeting meeting) {
		MeetingDto meetingDto = new MeetingDto();
		meetingDto.setSubject(meeting.getSubject());
		meetingDto.setUuid(meeting.getUuid());
		
		MeetingUserDto meetingUserDto = new MeetingUserDto();
		meetingUserDto.setOrganisationId(meeting.getMeetingUser().getOrganisationId());
		meetingUserDto.setEmail(meeting.getMeetingUser().getEmail());
		
		meetingDto.setCreatedBy(meetingUserDto);
		meetingDto.setStartTime(meeting.getStartTime());
		meetingDto.setEndTime(meeting.getEndTime());
		meetingDto.setDescription(meeting.getDescription());
		return meetingDto;
	}
}
