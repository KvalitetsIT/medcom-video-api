package dk.medcom.video.api.context;

public class UserContextImpl implements UserContext {

	protected String userOrganisation;
	protected String userEmail;
	protected UserRole userRole;
	
	public UserContextImpl(String userOrganisation, String userEmail, UserRole userRole) {
		this.userOrganisation = userOrganisation;
		this.userEmail = userEmail;
		this.userRole = userRole;
	}
	
	@Override
	public String getUserOrganisation() {
		return userOrganisation;
	}

	@Override
	public String getUserEmail() {
		return userEmail;
	}

	@Override
	public UserRole getUserRole() {
		return userRole;
	}
}
