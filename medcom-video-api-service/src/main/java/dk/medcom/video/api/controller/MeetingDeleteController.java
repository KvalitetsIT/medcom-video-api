package dk.medcom.video.api.controller;


import dk.medcom.video.api.api.MeetingDto;
import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.service.MeetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeetingDeleteController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MeetingDeleteController.class);

	@Autowired
	private MeetingService meetingService;

	@APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.DELETE)
	public EntityModel <MeetingDto> deleteMeeting(@PathVariable("uuid") String uuid) throws  RessourceNotFoundException, PermissionDeniedException, NotAcceptableException {
		LOGGER.debug("Entry of /meetings.delete uuid: " + uuid);
		
		meetingService.deleteMeeting(uuid);

		LOGGER.debug("Exit of /meetings.delete");
		return null;
	}
}
