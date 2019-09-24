package dk.medcom.video.api.controller;

import dk.medcom.video.api.dto.PoolInfoDto;
import dk.medcom.video.api.service.PoolInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class PoolController {
	private static Logger logger = LoggerFactory.getLogger(PoolController.class);

	private PoolInfoService poolInfoService;

	public PoolController(PoolInfoService poolInfoService) {
		this.poolInfoService = poolInfoService;
	}

	@RequestMapping(value = "/pool", method = RequestMethod.GET)
	public Resources<PoolInfoDto> getPoolInfo() {
//		logger.debug("Entry of /scheduling-info.get fromStartTime: "+ fromStartTime.toString() + " toEndTime: " + toEndTime.toString() + " provision status: "+ provisionStatus.getValue());
		logger.debug("Entering /pool");

		List<PoolInfoDto> response = poolInfoService.getPoolInfo();
		Resources<PoolInfoDto> resources = new Resources<>(response);

		Link selfRelLink = linkTo(methodOn(PoolController.class).getPoolInfo()).withSelfRel();
		resources.add(selfRelLink);

		logger.debug("Finishing /pool request.");
//		logger.debug("Exit of /scheduling-info.get resources: " + resources.toString());
		return resources;
	}
}
