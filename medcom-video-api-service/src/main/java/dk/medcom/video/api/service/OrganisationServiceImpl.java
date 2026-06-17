package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.organisation.OrganisationServiceClientV2;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrganisationServiceImpl implements OrganisationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationServiceImpl.class);
	private final UserContextService userService;
	private final OrganisationRepository organisationRepository;
	private final OrganisationStrategy organisationStrategy;
	private final OrganisationServiceClientV2 organisationServiceClientV2;

	public OrganisationServiceImpl(UserContextService userContextService,
	                               OrganisationRepository organisationRepository,
	                               OrganisationStrategy organisationStrategy,
	                               OrganisationServiceClientV2 organisationServiceClientV2) {
		this.organisationRepository = organisationRepository;
		this.organisationStrategy = organisationStrategy;
		this.userService = userContextService;
		this.organisationServiceClientV2 = organisationServiceClientV2;
	}

	@Override
	public Integer getPoolSizeForUserOrganisation() {
		return organisationStrategy.getPoolSizeForOrganisation(userService.getUserContext().getUserOrganisation());
	}

	@Override
	public Organisation getUserOrganisation() throws PermissionDeniedExceptionV2 {
		Organisation organisation = organisationRepository.findByOrganisationId(userService.getUserContext().getUserOrganisation());
		if (organisation == null) {
			LOGGER.debug("Organization was null");
			throw new PermissionDeniedExceptionV2();
		}
		return organisation;
	}

	@Override
	public Integer getPoolSizeForOrganisation(String organisationId) {
		return organisationStrategy.getPoolSizeForOrganisation(organisationId);
	}

	@Override
	public boolean userIsPermittedForOrganisation(String organisationId) {
		var userOrganisationCode = userService.getUserContext().getUserOrganisation();

		if (userOrganisationCode.equals(organisationId)) {
			return true;
		}

		return organisationServiceClientV2.getDescendantsOfOrganisation(userOrganisationCode).stream()
				.anyMatch(o -> o.code().equals(organisationId));
	}
}