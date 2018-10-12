package dk.medcom.video.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.SchedulingInfoController;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.UnauthorizedException;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.repository.OrganisationRepository;

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
				throw new PermissionDeniedException();
			}			
		}
		
		String userEmail = userService.getUserContext().getUserEmail();
		
		UserRole userRole = userService.getUserContext().getUserRole();
		if (userRole == UserRole.UNDEFINED || userRole == UserRole.UNAUTHORIZED) {
			throw new UnauthorizedException();
		}
				
		if (userRole != UserRole.PROVISIONER) {
			if ((userEmail == null ) || (userEmail.isEmpty()) || (userOrganisationId == null) || (userOrganisationId.isEmpty())) {
				userRole = UserRole.UNAUTHORIZED;
				throw new UnauthorizedException();
			}
		}
		
		LOGGER.info("Exit of preHandle method: Usermail: " + userEmail + " UserRole: " + userRole.ordinal() + " Organisation: " + organisation.getName());
		return true;
	}
}