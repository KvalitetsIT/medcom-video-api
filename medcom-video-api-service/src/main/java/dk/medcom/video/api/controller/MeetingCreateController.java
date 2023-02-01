package dk.medcom.video.api.controller;


import dk.medcom.video.api.PerformanceLogger;
import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.api.MeetingDto;
import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.service.MeetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class MeetingCreateController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MeetingCreateController.class);

	@Autowired
	private MeetingService meetingService;

	@Value("${short.link.base.url}")
	private String shortLinkBaseUrl;

	@APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
	@RequestMapping(value = "/meetings", method = RequestMethod.POST)
	public EntityModel <MeetingDto> createMeeting(@Valid @RequestBody CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotAcceptableException, NotValidDataException {
		LOGGER.debug("Entry of /meetings.post");

		var performanceLogger = new PerformanceLogger("create meeting");

		Meeting meeting = meetingService.createMeeting(createMeetingDto);
		LOGGER.info(meeting.getShortId());
		MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);
		EntityModel<MeetingDto> resource = EntityModel.of(meetingDto);

		performanceLogger.logTimeSinceCreation();

		LOGGER.debug("Exit of /meetings.post resource: " + resource);
		return resource;
	}
}
