package dk.medcom.video.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.MeetingUserRepository;

@Component
public class MeetingUserService {
	private static Logger LOGGER = LoggerFactory.getLogger(MeetingUserService.class);

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
		LOGGER.debug("Entry getOrCreateCurrentMeetingUser email is required and is: " + email);
		LOGGER.debug("Entry getOrCreateCurrentMeetingUser organisation is required  and is: " + organisationService.getUserOrganisation());
		MeetingUser meetingUser = meetingUserRepository.findOneByOrganisationAndEmail(organisationService.getUserOrganisation(), email);
		if (meetingUser == null) {
			LOGGER.debug("Creating meeting user");
			meetingUser = new MeetingUser();
			meetingUser.setEmail(email);
			meetingUser.setOrganisation(organisationService.getUserOrganisation());
			meetingUser = meetingUserRepository.save(meetingUser);
		}
		return meetingUser;
	}

}
