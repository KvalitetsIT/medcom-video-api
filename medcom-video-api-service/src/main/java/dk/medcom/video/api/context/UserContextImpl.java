package dk.medcom.video.api.context;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class UserContextImpl extends AbstractUserContextImpl {

	protected String userOrganisation;
	protected String userEmail;
	protected List<UserRole> userRoles;
	private final String autoCreateOrganisation;

	public UserContextImpl(String userOrganisation, String userEmail, UserRole userRole, String autoCreateOrganisation) {
		this(userOrganisation, userEmail, UserContextImpl.createList(userRole), autoCreateOrganisation);
	}
	
	public UserContextImpl(String userOrganisation, String userEmail, List<UserRole> userRoles, String autoCreateOrganisation) {
		this.userOrganisation = userOrganisation;
		this.userEmail = userEmail;
		this.userRoles = userRoles;
		this.autoCreateOrganisation = autoCreateOrganisation;
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
	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	@Override
	public Optional<String> getAutoCreateOrganisation() {
		return Optional.ofNullable(autoCreateOrganisation);
	}

	private static List<UserRole> createList(UserRole userRole) {
		List<UserRole> userRoles = new LinkedList<>();
		userRoles.add(userRole);
		return userRoles;
	}
}
