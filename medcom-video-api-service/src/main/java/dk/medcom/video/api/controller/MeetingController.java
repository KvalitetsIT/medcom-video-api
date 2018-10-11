package dk.medcom.video.api.controller;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

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
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.MeetingDto;
import dk.medcom.video.api.dto.SchedulingInfoDto;
import dk.medcom.video.api.dto.UpdateMeetingDto;
import dk.medcom.video.api.dto.UpdateSchedulingInfoDto;
import dk.medcom.video.api.service.MeetingService;

@RestController
public class MeetingController {

	@Autowired
	MeetingService meetingService;
	
	@RequestMapping(value = "/meetings", method = RequestMethod.GET)
	public Resources <MeetingDto> getMeetings( 
			@RequestParam(value = "from-start-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date fromStartTime,
			@RequestParam(value = "to-start-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date toStartTime) throws PermissionDeniedException {
		
		List<Meeting> meetings = meetingService.getMeetings(fromStartTime, toStartTime);
		List<MeetingDto> meetingDtos = new LinkedList<MeetingDto>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting);
			
			try { 
				Link schedulingInfoLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoByUUID(meeting.getUuid())).withRel("scheduling-info");
				meetingDto.add(schedulingInfoLink);
			} catch (RessourceNotFoundException | PermissionDeniedException e) {
			}

			
			meetingDtos.add(meetingDto);
		}
		Resources<MeetingDto> resources = new Resources<>(meetingDtos);
		
		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetings(fromStartTime, toStartTime)).withSelfRel();
		resources.add(selfRelLink);

		return resources;
	}
	
	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.GET)
	public Resource <MeetingDto> getMeetingByUUID(@PathVariable("uuid") String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		Meeting meeting = meetingService.getMeetingByUuid(uuid);
		MeetingDto meetingDto = new MeetingDto(meeting);
		Resource <MeetingDto> resource = new Resource <MeetingDto>(meetingDto);
		
		return resource;
	}

	@RequestMapping(value = "/meetings", method = RequestMethod.POST)
	public Resource <MeetingDto> createMeeting(@Valid @RequestBody CreateMeetingDto createMeetingDto) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException {
		Meeting meeting = meetingService.createMeeting(createMeetingDto);
		MeetingDto meetingDto = new MeetingDto(meeting);
		Resource <MeetingDto> resource = new Resource <MeetingDto>(meetingDto);
		return resource;
	}
	
	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.PUT)
	public Resource <MeetingDto> updateMeeting(@PathVariable("uuid") String uuid, @Valid @RequestBody UpdateMeetingDto updateMeetingDto ) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException {
		Meeting meeting = meetingService.updateMeeting(uuid, updateMeetingDto);
		MeetingDto meetingDto = new MeetingDto(meeting);
		Resource <MeetingDto> resource = new Resource <MeetingDto>(meetingDto);
		
		return resource;
	}
	@RequestMapping(value = "/meetings/{uuid}", method = RequestMethod.DELETE)
	public Resource <MeetingDto> deleteMeeting(@PathVariable("uuid") String uuid) throws  RessourceNotFoundException, PermissionDeniedException, NotAcceptableException {
		meetingService.deleteMeeting(uuid);

		return null;
	}

}
