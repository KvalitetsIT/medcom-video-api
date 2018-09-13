package dk.medcom.video.api.context;

public interface UserContext {

	String getUserOrganisation();
	
	String getUserEmail();
	
	UserRole getUserRole();
}
