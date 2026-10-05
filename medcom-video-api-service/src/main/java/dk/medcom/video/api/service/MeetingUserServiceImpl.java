package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeetingUserServiceImpl implements MeetingUserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MeetingUserServiceImpl.class);

	private final MeetingUserRepository meetingUserRepository;
	
	private final UserContextService userService;
	
	private final OrganisationService organisationService;

	public MeetingUserServiceImpl(MeetingUserRepository meetingUserRepository, UserContextService userService, OrganisationService organisationService) {
		this.meetingUserRepository = meetingUserRepository;
		this.userService = userService;
		this.organisationService = organisationService;
	}

	@Override
	public MeetingUser getOrCreateCurrentMeetingUser() throws PermissionDeniedExceptionV2 {
		return getOrCreateCurrentMeetingUser(userService.getUserContext().getUserEmail());
	}

	@Override
	public MeetingUser getOrCreateCurrentMeetingUser(String email) throws PermissionDeniedExceptionV2 {
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
