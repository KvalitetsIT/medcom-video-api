package dk.medcom.video.api.context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Component
public class WspUserContext extends RestTemplate implements UserContextFactory {

	private static Logger LOGGER = LoggerFactory.getLogger(WspUserContext.class);

	private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
	
	@Value("${SESSION.ID:SESSION}")
	private String sessionId;

	@Value("${sessiondata.headername:}")
	private String sessionDataHttpHeaderInput;
	
	@Value("${userservice.url}")
	private String userServiceUrl;

	@Value("${userservice.token.attribute.organisation}")
	private String userServiceTokenAttributeOrganisation;

	@Value("${userservice.token.attribute.email}")
	private String userServiceTokenAttributeEmail;

	@Value("${userservice.token.attribute.userrole}")
	private String userServiceTokenAttributeUserRole;

	@Value("${mapping.role.provisioner}")
	private String mappingRoleProvisioner;
	
	@Value("${mapping.role.admin}")
	private String mappingRoleAdmin;
	
	@Value("${mapping.role.user}")
	private String mappingRoleUser;
	
	@Value("${mapping.role.meeting_planner}")
	private String mappingRoleMeetingPlanner;

	@Override
	public UserContext getUserContext() {
		SessionData sessionData = getSessionData();
		String organisationId = sessionData.getUserAttribute(userServiceTokenAttributeOrganisation);
		String email = sessionData.getUserAttribute(userServiceTokenAttributeEmail);
		List<UserRole> userRoles = getUserRoles(sessionData);
		return new UserContextImpl(organisationId, email, userRoles);
	}

	private List<UserRole> getUserRoles(SessionData sessionData) {
		List<UserRole> userRoles = new LinkedList<>();

		List<String> userRoleStrList = sessionData.getUserAttributes(userServiceTokenAttributeUserRole);
		LOGGER.debug("User role is: " + userRoleStrList );
		LOGGER.debug("Map values are: Provisioner: " + mappingRoleProvisioner + " Admin: " + mappingRoleAdmin + " User: " + mappingRoleUser + " Meeting Planner: " + mappingRoleMeetingPlanner); 

		if (userRoleStrList != null && userRoleStrList.size() > 0) {
			for (String userRoleStr : userRoleStrList) {
				if (userRoleStr.equals(mappingRoleProvisioner)) {
					if ((sessionData.getUserAttribute(userServiceTokenAttributeOrganisation) != null) && (sessionData.getUserAttribute(userServiceTokenAttributeEmail) != null) ) {
						LOGGER.debug("Provisioner changed to provisioner_user. Because of organisation and email: " +  sessionData.getUserAttribute(userServiceTokenAttributeOrganisation) + " and " + sessionData.getUserAttribute(userServiceTokenAttributeEmail));
						userRoles.add(UserRole.PROVISIONER_USER);
					} else {
						userRoles.add(UserRole.PROVISIONER);
					}
				} else if (userRoleStr.equals(mappingRoleAdmin)) {
					userRoles.add(UserRole.ADMIN);
				} else if (userRoleStr.equals(mappingRoleUser)) {
					userRoles.add(UserRole.USER);
				} else if (userRoleStr.equals(mappingRoleMeetingPlanner)) {
					userRoles.add(UserRole.MEETING_PLANNER);
				} else { 
					LOGGER.debug("Userrole unknown  "+userRoleStr+" ...ignoring");
				}
			}
		} else {
			LOGGER.error("Attributes from token does not contain role (looking for "+userServiceTokenAttributeUserRole+")");
		}
		return userRoles;
	}

	public SessionData getSessionData() {
		String sessionDataFromHeader = getSessiondataFromHeader();
		if (sessionDataFromHeader != null && !sessionDataFromHeader.equals("")) {
			return parseSessionDataValue(sessionDataFromHeader);
		}

		LOGGER.debug("Calling user service to get session data.");
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

	public SessionData parseSessionDataValue(String encoded) {
		LOGGER.debug("Parsing session data");
		String decoded = "";
		try {
			LOGGER.debug("Decoding session data.");
			decoded = new String(Base64.getDecoder().decode(encoded));
		} catch (IllegalArgumentException e) {
			LOGGER.error("Failed to decode headervalue: "+encoded);
			return null;
		}
	    try {
			LOGGER.debug("Parsing session data.");
			SessionData sessionData = mapper.readValue(decoded, SessionData.class);
			if (!sessionData.containsUserAttributes()) {
				LOGGER.debug("Session data does not contain user attributes.");
				return null;
			}
			return sessionData;
		} catch (IOException e) {
			LOGGER.error("Failed to parse headervalue: "+decoded);
			return null;
		}
		
	}
	
	private String getSessiondataFromHeader() {
		if (sessionDataHttpHeaderInput != null && !sessionDataHttpHeaderInput.equals("")) {
			LOGGER.debug("Trying to get session data from HTTP header: " + sessionDataHttpHeaderInput);
			HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			return servletRequest.getHeader(sessionDataHttpHeaderInput);
		}
		LOGGER.debug("Session data not found in HTTP header");
		return null;
	}
	
	private String getSessionId() {
		HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return servletRequest.getHeader(sessionId);
	}
}
