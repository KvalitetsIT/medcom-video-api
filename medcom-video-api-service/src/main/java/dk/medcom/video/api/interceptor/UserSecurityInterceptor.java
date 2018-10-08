package dk.medcom.video.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.UnauthorizedException;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.repository.OrganisationRepository;

public class UserSecurityInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	UserContextService userService;
	
	@Autowired
	OrganisationRepository organisationRepository;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		//TODO: kan man gemme dem til senere?
		String userOrganisationId = userService.getUserContext().getUserOrganisation();
		Organisation organisation = organisationRepository.findByOrganisationId(userOrganisationId);
		if (organisation == null) {
			throw new PermissionDeniedException();
		}
		
		String userEmail = userService.getUserContext().getUserEmail();
		
		UserRole userRole = userService.getUserContext().getUserRole();
		if (userRole == UserRole.UNDEFINED || userRole == UserRole.UNAUTHORIZED) {
			throw new UnauthorizedException();
		}
		
		userRole = userRole.claimRole(userEmail, userOrganisationId);
		if (userRole == UserRole.UNAUTHORIZED) {
			throw new UnauthorizedException();
		}
		
		return true;
	}
}