package dk.medcom.video.api.service;

import dk.medcom.video.api.organisation.OrganisationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.repository.OrganisationRepository;


@Component
public class OrganisationService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(OrganisationService.class);
	
	@Autowired
	UserContextService userService;
	
	@Autowired
	OrganisationRepository organisationRepository;

	@Autowired
	private OrganisationStrategy organisationStrategy;

	public Integer getPoolSizeForUserOrganisation() {
		return organisationStrategy.getPoolSizeForOrganisation(userService.getUserContext().getUserOrganisation());
	}

	public Organisation getUserOrganisation() throws PermissionDeniedException {
		Organisation organisation = organisationRepository.findByOrganisationId(userService.getUserContext().getUserOrganisation());
		if (organisation == null) {
			LOGGER.debug("Organization was null");
			throw new PermissionDeniedException();
		}			
		return organisation;
	}

	public Integer getPoolSizeForOrganisation(String organisationId) {
		return organisationStrategy.getPoolSizeForOrganisation(organisationId);
	}
}
