package dk.medcom.video.api.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.UnauthorizedException;

@Aspect
@Component
public class APISecurityAspect {
	
	@Autowired
	UserContextService userService;
	
	@Before("@annotation(aPISecurityAnnotation)")
	public void APISecurityAnnotation(JoinPoint joinPoint, APISecurityAnnotation aPISecurityAnnotation) throws Throwable {

		UserRole[] allowedUserRoles = aPISecurityAnnotation.value();
		UserRole userRole = userService.getUserContext().getUserRole();
		if (!checkRoleAllowed(allowedUserRoles, userRole)) {
			throw new UnauthorizedException();
		}
    }
	
	private boolean checkRoleAllowed(UserRole[] allowedUserRoles, UserRole userRole) {
		for (int i = 0; i < allowedUserRoles.length; i++){
	        if (allowedUserRoles[i] == userRole) {
	        	return true;
	        }
	 	}
		return false;
	}

}