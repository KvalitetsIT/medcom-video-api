package dk.medcom.video.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.UnauthorizedException;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.OrganisationRepository;

public class UserSecurityInterceptor extends HandlerInterceptorAdapter {
	
	private static Logger LOGGER = LoggerFactory.getLogger(UserSecurityInterceptor.class);
	
	@Autowired
	UserContextService userService;
	
	@Autowired
	OrganisationRepository organisationRepository;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LOGGER.debug("Entry of preHandle method");
		
		String userOrganisationId = userService.getUserContext().getUserOrganisation();
		Organisation organisation = null;
		if ((userOrganisationId != null) && (!userOrganisationId.isEmpty())) {
			organisation = organisationRepository.findByOrganisationId(userOrganisationId);
			if (organisation == null) {
				LOGGER.debug("organisation is not found using findByOrganisationId(userOrganisationId). userOrganisationId: " + userOrganisationId );
				throw new PermissionDeniedException();
			}			
		}
		
		String userEmail = userService.getUserContext().getUserEmail();
		
		if (userService.getUserContext().hasNoLegalRoles()) {
			LOGGER.debug("userRole is not valid or not set");
			throw new UnauthorizedException();
		}
				
		if (!userService.getUserContext().hasOnlyRole(UserRole.PROVISIONER)) {
			if ((userEmail == null ) || (userEmail.isEmpty()) || (userOrganisationId == null) || (userOrganisationId.isEmpty())) {
				LOGGER.debug("Email or user are not valid: userEmail: " + userEmail + ", userOrganisationId = " + userOrganisationId);
				throw new UnauthorizedException();
			}
		}

		String organisationId = null;
		if(organisation != null) {
			organisationId = organisation.getOrganisationId();
		}
		LOGGER.info("User information: organisation: {}, email: {}, roles: {}", organisationId, userEmail, userService.getUserContext().getUserRoles());

		LOGGER.debug("Exit of preHandle method: Usermail: " + userEmail + " UserRole: " + userService.getUserContext().getUserRoles() + " Organisation: " + organisation);
		return true;
	}
}