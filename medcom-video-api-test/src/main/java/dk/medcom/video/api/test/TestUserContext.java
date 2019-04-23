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
		//return new UserContextImpl("kvak", "me2@me.dk", UserRole.ADMIN);
//		return new UserContextImpl("kvak", "me2@me.dk", UserRole.PROVISIONER_USER); 		//0 meetings for this user in test data 
//		return new UserContextImpl("test-org", "me@me101.dk", UserRole.PROVISIONER_USER);  	//4 meetings for this user in test data
//		return new UserContextImpl("test-org", "me@me105organizer.dk", UserRole.USER);  	//2 meeting for this user in test data
//		return new UserContextImpl("test-org", "me@me101.dk", UserRole.USER);  				//3 meetings for this user in test data
//		return new UserContextImpl("company 2", "me@me107.dk", UserRole.USER);  				//testing schedulingTemplate
		return new UserContextImpl("company 2", "me@me107.dk", UserRole.PROVISIONER_USER); 		//testing schedulingTemplate - manual check: vmrAvailableBefore is 20
//		return new UserContextImpl("test-org", "me@me101.dk", UserRole.MEETING_PLANNER);	//4 meetings for this user in test data (two as organizer, one as creator
//		return new UserContextImpl("test-org", "me@me102.dk", UserRole.ADMIN);	
//
//		return new UserContextImpl("test-org", "", UserRole.MEETING_PLANNER);	//4 meetings for this user in test data (two as organizer, one as creator
//		return new UserContextImpl("test-org", "", UserRole.PROVISIONER);  	//4 meetings for this user in test data
	}
	
}
