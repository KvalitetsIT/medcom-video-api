package dk.medcom.video.api.controller;

import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.service.PoolInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class PoolController {
	private static final Logger logger = LoggerFactory.getLogger(PoolController.class);

	private final PoolInfoService poolInfoService;
	private final List<String> provisionExcludeOrganisations;

	public PoolController(PoolInfoService poolInfoService, @Value("${event.organisation.filter:#{null}}") List<String> provisionExcludeOrganisations) {
		this.poolInfoService = poolInfoService;
		this.provisionExcludeOrganisations = Objects.requireNonNullElse(provisionExcludeOrganisations, Collections.emptyList());
	}

	@RequestMapping(value = "/pool", method = RequestMethod.GET)
	public CollectionModel<PoolInfoDto> getPoolInfo() {
		logger.debug("Entering /pool");

		List<PoolInfoDto> response = poolInfoService.getPoolInfo().stream().filter(x -> !provisionExcludeOrganisations.isEmpty() && !provisionExcludeOrganisations.contains(x.getOrganizationId())).collect(Collectors.toList());
		CollectionModel<PoolInfoDto> resources = new CollectionModel<>(response);

		Link selfRelLink = linkTo(methodOn(PoolController.class).getPoolInfo()).withSelfRel();
		resources.add(selfRelLink);

		logger.debug("Finishing /pool request.");
		return resources;
	}
}
