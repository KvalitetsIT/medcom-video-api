package dk.medcom.video.api.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingUser; //TODO - include?
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.MeetingDto;
import dk.medcom.video.api.service.MeetingService;
import dk.medcom.video.api.service.MeetingUserService; //TODO - include?

@RestController
public class MeetingController {

	@Autowired
	MeetingService meetingService;
	
	@Autowired
	MeetingUserService meetingUserService; //TODO: her?

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
	public MeetingDto createMeeting(@RequestBody CreateMeetingDto createMeetingDto) {
		//***********************************************************************
		//TODO: hvor skal det laves?
		//TODO: skal dto objecgt med ned for meeting user?
		MeetingUser meetingUser = meetingUserService.createMeetingUser();
		//TODO: inkluder meetinguser i create, eller sæt på efterfølgende?
		
		Meeting meeting = meetingService.createMeeting(createMeetingDto);
		return convert(meeting);
	}
	
	public MeetingDto convert(Meeting meeting) {
		MeetingDto meetingDto = new MeetingDto();
		meetingDto.setSubject(meeting.getSubject());
		meetingDto.setUuid(meeting.getUuid());
		return meetingDto;
	}
}
