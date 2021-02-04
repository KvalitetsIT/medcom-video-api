package dk.medcom.video.api.controller;

import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.api.CreateSchedulingInfoDto;
import dk.medcom.video.api.api.ProvisionStatus;
import dk.medcom.video.api.api.SchedulingInfoDto;
import dk.medcom.video.api.api.UpdateSchedulingInfoDto;
import dk.medcom.video.api.service.SchedulingInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SchedulingInfoController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingInfoController.class);
	
	private final SchedulingInfoService schedulingInfoService;
	private final String shortLinkBaseUrl;

	SchedulingInfoController(SchedulingInfoService schedulingInfoService, @Value("${short.link.base.url}") String shortLinkBaseUrl) {
		this.schedulingInfoService = schedulingInfoService;
		this.shortLinkBaseUrl = shortLinkBaseUrl;
	}

	@APISecurityAnnotation({UserRole.PROVISIONER_USER})
	@RequestMapping(value = "/scheduling-info", method = RequestMethod.POST)
	public EntityModel<SchedulingInfoDto> createSchedulingInfo(@Valid @RequestBody CreateSchedulingInfoDto createSchedulingInfoDto) throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
		LOGGER.debug("Entry of /scheduling-info.post.");

		SchedulingInfo schedulingInfo = schedulingInfoService.createSchedulingInfo(createSchedulingInfoDto);
		SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo, shortLinkBaseUrl);
		EntityModel <SchedulingInfoDto> resource = new EntityModel<>(schedulingInfoDto);

		LOGGER.debug("Exit of /scheduling-info.post resource: " + resource.toString());
		return resource;
	}

	@RequestMapping(value = "/scheduling-info", method = RequestMethod.GET)
	public CollectionModel <SchedulingInfoDto> getSchedulingInfo(
			@RequestParam(value = "from-start-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date fromStartTime, 
			@RequestParam(value = "to-end-time") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZZZ") Date toEndTime,
			@RequestParam(value = "provision-status", required = false, defaultValue = "AWAITS_PROVISION") ProvisionStatus provisionStatus) {
		LOGGER.debug("Entry of /scheduling-info.get fromStartTime: "+ fromStartTime.toString() + " toEndTime: " + toEndTime.toString() + " provision status: "+ provisionStatus.getValue()); 

		List<SchedulingInfo> schedulingInfos = schedulingInfoService.getSchedulingInfo(fromStartTime, toEndTime, provisionStatus);
		List<SchedulingInfoDto> schedulingInfoDtos = new LinkedList<>();
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo, shortLinkBaseUrl);
			schedulingInfoDtos.add(schedulingInfoDto);
		}
		CollectionModel<SchedulingInfoDto> resources = new CollectionModel<>(schedulingInfoDtos);
		
		Link selfRelLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfo(fromStartTime, toEndTime, provisionStatus)).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("Exit of /scheduling-info.get resources: " + resources.toString());
		return resources;
	}
	
	@RequestMapping(value = "/scheduling-info/{uuid}", method = RequestMethod.GET)
	public EntityModel <SchedulingInfoDto> getSchedulingInfoByUUID(@PathVariable("uuid") String uuid) throws RessourceNotFoundException {
		SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(uuid);
		SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo, shortLinkBaseUrl);

		return  new EntityModel<>(schedulingInfoDto);
	}

	@APISecurityAnnotation({UserRole.PROVISIONER_USER}) //full user context is required in order to update, because of updatedbyuser
	@RequestMapping(value = "/scheduling-info/{uuid}", method = RequestMethod.PUT)
	public EntityModel <SchedulingInfoDto> updateSchedulingInfo(@PathVariable("uuid") String uuid, @Valid @RequestBody UpdateSchedulingInfoDto updateSchedulingInfoDto ) throws RessourceNotFoundException, PermissionDeniedException {
		LOGGER.debug("Entry of /scheduling-info.put uuid: " + uuid);
				
		SchedulingInfo schedulingInfo = schedulingInfoService.updateSchedulingInfo(uuid, updateSchedulingInfoDto);
		SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo, shortLinkBaseUrl);
		EntityModel <SchedulingInfoDto> resource = new EntityModel<>(schedulingInfoDto);
		
		LOGGER.debug("Exit of /scheduling-info.put resource: " + resource.toString());
		return resource;
	}

	@APISecurityAnnotation({UserRole.PROVISIONER_USER})
	@RequestMapping(value = "/scheduling-info-provision", method = RequestMethod.GET)
	public CollectionModel<SchedulingInfoDto> getSchedulingInfoAwaitsProvision() {
		LOGGER.debug("Entry of /scheduling-info-provision.get");

		List<SchedulingInfo> schedulingInfos = schedulingInfoService.getSchedulingInfoAwaitsProvision();
		List<SchedulingInfoDto> schedulingInfoDtos = new LinkedList<>();
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo, shortLinkBaseUrl);
			schedulingInfoDtos.add(schedulingInfoDto);
		}
		CollectionModel<SchedulingInfoDto> resources = new CollectionModel<>(schedulingInfoDtos);

		Link selfRelLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoAwaitsProvision()).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("Exit of /scheduling-info-provision.get resources: " + resources.toString());
		return resources;
	}

	@APISecurityAnnotation({UserRole.PROVISIONER_USER})
	@RequestMapping(value = "/scheduling-info-deprovision", method = RequestMethod.GET)
	public CollectionModel<SchedulingInfoDto> getSchedulingInfoAwaitsDeProvision() {
		LOGGER.debug("Entry of /scheduling-info-deprovision.get");

		List<SchedulingInfo> schedulingInfos = schedulingInfoService.getSchedulingInfoAwaitsDeProvision();
		List<SchedulingInfoDto> schedulingInfoDtos = new LinkedList<>();
		for (SchedulingInfo schedulingInfo : schedulingInfos) {
			SchedulingInfoDto schedulingInfoDto = new SchedulingInfoDto(schedulingInfo, shortLinkBaseUrl);
			schedulingInfoDtos.add(schedulingInfoDto);
		}
		CollectionModel<SchedulingInfoDto> resources = new CollectionModel<>(schedulingInfoDtos);

		Link selfRelLink = linkTo(methodOn(SchedulingInfoController.class).getSchedulingInfoAwaitsDeProvision()).withSelfRel();
		resources.add(selfRelLink);

		LOGGER.debug("Exit of /scheduling-info-deprovision.get resources: " + resources.toString());
		return resources;
	}
}
