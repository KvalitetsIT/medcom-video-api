package dk.medcom.video.api.configuration;

import dk.medcom.video.api.interceptor.OrganisationInterceptor;
import dk.medcom.video.api.organisation.OrganisationDatabaseStrategy;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.organisation.OrganisationServiceStrategy;
import dk.medcom.video.api.repository.OrganisationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserContextServiceImpl;
import dk.medcom.video.api.interceptor.LoggingInterceptor;
import dk.medcom.video.api.interceptor.UserSecurityInterceptor;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan({"dk.medcom.video.api.service", "dk.medcom.video.api.controller", "dk.medcom.video.api.aspect"})
public class ServiceConfiguration extends WebMvcConfigurerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(ServiceConfiguration.class);

	@Autowired
	private OrganisationStrategy organisationStrategy;

	@Autowired
	private OrganisationRepository organisationRepository;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LOGGER.debug("Adding interceptors");
		registry.addInterceptor(loggingInterceptor());
		registry.addInterceptor(organisationInterceptor());
		registry.addInterceptor(userSecurityInterceptor());
	} 

	@Bean
	public OrganisationInterceptor organisationInterceptor() {
		return new OrganisationInterceptor(organisationStrategy, organisationRepository);
	}

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

	@Bean
	public LoggingInterceptor loggingInterceptor() {
		LOGGER.debug("Creating loggingInterceptor");
		return new LoggingInterceptor();
	}
	
	@Bean
	public UserSecurityInterceptor userSecurityInterceptor() {
		LOGGER.debug("Creating userSecurityInterceptor");
		return new UserSecurityInterceptor();
	}
	
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
	public UserContextService userContextService() {
		UserContextServiceImpl ucs = new UserContextServiceImpl();
		return ucs;
	}
}
