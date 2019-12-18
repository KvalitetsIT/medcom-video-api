package dk.medcom.video.api.controller;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.MeetingDto;
import dk.medcom.video.api.dto.UpdateMeetingDto;
import dk.medcom.video.api.service.MeetingService;

@RestController
public class MeetingController {
	private static Logger LOGGER = LoggerFactory.getLogger(MeetingController.class);

	@Autowired
	MeetingService meetingService;
	
	@RequestMapping(value = "/meetings", method = RequestMethod.GET)
	public Resources <MeetingDto> getMeetings( 
			@RequestParam(value = "from-start-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date fromStartTime,
			@RequestParam(value = "to-start-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date toStartTime) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Entry of /meetings.get fromStartTime: "+ fromStartTime.toString() + " toStartTime: " + toStartTime.toString());
		
		List<Meeting> meetings = meetingService.getMeetings(fromStartTime, toStartTime);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		Resources<MeetingDto> resources = new Resources<>(meetingDtos);
		
		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetings(fromStartTime, toStartTime)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("Exit of /meetings.get resources: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params = "subject")
	public Resources <MeetingDto> getMeetings(String subject) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Getting meetings by subject: " + subject);

		List<Meeting> meetings = meetingService.getMeetingsBySubject(subject);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		Resources<MeetingDto> resources = new Resources<>(meetingDtos);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetings(subject)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("end og get meeting by subject: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params = "organizedBy")
	public Resources <MeetingDto> getMeetingsOrganizedBy(String organizedBy) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Getting meetings by organized by: " + organizedBy);

		List<Meeting> meetings = meetingService.getMeetingsByOrganizedBy(organizedBy);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		Resources<MeetingDto> resources = new Resources<>(meetingDtos);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetingsOrganizedBy(organizedBy)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("end og get meeting by organized by: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params = "uriWithDomain")
	public Resources <MeetingDto> getMeetingsUriWithDomain(String uriWithDomain) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Getting meetings by uri with domain: " + uriWithDomain);

		List<Meeting> meetings = meetingService.getMeetingsByUriWithDomain(uriWithDomain);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		Resources<MeetingDto> resources = new Resources<>(meetingDtos);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetingsUriWithDomain(uriWithDomain)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("end og get meeting by uri with domain: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.GET, params = "label")
	public Resources <MeetingDto> getMeetingsByLabel(String label) throws PermissionDeniedException, RessourceNotFoundException {
		LOGGER.debug("Getting meetings by label with label: " + label);

		List<Meeting> meetings = meetingService.getMeetingsByLabel(label);
		List<MeetingDto> meetingDtos = new LinkedList<>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting);

			Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
			meetingDto.add(schedulingInfoLink);
			meetingDtos.add(meetingDto);
		}
		Resources<MeetingDto> resources = new Resources<>(meetingDtos);

		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetingsByLabel(label)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("end og get meeting by label with label: " + resources.toString());
		return resources;
	}

	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.GET)
	public Resource <MeetingDto> getMeetingByUUID(@PathVariable("uuid") String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		LOGGER.debug("Entry of /meetings.get uuid: " + uuid);
		
		Meeting meeting = meetingService.getMeetingByUuid(uuid);
		MeetingDto meetingDto = new MeetingDto(meeting);
		Resource <MeetingDto> resource = new Resource<>(meetingDto);
		
		LOGGER.debug("Exit of /meetings.get resource: " + resource);
		return resource;
	}
	@APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
	@RequestMapping(value = "/meetings", method = RequestMethod.POST)
	public Resource <MeetingDto> createMeeting(@Valid @RequestBody CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotAcceptableException, NotValidDataException {
		LOGGER.debug("Entry of /meetings.post");
		
		Meeting meeting = meetingService.createMeeting(createMeetingDto);
		MeetingDto meetingDto = new MeetingDto(meeting);
		Resource<MeetingDto> resource = new Resource<>(meetingDto);
		
		LOGGER.debug("Exit of /meetings.post resource: " + resource);
		return resource;
	}
	
	@APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.PUT)
	public Resource <MeetingDto> updateMeeting(@PathVariable("uuid") String uuid, @Valid @RequestBody UpdateMeetingDto updateMeetingDto ) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		LOGGER.debug("Entry of /meetings.put uuid: " + uuid);
		
		Meeting meeting = meetingService.updateMeeting(uuid, updateMeetingDto);
		MeetingDto meetingDto = new MeetingDto(meeting);
		Resource <MeetingDto> resource = new Resource<>(meetingDto);
		
		LOGGER.debug("Exit of /meetings.put resource: " + resource);
		return resource;
	}
	
	@APISecurityAnnotation({UserRole.MEETING_PLANNER, UserRole.PROVISIONER_USER, UserRole.ADMIN, UserRole.USER})
	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.DELETE)
	public Resource <MeetingDto> deleteMeeting(@PathVariable("uuid") String uuid) throws  RessourceNotFoundException, PermissionDeniedException, NotAcceptableException {
		LOGGER.debug("Entry of /meetings.delete uuid: " + uuid);
		
		meetingService.deleteMeeting(uuid);

		LOGGER.debug("Exit of /meetings.delete");
		return null;
	}

}
