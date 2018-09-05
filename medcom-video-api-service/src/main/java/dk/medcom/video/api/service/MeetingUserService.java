//TODO: skal denne klasse findes?

package dk.medcom.video.api.service;

//import java.util.List;
//import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.MeetingUser;
//import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.repository.MeetingUserRepository;

@Component
public class MeetingUserService {

	@Autowired
	MeetingUserRepository meetingUserRepository;
	
	@Autowired
	UserContext userService;
	
//	public List<Meeting> getMeetings() {
//		
//		return meetingRepository.findByOrganisationId(userService.getUserOrganisation());
//	}

//	public Meeting getMeetingByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
//		Meeting meeting = meetingRepository.findOneByUuid(uuid);
//		if (meeting == null) {
//			throw new RessourceNotFoundException("meeting", "uuid");
//		}
//		if (!meeting.getOrganisationId().equals(userService.getUserOrganisation())) {
//			throw new PermissionDeniedException();
//		}
//		return meeting;
//	}

	public MeetingUser createMeetingUser() {
//		MeetingUser meetingUser = convert(createMeetingDto);
//		meetingUser = meetingUserRepository.save(meetingUser);
		//TODO: skal man ned igennem convert?
		MeetingUser meetingUser = new MeetingUser();
		meetingUser.setEmail("me@meme.dk"); //TODO: get email from user
		meetingUser.setOrganisationId(userService.getUserOrganisation());
		return meetingUser;
	}

//	public Meeting convert(CreateMeetingDto createMeetingDto) {
//		Meeting meeting = new Meeting();
//		meeting.setSubject(createMeetingDto.getSubject());
//		meeting.setUuid(UUID.randomUUID().toString());
//		meeting.setOrganisationId(userService.getUserOrganisation());
//		meeting.setOrganisationId(userService.get);
//		return meeting;
//	}
}
