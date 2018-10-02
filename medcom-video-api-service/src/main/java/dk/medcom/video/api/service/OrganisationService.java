package dk.medcom.video.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.repository.OrganisationRepository;


@Component
public class OrganisationService {
	
	@Autowired
	UserContextService userService;
	
	@Autowired
	OrganisationRepository organisationRepository;

	public Organisation getUserOrganisation() throws PermissionDeniedException {
		
		Organisation organisation = organisationRepository.findByOrganisationId(userService.getUserContext().getUserOrganisation());
		if (organisation == null) {
			throw new PermissionDeniedException();
		}			
		return organisation;
	}
	
}
