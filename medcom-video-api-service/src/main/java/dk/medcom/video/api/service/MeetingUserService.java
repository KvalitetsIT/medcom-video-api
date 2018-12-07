package dk.medcom.video.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
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

	public MeetingUser getOrCreateCurrentMeetingUser() throws PermissionDeniedException {
		return getOrCreateCurrentMeetingUser(userService.getUserContext().getUserEmail());
	}
	
	public MeetingUser getOrCreateCurrentMeetingUser(String email) throws PermissionDeniedException {
		MeetingUser meetingUser = meetingUserRepository.findOneByOrganisationAndEmail(organisationService.getUserOrganisation(), email);
		if (meetingUser == null) {
			meetingUser = new MeetingUser();
			meetingUser.setEmail(email);
			meetingUser.setOrganisation(organisationService.getUserOrganisation());
			meetingUser = meetingUserRepository.save(meetingUser);
		}
		return meetingUser;
	}

}
