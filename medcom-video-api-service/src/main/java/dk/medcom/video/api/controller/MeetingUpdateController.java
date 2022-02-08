package dk.medcom.video.api.controller;

import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.api.MeetingDto;
import dk.medcom.video.api.api.PatchMeetingDto;
import dk.medcom.video.api.api.UpdateMeetingDto;
import dk.medcom.video.api.mapper.MeetingMapper;
import dk.medcom.video.api.service.MeetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class MeetingUpdateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeetingUpdateController.class);

    private final MeetingService meetingService;
    private final String shortLinkBaseUrl;

    public MeetingUpdateController(MeetingService meetingService, @Value("${short.link.base.url}") String shortLinkBaseUrl) {
        this.meetingService = meetingService;
        this.shortLinkBaseUrl = shortLinkBaseUrl;
    }

    @APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
    @PutMapping(value = "/meetings/{uuid}")
    public EntityModel<MeetingDto> updateMeeting(@PathVariable("uuid") String uuid, @Valid @RequestBody UpdateMeetingDto updateMeetingDto ) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
        LOGGER.debug("Entry of /meetings.put uuid: " + uuid);

        Meeting meeting = meetingService.updateMeeting(uuid, updateMeetingDto);
        MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);
        EntityModel <MeetingDto> resource = new EntityModel<>(meetingDto);

        LOGGER.debug("Exit of /meetings.put resource: " + resource);
        return resource;
    }

    @APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
    @PatchMapping(value = "/meetings/{uuid}")
    public EntityModel<MeetingDto> patchMeeting(@PathVariable("uuid") UUID uuid, @Valid @RequestBody PatchMeetingDto patchMeetingDto) throws NotAcceptableException, PermissionDeniedException, RessourceNotFoundException, NotValidDataException {
        LOGGER.debug("Entry of /meetings.patch uuid: {}", uuid);

        var response = meetingService.patchMeeting(uuid, patchMeetingDto);

        var entityModel = new EntityModel<>(new MeetingMapper().mapFromMeeting(response, shortLinkBaseUrl));

        LOGGER.debug("Exit of /meetings.patch ressource: {}", entityModel);
        return entityModel;
    }
}
