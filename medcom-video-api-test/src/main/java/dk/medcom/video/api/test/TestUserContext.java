package dk.medcom.video.api.test;

import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextFactory;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserRole;

@Component
public class TestUserContext implements UserContextFactory {

	@Override
	public UserContext getUserContext() {
		return new UserContextImpl("kvak", "me2@me.dk", UserRole.ADMIN);
	}
	
}
