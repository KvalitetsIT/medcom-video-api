package dk.medcom.video.api.test;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextFactory;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserRole;

@Component
public class TestUserContext implements UserContextFactory {

	@Override
	public UserContext getUserContext() {
		
		
		
		HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String testuserorg = servletRequest.getHeader("testuserorg");
		if (testuserorg == null) {
			testuserorg = "test-org";
		}

					//return new UserContextImpl("kvak", "me2@me.dk", UserRole.ADMIN);
					//		return new UserContextImpl("kvak", "me2@me.dk", UserRole.PROVISIONER_USER); 		//0 meetings for this user in test data 
//		return new UserContextImpl("test-org", "me@me101.dk", UserRole.PROVISIONER_USER);  	//4 meetings for this user in test data
					//		return new UserContextImpl("test-org", "me@me105organizer.dk", UserRole.USER);  	//2 meeting for this user in test data
//		return new UserContextImpl("test-org", "me@me101.dk", UserRole.USER);  				//3 meetings for this user in test data
					//		return new UserContextImpl("company 2", "me@me107.dk", UserRole.USER);  				//testing schedulingTemplate
//							return new UserContextImpl("company 2", "me@me107.dk", UserRole.PROVISIONER_USER); 		//testing schedulingTemplate - manual check: vmrAvailableBefore is 2
		return new UserContextImpl("pool-test-org1", "me@me107.dk", UserRole.ADMIN); 		//testing schedulingTemplate - manual check: vmrAvailableBefore is 2
//		return new UserContextImpl("pool-test-org", "me@me107.dk", UserRole.ADMIN); 		//testing schedulingTemplate - manual check: vmrAvailableBefore is 2
		// 0
//		return new UserContextImpl(testuserorg, "me@me101.dk", UserRole.ADMIN);	//4 meetings for this user in test data (two as organizer, one as creator
//		return new UserContextImpl("test-org", "me@me102.dk", UserRole.ADMIN);	 //use this to test schedulingTemplates in postman
//
					//		return new UserContextImpl("test-org", "", UserRole.MEETING_PLANNER);	//4 meetings for this user in test data (two as organizer, one as creator
					//		return new UserContextImpl("test-org", "", UserRole.PROVISIONER);  	//4 meetings for this user in test data
		
		//for testing more than one role - normal user and provisioner
//		List<UserRole> userRoles = new LinkedList<>();
//		userRoles.add(UserRole.USER);
//		userRoles.add(UserRole.PROVISIONER_USER);
//		return new UserContextImpl("test-org", "me@me101.dk", userRoles);
	
	}
	
}
