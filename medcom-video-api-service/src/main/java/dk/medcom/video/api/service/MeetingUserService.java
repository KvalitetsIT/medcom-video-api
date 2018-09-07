package dk.medcom.video.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.repository.MeetingUserRepository;

@Component
public class MeetingUserService {

	@Autowired
	MeetingUserRepository meetingUserRepository;
	
	@Autowired
	UserContextService userService;
	

	public MeetingUser getOrCreateCurrentMeetingUser() {
		MeetingUser meetingUser = meetingUserRepository.findOneByOrganisationIdAndEmail(userService.getUserContext().getUserOrganisation(), userService.getUserContext().getUserEmail());
		if (meetingUser == null) {
			meetingUser = new MeetingUser();
			meetingUser.setEmail(userService.getUserContext().getUserEmail());
			meetingUser.setOrganisationId(userService.getUserContext().getUserOrganisation());
			meetingUser = meetingUserRepository.save(meetingUser);
		}
		return meetingUser;
	}

}
