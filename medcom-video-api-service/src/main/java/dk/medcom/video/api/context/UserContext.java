package dk.medcom.video.api.context;

import java.util.List;
import java.util.Optional;

public interface UserContext {

	String getUserOrganisation();
	
	String getUserEmail();
	
	List<UserRole> getUserRoles();
	
	boolean hasNoLegalRoles();
	
	boolean hasAnyNumberOfRoles(List<UserRole> userRoles);
	
	boolean hasOnlyRole(UserRole role);

	boolean hasRole(UserRole role);

	boolean isOrganisationalMeetingAdministrator();

	Optional<String> getAutoCreateOrganisation();
}
