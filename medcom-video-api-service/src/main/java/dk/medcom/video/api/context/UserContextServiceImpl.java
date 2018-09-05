package dk.medcom.video.api.context;

import org.springframework.beans.factory.annotation.Autowired;

public class UserContextServiceImpl implements UserContextService {

	@Autowired
	private UserContextFactory userContextFactory;
	
	private UserContext userContext;
	
	public synchronized UserContext getUserContext()  {
		
		if (userContext == null) {
			userContext = userContextFactory.getUserContext();
		}
		return userContext;
	}
}
