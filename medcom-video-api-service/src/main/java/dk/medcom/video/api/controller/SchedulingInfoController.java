package dk.medcom.video.api.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

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

import org.slf4j.Logger;
import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.SchedulingInfoDto;
import dk.medcom.video.api.dto.UpdateSchedulingInfoDto;
import dk.medcom.video.api.service.SchedulingInfoService;

@RestController
public class SchedulingInfoController {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoController.class);
	
	@Autowired
	SchedulingInfoService schedulingInfoService;
	
	@RequestMapping(value = "/scheduling-info", method = RequestMethod.GET)
	public Resources <SchedulingInfoDto> getSchedulingInfo(
			@RequestParam(value = "from-start-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date fromStartTime, 
			@RequestParam(value = "to-end-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date toEndTime,
			@RequestParam(value = "provision-status", required = false, defaultValue = "AWAITS_PROVISION") ProvisionStatus provisionStatus) {
		LOGGER.debug("Entry of /scheduling-info.get fromStartTime: "+ fromStartTime.toString() + " toEndTime: " + toEndTime.toString() + " provision status: "+ provisionStatus.getValue()); 

		List<SchedulingInfo> schedulingInfos = schedulingInfoService.getSchedulingInfo(fromStartTime, toEndTime, provisionStatus);
		List<SchedulingInfoDto> schedulingInfoDtos = new LinkedList<SchedulingInfoDto>();
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo);
			schedulingInfoDtos.add(schedulingInfoDto);
		}
		Resources<SchedulingInfoDto> resources = new Resources<>(schedulingInfoDtos);
		
		Link selfRelLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfo(fromStartTime, toEndTime, provisionStatus)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("Exit of /scheduling-info.get resources: " + resources.toString());
		return resources;
	}
	
	@RequestMapping(value = "/scheduling-info/{uuid}", method = RequestMethod.GET)
	public Resource <SchedulingInfoDto> getSchedulingInfoByUUID(@PathVariable("uuid") String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(uuid);
		SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo);
		Resource <SchedulingInfoDto> resource = new Resource <SchedulingInfoDto>(schedulingInfoDto);
		
		return resource;
	}

	@APISecurityAnnotation({UserRole.PROVISIONER_USER}) //full user context is required in order to update, because of updatedbyuser
	@RequestMapping(value = "/scheduling-info/{uuid}", method = RequestMethod.PUT)
	public Resource <SchedulingInfoDto> updateSchedulingInfo(@PathVariable("uuid") String uuid, @Valid @RequestBody UpdateSchedulingInfoDto updateSchedulingInfoDto ) throws RessourceNotFoundException, PermissionDeniedException, NotValidDataException {
		LOGGER.debug("Entry of /scheduling-info.put uuid: " + uuid);
				
		SchedulingInfo schedulingInfo = schedulingInfoService.updateSchedulingInfo(uuid, updateSchedulingInfoDto);
		SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo);
		Resource <SchedulingInfoDto> resource = new Resource <SchedulingInfoDto>(schedulingInfoDto);
		
		LOGGER.debug("Exit of /scheduling-info.put resource: " + resource.toString());
		return resource;
	}

}
