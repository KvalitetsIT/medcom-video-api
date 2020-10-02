package dk.medcom.video.api.controller;


import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.*;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.MeetingDto;
import dk.medcom.video.api.dto.UpdateMeetingDto;
import dk.medcom.video.api.service.MeetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class MeetingController {
	private static Logger LOGGER = LoggerFactory.getLogger(MeetingController.class);

	@Autowired
	MeetingService meetingService;

	@Value("${short.link.base.url}")
	private String shortLinkBaseUrl;
	
	@RequestMapping(value = "/meetings", method = RequestMethod.GET)
	public CollectionModel <MeetingDto> getMeetings( 
			@RequestParam(value = "from-start-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date fromStartTime,
			@RequestParam(value = "to-start-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date toStartTime) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Entry of /meetings.get fromStartTime: "+ fromStartTime.toString() + " toStartTime: " + toStartTime.toString());
		
		List<Meeting> meetings = meetingService.getMeetings(fromStartTime, toStartTime);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		CollectionModel<MeetingDto> resources = new CollectionModel<>(meetingDtos);
		
		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetings(fromStartTime, toStartTime)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("Exit of /meetings.get resources: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params = "short-id")
	public EntityModel <MeetingDto> getMeetingByShortId(@RequestParam(value = "short-id")String shortId) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Entry of /meetings.get shortId: "+ shortId);

		Meeting meeting = meetingService.getMeetingByShortId(shortId);

		MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);

		Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
		meetingDto.add(schedulingInfoLink);
		EntityModel<MeetingDto> resources = new EntityModel<>(meetingDto);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetingByShortId(shortId)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("Exit of /meetings.get resources: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params = "subject")
	public CollectionModel <MeetingDto> getMeetings(String subject) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Getting meetings by subject: " + subject);

		List<Meeting> meetings = meetingService.getMeetingsBySubject(subject);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		CollectionModel<MeetingDto> resources = new CollectionModel<>(meetingDtos);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetings(subject)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("end og get meeting by subject: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params = "organizedBy")
	public CollectionModel <MeetingDto> getMeetingsOrganizedBy(String organizedBy) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Getting meetings by organized by: " + organizedBy);

		List<Meeting> meetings = meetingService.getMeetingsByOrganizedBy(organizedBy);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		CollectionModel<MeetingDto> resources = new CollectionModel<>(meetingDtos);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetingsOrganizedBy(organizedBy)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("end og get meeting by organized by: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params = "uriWithDomain")
	public CollectionModel <MeetingDto> getMeetingsUriWithDomain(String uriWithDomain) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Getting meetings by uri with domain: " + uriWithDomain);

		List<Meeting> meetings = meetingService.getMeetingsByUriWithDomain(uriWithDomain);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		CollectionModel<MeetingDto> resources = new CollectionModel<>(meetingDtos);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetingsUriWithDomain(uriWithDomain)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("end og get meeting by uri with domain: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params="search")
	public CollectionModel<MeetingDto> genericSearchMeetings(@RequestParam(name ="search") String search,
													   @RequestParam(name="from-start-time", required = false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ") Date fromStartTime,
													   @RequestParam(name = "to-start-time", required = false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ") Date toStartTime) throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException {
		LOGGER.debug(String.format("Searching for meetings. Search: %s, fromStartTime: %s, toStartTime: %s.", search, fromStartTime, toStartTime));

		if((fromStartTime != null && toStartTime == null) || (fromStartTime == null && toStartTime != null)) {
			throw new NotValidDataException(NotValidDataErrors.BOTH_FROM_AND_TO_START_TIME_MUST_BE_PROVIDED_OR_NONE);
		}

		List<Meeting> meetings = meetingService.searchMeetings(search, fromStartTime, toStartTime);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		CollectionModel<MeetingDto> resources = new CollectionModel<>(meetingDtos);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).genericSearchMeetings(search, fromStartTime, toStartTime)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("End generic search meetings: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params = "label")
	public CollectionModel <MeetingDto> getMeetingsByLabel(String label) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Getting meetings by label with label: " + label);

		List<Meeting> meetings = meetingService.getMeetingsByLabel(label);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		CollectionModel<MeetingDto> resources = new CollectionModel<>(meetingDtos);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetingsByLabel(label)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("end og get meeting by label with label: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.GET)
	public EntityModel <MeetingDto> getMeetingByUUID(@PathVariable("uuid") String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		LOGGER.debug("Entry of /meetings.get uuid: " + uuid);
		
		Meeting meeting = meetingService.getMeetingByUuid(uuid);
		MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);
		EntityModel <MeetingDto> resource = new EntityModel<>(meetingDto);
		
		LOGGER.debug("Exit of /meetings.get resource: " + resource);
		return resource;
	}
	@APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
	@RequestMapping(value = "/meetings", method = RequestMethod.POST)
	public EntityModel <MeetingDto> createMeeting(@Valid @RequestBody CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotAcceptableException, NotValidDataException {
		LOGGER.debug("Entry of /meetings.post");
		
		Meeting meeting = meetingService.createMeeting(createMeetingDto);
		LOGGER.info(meeting.getShortId());
		MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);
		EntityModel<MeetingDto> resource = new EntityModel<>(meetingDto);
		
		LOGGER.debug("Exit of /meetings.post resource: " + resource);
		return resource;
	}
	
	@APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.PUT)
	public EntityModel <MeetingDto> updateMeeting(@PathVariable("uuid") String uuid, @Valid @RequestBody UpdateMeetingDto updateMeetingDto ) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		LOGGER.debug("Entry of /meetings.put uuid: " + uuid);
		
		Meeting meeting = meetingService.updateMeeting(uuid, updateMeetingDto);
		MeetingDto meetingDto = new MeetingDto(meeting, shortLinkBaseUrl);
		EntityModel <MeetingDto> resource = new EntityModel<>(meetingDto);
		
		LOGGER.debug("Exit of /meetings.put resource: " + resource);
		return resource;
	}
	
	@APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.DELETE)
	public EntityModel <MeetingDto> deleteMeeting(@PathVariable("uuid") String uuid) throws  RessourceNotFoundException, PermissionDeniedException, NotAcceptableException {
		LOGGER.debug("Entry of /meetings.delete uuid: " + uuid);
		
		meetingService.deleteMeeting(uuid);

		LOGGER.debug("Exit of /meetings.delete");
		return null;
	}

}
