package dk.medcom.video.api.configuration;

import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.organisation.OrganisationDatabaseStrategy;
import dk.medcom.video.api.organisation.OrganisationServiceStrategy;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganisationStrategyConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationStrategyConfiguration.class);

	@Bean
	@ConditionalOnProperty(value="organisation.service.enabled", matchIfMissing = true, havingValue = "false")
	public OrganisationStrategy organisationDbStrategy(OrganisationRepository organisationRepository) {
		LOGGER.info("Starting up with database organisation strategy.");
		return new OrganisationDatabaseStrategy(organisationRepository);
	}

	@Bean
	@ConditionalOnProperty(value="organisation.service.enabled", matchIfMissing = false, havingValue = "true")
	public OrganisationStrategy organisationServiceStrategy(@Value("${organisation.service.endpoint}") String endpoint) {
		LOGGER.info("Starting up with service organisation strategy.");
		return new OrganisationServiceStrategy(endpoint);
	}
}
