package dk.medcom.video.api.test;

import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContext;

@Component
public class TestUserContext implements UserContext {

	@Override
	public String getUserOrganisation() {
		return "kvak";
	}
}
