package dk.medcom.video.api.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.medcom.video.api.service.exception.KeycloakClientException;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;

public class KeycloakHttpClientServiceImpl implements KeycloakHttpClientService {
    private final static Logger logger = LoggerFactory.getLogger(KeycloakHttpClientServiceImpl.class);
    private final static String keycloakTokenPath = "/protocol/openid-connect/token";

    private final String videoApiClient;
    private final String videoApiClientSecret;
    private final RestClient restClient;

    public KeycloakHttpClientServiceImpl(String endpoint, String videoApiClient,
                                         String videoApiClientSecret, RestClient.Builder restClientBuilder) {
        this.videoApiClient = videoApiClient;
        this.videoApiClientSecret = videoApiClientSecret;
        this.restClient = restClientBuilder.baseUrl(endpoint).build();
    }


    @Override
    public String getVideoApiAccessToken() {
        logger.debug("Get video api client token from keycloak.");

        var response = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(keycloakTokenPath)
                        .build())
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials" +
                        "&client_id=" + videoApiClient +
                        "&client_secret=" + videoApiClientSecret)
                .retrieve()
                .body(KeyCloakToken.class);

        if (response == null) {
            logger.warn("Failed to retrieve video api access token.");
            throw new KeycloakClientException("Failed to retrieve video api access token.");
        }

        return response.accessToken;
    }

    record KeyCloakToken(@JsonProperty("access_token") String accessToken){}
}
