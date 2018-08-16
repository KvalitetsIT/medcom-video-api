package dk.medcom.video.api.context;

import java.util.List;

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
public class WspUserContext extends RestTemplate implements UserContext {

	private static Logger LOGGER = LoggerFactory.getLogger(WspUserContext.class);

	@Value("${SESSION.ID:SESSION}")
	private String sessionId;

	@Value("${userservice.url}")
	private String userServiceUrl;

	@Value("${userservice.token.attribute.organisation}")
	private String userServiceTokenAttributeOrganisation;

	@Override
	public String getUserOrganisation() {
		return getUserAttribute(userServiceTokenAttributeOrganisation);
	}
	
	public String getUserAttribute(String attributeName) {
		SessionData sessionData = getSessionData();
		if (sessionData != null) {
			List<String> orgs = sessionData.getUserAttributes().get(attributeName);
			if (orgs != null && orgs.size() > 0) {
				return orgs.get(0);
			} else {
				LOGGER.error("no attribute with key:"+attributeName);
			}
		}
		return null;
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
