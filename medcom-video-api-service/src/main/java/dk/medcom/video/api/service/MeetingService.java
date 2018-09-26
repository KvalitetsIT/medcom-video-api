package dk.medcom.video.api.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
	MeetingUserService meetingUserService;
	
	@Autowired
	SchedulingInfoService schedulingInfoService;
	
	@Autowired
	OrganisationService organisationService;
	
	
	public List<Meeting> getMeetings(Date fromStartTime, Date toStartTime) {
	
		return meetingRepository.findByOrganisationAndStartTimeBetween(organisationService.getUserOrganisation(), fromStartTime, toStartTime);
	}

	public Meeting getMeetingByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		Meeting meeting = meetingRepository.findOneByUuid(uuid);
		if (meeting == null) {
			throw new RessourceNotFoundException("meeting", "uuid");
		}
		if (!meeting.getOrganisation().equals(organisationService.getUserOrganisation())) {
			throw new PermissionDeniedException();
		}
		return meeting;
	}

	public Meeting createMeeting(CreateMeetingDto createMeetingDto) throws RessourceNotFoundException {
		Meeting meeting = convert(createMeetingDto);
		meeting.setMeetingUser(meetingUserService.getOrCreateCurrentMeetingUser());
		
		meeting = meetingRepository.save(meeting);
		if (meeting != null) {
			schedulingInfoService.createSchedulingInfo(meeting);
		}
		return meeting;
	}

	public Meeting convert(CreateMeetingDto createMeetingDto) {
		Meeting meeting = new Meeting();
		meeting.setSubject(createMeetingDto.getSubject());
		meeting.setUuid(UUID.randomUUID().toString());
		meeting.setOrganisation(organisationService.getUserOrganisation());
		
		meeting.setStartTime(createMeetingDto.getStartTime());
		meeting.setEndTime(createMeetingDto.getEndTime());
		meeting.setDescription(createMeetingDto.getDescription());
	
		return meeting;
	}
}
