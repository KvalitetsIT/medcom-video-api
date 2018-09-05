package dk.medcom.video.api.context;

public class UserContextImpl implements UserContext {

	protected String userOrganisation;
	
	public UserContextImpl(String userOrganisation) {
		this.userOrganisation = userOrganisation;
	}
	
	@Override
	public String getUserOrganisation() {
		return userOrganisation;
	}

}
