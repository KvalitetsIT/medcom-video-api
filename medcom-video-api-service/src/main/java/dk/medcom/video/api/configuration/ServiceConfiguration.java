package dk.medcom.video.api.configuration;

import dk.medcom.audit.client.AuditClient;
import dk.medcom.audit.client.messaging.nats.NatsPublisher;
import dk.medcom.video.api.actuator.VdxApiMetrics;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserContextServiceImpl;
import dk.medcom.video.api.dao.EntitiesIvrThemeDao;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.PoolHistoryDao;
import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.interceptor.OrganisationInterceptor;
import dk.medcom.video.api.interceptor.UserSecurityInterceptor;
import dk.medcom.video.api.organisation.*;
import dk.medcom.video.api.service.*;
import dk.medcom.video.api.service.impl.AuditServiceImpl;
import dk.medcom.video.api.service.impl.CustomUriValidatorImpl;
import dk.medcom.video.api.service.impl.SchedulingInfoEventPublisherImpl;
import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan({"dk.medcom.video.api.service", "dk.medcom.video.api.controller", "dk.medcom.video.api.aspect"})
public class ServiceConfiguration implements WebMvcConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfiguration.class);

	@Autowired
	private OrganisationStrategy organisationStrategy;

	@Autowired
	private OrganisationRepository organisationRepository;

	@Value("${ALLOWED_ORIGINS}")
	private List<String> allowedOrigins;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LOGGER.debug("Adding interceptors");
		registry.addInterceptor(organisationInterceptor());
		registry.addInterceptor(userSecurityInterceptor());
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		allowedOrigins.forEach(config::addAllowedOrigin);
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(0);

		return bean;
	}

	@Bean
	public CustomUriValidator customUriValidator() {
		return new CustomUriValidatorImpl();
	}

	@Bean
	public AuditService auditService(AuditClient auditClient) {
		return new AuditServiceImpl(auditClient);
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
	public SchedulingInfoEventPublisher schedulingInfoEventPublisher(@Qualifier("natsEventPublisher") NatsPublisher eventPublisher, EntitiesIvrThemeDao entitiesIvrThemeDao, @Value("event.organisation.filter") List<String> filterOrganisations) {
		return new SchedulingInfoEventPublisherImpl(eventPublisher, entitiesIvrThemeDao, x -> filterOrganisations.isEmpty() || filterOrganisations.contains(x));
	}

	@Bean
	public OrganisationTreeServiceClient organisationTreeServiceClient(@Value("${organisationtree.service.endpoint}") String endpoint) {
		return new OrganisationTreeServiceClientImpl(endpoint);
	}

	@Bean
	public PoolHistoryService poolHistoryService(PoolInfoRepository poolInfoRepository, PoolHistoryDao PoolHistorydao) {
		return new PoolHistoryServiceImpl(poolInfoRepository, PoolHistorydao);
	}

	@Bean
	public UserSecurityInterceptor userSecurityInterceptor() {
		LOGGER.debug("Creating userSecurityInterceptor");
		return new UserSecurityInterceptor();
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
	public UserContextService userContextService() {
		return new UserContextServiceImpl();
	}

	@Autowired
	private PrometheusConfig prometheusConfig;

	@Autowired
	private Clock clock;

	@Autowired
	private CollectorRegistry collectorRegistry;

	@Autowired
    private PoolInfoService poolInfoService;

	@Bean
	public PrometheusScrapeEndpoint prometheus() {
		return new PrometheusScrapeEndpoint(collectorRegistry);
	}

	@Bean
	public PrometheusScrapeEndpoint appmetricsScrapeEndpoint() {
		PrometheusMeterRegistry registry = new PrometheusMeterRegistry(prometheusConfig, new CollectorRegistry(false), clock);
		return new VdxApiMetrics(registry.getPrometheusRegistry(), poolInfoService);
	}
}
