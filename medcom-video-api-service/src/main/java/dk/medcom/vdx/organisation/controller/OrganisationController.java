package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.api.OrganisationDto;
import dk.medcom.vdx.organisation.api.OrganisationGroupDto;
import dk.medcom.vdx.organisation.service.OrganisationNameService;
import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrganisationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationController.class);

	@Autowired
	private OrganisationNameService organisationService;

	@APISecurityAnnotation({ UserRole.ADMIN })
	@GetMapping(value = "/services/organisation/{code}")
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

	@APISecurityAnnotation({ UserRole.ADMIN })
	@GetMapping(value = "/services/organisation/uri/{uri}")
	public OrganisationGroupDto getOrganisationByUri(@PathVariable("uri") String uri) throws RessourceNotFoundException {
		LOGGER.debug("Entry of /services/organisation/uri.get code: " + uri);

		var organisation = organisationService.getOrganisationByUriWithDomain(uri);
		if (organisation == null){
			throw new RessourceNotFoundException("Organisation with meeting on URI", uri);
		}

		var response = new OrganisationGroupDto();
		response.setName(organisation.getName());
		response.setCode(organisation.getOrganisationId());
		response.setGroupId(organisation.getGroupId());
		int poolSize = organisation.getPoolSize() == null ? 0 : organisation.getPoolSize();
		response.setPoolSize(poolSize);

		return response;
	}
}
