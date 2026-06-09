package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import dk.medcom.video.api.organisation.model.OrganisationTree;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrganisationServiceImpl implements OrganisationService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationServiceImpl.class);

	private final UserContextService userService;
	private final OrganisationRepository organisationRepository;
	private final OrganisationStrategy organisationStrategy;
	private final OrganisationTreeServiceClient organisationTreeServiceClient;

	public OrganisationServiceImpl(UserContextService userContextService,
								   OrganisationRepository organisationRepository,
								   OrganisationStrategy organisationStrategy, OrganisationTreeServiceClient organisationTreeServiceClient) {
		this.organisationRepository = organisationRepository;
		this.organisationStrategy = organisationStrategy;
		this.userService = userContextService;
		this.organisationTreeServiceClient = organisationTreeServiceClient;
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
	public boolean userIsPermittedForOrganisation(String organisationId){
		var userOrganisationCode = userService.getUserContext().getUserOrganisation();
		var tree = organisationTreeServiceClient.getOrganisationTree(userOrganisationCode);
		if (tree == null) {
			return false;
		}
		var userNode = findNode(userOrganisationCode, tree);
		return userNode != null && subtreeContains(userNode, organisationId);
	}

	private OrganisationTree findNode(String code, OrganisationTree node) {
		if (code.equals(node.getCode())) {
			return node;
		}
		for (var child : node.getChildren()) {
			var found = findNode(code, child);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	private boolean subtreeContains(OrganisationTree node, String code) {
		if (code.equals(node.getCode())) {
			return true;
		}
		for (var child : node.getChildren()) {
			if (subtreeContains(child, code)) {
				return true;
			}
		}
		return false;
	}

}
