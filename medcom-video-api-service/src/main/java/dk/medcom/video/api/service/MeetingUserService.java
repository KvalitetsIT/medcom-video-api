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
	
	@Autowired
	OrganisationService organisationService;

	public MeetingUser getOrCreateCurrentMeetingUser() {
		MeetingUser meetingUser = meetingUserRepository.findOneByOrganisationAndEmail(organisationService.getUserOrganisation(), userService.getUserContext().getUserEmail());
		if (meetingUser == null) {
			meetingUser = new MeetingUser();
			meetingUser.setEmail(userService.getUserContext().getUserEmail());
			meetingUser.setOrganisation(organisationService.getUserOrganisation());
			meetingUser = meetingUserRepository.save(meetingUser);
		}
		return meetingUser;
	}

}
