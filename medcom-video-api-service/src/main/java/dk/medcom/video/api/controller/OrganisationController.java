package dk.medcom.video.api.controller;

import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.api.OrganisationDto;
import dk.medcom.video.api.service.OrganisationNameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrganisationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationController.class);

	@Autowired
	private OrganisationNameService organisationService;

	@APISecurityAnnotation({ UserRole.ADMIN })
	@RequestMapping(value = "/services/organisation/{code}", method = RequestMethod.GET)
	public OrganisationDto getOrganisation(@PathVariable("code") String code) throws RessourceNotFoundException {
		LOGGER.debug("Entry of /services/organisation.get code: " + code);

		var optionalOrganisation = organisationService.getOrganisationById(code);
		var organisation = optionalOrganisation.orElseThrow(() -> new RessourceNotFoundException("OrganisationId", code));

		var response = new OrganisationDto();
		response.setName(organisation.getName());
		response.setCode(organisation.getOrganisationId());
		int poolSize = organisation.getPoolSize() == null ? 0 : organisation.getPoolSize();
		response.setPoolSize(poolSize);

		return response;
	}
}
