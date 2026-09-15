package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.organisation.model.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrganisationServiceImpl implements OrganisationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationServiceImpl.class);

	private final UserContextService userService;
	private final OrganisationStrategy organisationStrategy;

	public OrganisationServiceImpl(UserContextService userContextService,
								   OrganisationStrategy organisationStrategy) {
		this.organisationStrategy = organisationStrategy;
		this.userService = userContextService;
	}

	@Override
	public Organisation getUserOrganisation() throws PermissionDeniedException {
		Organisation organisation = organisationStrategy.findOrganisationByCode(userService.getUserContext().getUserOrganisation());
		if (organisation == null) {
			LOGGER.debug("Organization was null");
			throw new PermissionDeniedException();
		}
		return organisation;
	}

	@Override
	public Integer getPoolSizeForOrganisation(String organisationId) {
		return organisationStrategy.getPoolSizeForOrganisation(organisationId);
	}

	@Override
	public Integer getPoolSizeForUserOrganisation() {
		return organisationStrategy.getPoolSizeForOrganisation(userService.getUserContext().getUserOrganisation());
	}
}
