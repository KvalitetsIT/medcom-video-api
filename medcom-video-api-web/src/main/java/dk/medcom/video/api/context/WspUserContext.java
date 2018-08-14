package dk.medcom.video.api.context;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WspUserContext extends RestTemplate implements UserContext {

	@Value("${userservice.session.id}")
	private String sessionId;
	
	@Value("${userservice.url}")
	private String userServiceUrl;

	@Value("${userservice.token.attribute.organisation}")
	private String userServiceTokenAttributeOrganisation;

	@Override
	public String getUserOrganisation() {
		SessionData sessionData = getSessionData();
		List<String> orgs = sessionData.getUserAttributes().get(userServiceTokenAttributeOrganisation);
		return orgs.get(0);
	}

	public SessionData getSessionData() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(sessionId, getSessionId());
		HttpEntity<Void> request = new HttpEntity<>(headers);
		ResponseEntity<SessionData> response = exchange(userServiceUrl + "/getsessiondata", HttpMethod.GET, request, SessionData.class); 
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return response.getBody();
		} else {
			return null;
		}
	}		

	private String getSessionId() {
		HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		if (servletRequest.getCookies() != null) {
			for (Cookie c : servletRequest.getCookies()) {
				if (c.getName().equals(sessionId)) {
					return c.getValue();
				}
			}   		
		}
		return "";
	}
}
