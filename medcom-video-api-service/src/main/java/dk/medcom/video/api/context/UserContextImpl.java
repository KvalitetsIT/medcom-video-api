package dk.medcom.video.api.context;

public class UserContextImpl implements UserContext {

	protected String userOrganisation;
	protected String userEmail;
	
	public UserContextImpl(String userOrganisation, String userEmail) {
		this.userOrganisation = userOrganisation;
		this.userEmail = userEmail;
	}
	
	@Override
	public String getUserOrganisation() {
		return userOrganisation;
	}

	@Override
	public String getUserEmail() {
		return userEmail;
	}

}
