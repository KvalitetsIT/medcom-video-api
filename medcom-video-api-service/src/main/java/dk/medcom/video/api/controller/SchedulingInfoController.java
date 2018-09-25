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

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.ProvisionStatusParmConverter;
import dk.medcom.video.api.dto.SchedulingInfoDto;
import dk.medcom.video.api.dto.UpdateSchedulingInfoDto;
import dk.medcom.video.api.service.SchedulingInfoService;

@RestController
public class SchedulingInfoController {

	@Autowired
	SchedulingInfoService schedulingInfoService;
	
	@RequestMapping(value = "/scheduling-info", method = RequestMethod.GET)
	public Resources <SchedulingInfoDto> getSchedulingInfo(
			@RequestParam(value = "from-start-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") Date fromStartTime, 
			@RequestParam(value = "to-end-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") Date toEndTime,
			@RequestParam(value = "provision-status", required = false, defaultValue = "0") ProvisionStatus provisionStatus) {
		//TODO: error reporting on invalid provisionStatus as input parm must be improved
		
		List<SchedulingInfo> schedulingInfos = schedulingInfoService.getSchedulingInfo(fromStartTime, toEndTime, provisionStatus);
		List<SchedulingInfoDto> schedulingInfoDtos = new LinkedList<SchedulingInfoDto>();
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo);
			schedulingInfoDtos.add(schedulingInfoDto);
		}
		Resources<SchedulingInfoDto> resources = new Resources<>(schedulingInfoDtos);
		
		//TODO: links are returning the ENUM variable and not the integer value
		Link selfRelLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfo(fromStartTime, toEndTime, provisionStatus)).withSelfRel();
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

	@RequestMapping(value = "/scheduling-info/{uuid}", method = RequestMethod.PUT)
	public Resource <SchedulingInfoDto> schedulingInfo(@PathVariable("uuid") String uuid, @Valid @RequestBody UpdateSchedulingInfoDto updateSchedulingInfoDto ) throws RessourceNotFoundException, PermissionDeniedException {
		//TODO: error reporting on invalid provisionStatus in request body must be improved
		SchedulingInfo schedulingInfo = schedulingInfoService.updateSchedulingInfo(uuid, updateSchedulingInfoDto);
		SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo);
		Resource <SchedulingInfoDto> resource = new Resource <SchedulingInfoDto>(schedulingInfoDto);
		
		return resource;
	}
	
	@InitBinder
	public void initBinder(final WebDataBinder webdataBinder) {  //Handles correct input of @RequestParam
		webdataBinder.registerCustomEditor(ProvisionStatus.class, new ProvisionStatusParmConverter());
	}


}
