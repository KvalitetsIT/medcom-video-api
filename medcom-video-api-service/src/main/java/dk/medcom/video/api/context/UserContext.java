package dk.medcom.video.api.context;

import java.util.List;

public interface UserContext {

	String getUserOrganisation();
	
	String getUserEmail();
	
	List<UserRole> getUserRoles();
	
	boolean hasNoLegalRoles();
	
	boolean hasAnyNumberOfRoles(List<UserRole> userRoles);
	
	boolean hasOnlyRole(UserRole role);
	
	boolean isOrganisationalMeetingAdministrator();
}
