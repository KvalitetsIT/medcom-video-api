package dk.medcom.video.api.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.SchedulingInfoDto;
import dk.medcom.video.api.service.SchedulingInfoService;

@RestController
public class SchedulingInfoController {

	@Autowired
	SchedulingInfoService schedulingInfoService;
	
	@RequestMapping(value = "/scheduling-info", method = RequestMethod.GET)
	//public MeetingDto[] getMeetings() {
	public Resources <SchedulingInfoDto> getSchedulingInfo() {
		List<SchedulingInfo> schedulingInfos = schedulingInfoService.getSchedulingInfo();
		
		List<SchedulingInfoDto> schedulingInfoDtos = new LinkedList<SchedulingInfoDto>();
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo);
			schedulingInfoDtos.add(schedulingInfoDto);
		}
		Resources<SchedulingInfoDto> resources = new Resources<>(schedulingInfoDtos);
		
		Link selfRelLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfo()).withSelfRel();
		resources.add(selfRelLink);

		return resources;
	}
	
	@RequestMapping(value = "/scheduling-info/{uuid}", method = RequestMethod.GET)
	public Resource <SchedulingInfoDto> getSchedulingInfoByUUID(@PathVariable("uuid") String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(uuid);
		SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo);
		Resource <SchedulingInfoDto> resource = new Resource <SchedulingInfoDto>(schedulingInfoDto);
		
		return resource;

	}

}
