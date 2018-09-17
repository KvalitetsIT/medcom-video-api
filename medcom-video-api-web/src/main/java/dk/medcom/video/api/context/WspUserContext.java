package dk.medcom.video.api.context;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class WspUserContext extends RestTemplate implements UserContextFactory {

	private static Logger LOGGER = LoggerFactory.getLogger(WspUserContext.class);

	@Value("${SESSION.ID:SESSION}")
	private String sessionId;

	@Value("${userservice.url}")
	private String userServiceUrl;

	@Value("${userservice.token.attribute.organisation}")
	private String userServiceTokenAttributeOrganisation;

	@Value("${userservice.token.attribute.email}")
	private String userServiceTokenAttributeEmail;

	@Value("${userservice.token.attribute.userrole}")
	private String userServiceTokenAttributeUserRole;


	@Override
	public UserContext getUserContext() {
		SessionData sessionData = getSessionData();
		String organisationId = sessionData.getUserAttribute(userServiceTokenAttributeOrganisation);
		String email = sessionData.getUserAttribute(userServiceTokenAttributeEmail);
		UserRole userRole = getUserRole(sessionData);
		return new UserContextImpl(organisationId, email, userRole);
	}

	private UserRole getUserRole(SessionData sessionData) {
		String userRoleStr = sessionData.getUserAttribute(userServiceTokenAttributeUserRole);
		if (userRoleStr != null) {
			try {
				UserRole userRole = UserRole.valueOf(userRoleStr);
				return userRole;
			} catch (IllegalArgumentException e) {
				LOGGER.error("Userrole "+userRoleStr+" not legal value");
				return null;
			}
		} else {
			LOGGER.error("Attributes from token does not contain role (looking for "+userServiceTokenAttributeUserRole+")");
			return null;
		}
	}

	public SessionData getSessionData() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(sessionId, getSessionId());
		HttpEntity<Void> request = new HttpEntity<>(headers);
		ResponseEntity<SessionData> response = exchange(userServiceUrl + "/getsessiondata", HttpMethod.GET, request, SessionData.class); 
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return response.getBody();
		} else {
			LOGGER.error("return code from getsessiondata:"+response.getStatusCodeValue());
			return null;
		}
	}		

	private String getSessionId() {
		HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return servletRequest.getHeader(sessionId);
	}
}
