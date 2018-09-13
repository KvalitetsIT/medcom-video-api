//TODO Lene: overvej om Resources, Resource, MeetingDto etc kan gøres mere simpelt. Er alle lag nødvendige?
package dk.medcom.video.api.controller;

import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.MeetingDto;
import dk.medcom.video.api.service.MeetingService;

@RestController
public class MeetingController {

	@Autowired
	MeetingService meetingService;
	
	@RequestMapping(value = "/meetings", method = RequestMethod.GET)
	//public MeetingDto[] getMeetings() {
	public Resources <MeetingDto> getMeetings() {
		List<Meeting> meetings = meetingService.getMeetings();
		
		List<MeetingDto> meetingDtos = new LinkedList<MeetingDto>();
		for (Meeting meeting : meetings) {
			MeetingDto meetingDto = new MeetingDto(meeting);
			meetingDtos.add(meetingDto);
		}
		Resources<MeetingDto> resources = new Resources<>(meetingDtos);
		
		//Link selfRelLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MeetingController.class).getMeetings()).withSelfRel();
		Link selfRelLink = linkTo(methodOn(MeetingController.class).getMeetings()).withSelfRel();
		resources.add(selfRelLink);

		return resources;
		//return meetingDtos.toArray(new MeetingDto[meetingDtos.size()]);
	}
	
//TODO Lene: det burde være meetings her på get og på post jf webside 	
	@RequestMapping(value = "/meeting/{uuid}", method = RequestMethod.GET)
	//public MeetingDto getMeetingByUUID(@PathVariable("uuid") String uuid) throws RessourceNotFoundException, PermissionDeniedException {
	public Resource <MeetingDto> getMeetingByUUID(@PathVariable("uuid") String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		Meeting meeting = meetingService.getMeetingByUuid(uuid);
		MeetingDto meetingDto = new MeetingDto(meeting);
		Resource <MeetingDto> resource = new Resource <MeetingDto>(meetingDto);
		
		return resource;
		//return meetingDto;
	}

	@RequestMapping(value = "/meeting", method = RequestMethod.POST)
	//public MeetingDto createMeeting(@Valid @RequestBody CreateMeetingDto createMeetingDto) throws RessourceNotFoundException {
	public Resource <MeetingDto> createMeeting(@Valid @RequestBody CreateMeetingDto createMeetingDto) throws RessourceNotFoundException {
		Meeting meeting = meetingService.createMeeting(createMeetingDto);
		MeetingDto meetingDto = new MeetingDto(meeting);
		Resource <MeetingDto> resource = new Resource <MeetingDto>(meetingDto);
		return resource;
		//return meetingDto;
	}

}
