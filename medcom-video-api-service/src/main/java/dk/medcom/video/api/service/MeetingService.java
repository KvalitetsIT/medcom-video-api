package dk.medcom.video.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.repository.MeetingRepository;

@Component
public class MeetingService {

	@Autowired
	MeetingRepository meetingRepository;
	
	@Autowired
	UserContext userService;
	
	public List<Meeting> getMeetings() {
		
		return meetingRepository.findByOrganisationId(userService.getUserOrganisation());
	}

	public Meeting getMeetingByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		Meeting meeting = meetingRepository.findOneByUuid(uuid);
		if (meeting == null) {
			throw new RessourceNotFoundException("meeting", "uuid");
		}
		if (!meeting.getOrganisationId().equals(userService.getUserOrganisation())) {
			throw new PermissionDeniedException();
		}
		return meeting;
	}

	public Meeting createMeeting(CreateMeetingDto createMeetingDto) {
		Meeting meeting = convert(createMeetingDto);
		meeting = meetingRepository.save(meeting);
		return meeting;
	}

	public Meeting convert(CreateMeetingDto createMeetingDto) {
		Meeting meeting = new Meeting();
		meeting.setSubject(createMeetingDto.getSubject());
		meeting.setUuid(UUID.randomUUID().toString());
		meeting.setOrganisationId(userService.getUserOrganisation());
		return meeting;
	}
}
