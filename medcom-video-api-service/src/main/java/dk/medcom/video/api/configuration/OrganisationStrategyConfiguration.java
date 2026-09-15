package dk.medcom.video.api.configuration;

import dk.medcom.video.api.organisation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganisationStrategyConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationStrategyConfiguration.class);

	@Bean
	public OrganisationStrategy organisationServiceStrategy(OrganisationServiceClientV2 organisationServiceClientV2, OrganisationServiceClient organisationServiceClient) {
		LOGGER.info("Starting up with service organisation strategy.");
		return new OrganisationServiceStrategy(organisationServiceClientV2, organisationServiceClient);
	}

	@Bean
	public OrganisationServiceClient organisationServiceClient(@Value("${organisation.service.endpoint}") String endpoint) {
		return new OrganisationServiceClientImpl(endpoint);
	}
}
