package dk.medcom.video.api.organisation;

import dk.medcom.video.api.keycloak.KeycloakHttpClientService;
import dk.medcom.video.api.organisation.model.OrganisationSimple;
import dk.medcom.video.api.service.exception.OrganisationServiceClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class OrganisationServiceClientV2Impl implements OrganisationServiceClientV2 {
    private static final Logger logger = LoggerFactory.getLogger(OrganisationServiceClientV2Impl.class);
    private final KeycloakHttpClientService keycloakHttpClientService;
    private final RestClient restClient;

    public OrganisationServiceClientV2Impl(KeycloakHttpClientService keycloakHttpClientService, String endpoint, RestClient.Builder restClientBuilder) {
        this.keycloakHttpClientService = keycloakHttpClientService;
        this.restClient = restClientBuilder.baseUrl(endpoint).build();
    }

    @Override
    public List<OrganisationSimple> getDescendantsOfOrganisation(String code) {
        logger.debug("Calling /services/v2/organisation/{}/descendants", code);

        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/organisation/" + code + "/descendants")
                            .build())
                    .header("Authorization", retrieveAccessTokenHeader())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            logger.warn("Caught exception from organisation service request. Exception: ", e);
            throw new OrganisationServiceClientException("Caught exception from organisation service request. Message: %s".formatted(e.getMessage()));
        }
    }

    private String retrieveAccessTokenHeader() {
        logger.debug("Get video api access token for organisation api request.");
        return "Bearer " + keycloakHttpClientService.getVideoApiAccessToken();
    }
}
