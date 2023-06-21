package dk.medcom.video.api.configuration;

import dk.kvalitetsit.audit.client.AuditClient;
import dk.kvalitetsit.audit.client.messaging.MessagePublisher;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserContextServiceImpl;
import dk.medcom.video.api.dao.*;
import dk.medcom.video.api.interceptor.OrganisationInterceptor;
import dk.medcom.video.api.interceptor.UserSecurityInterceptor;
import dk.medcom.video.api.organisation.OrganisationServiceClient;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClientImpl;
import dk.medcom.video.api.service.*;
import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.prometheus.client.CollectorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
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

	@Autowired
	private OrganisationServiceClient organisationServiceClient;

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
	public SchedulingTemplateService schedulingTemplateService(SchedulingTemplateRepository schedulingTemplateRepository,
															   OrganisationService organisationService,
															   MeetingUserService meetingUserService,
															   OrganisationTreeServiceClient organisationTreeServiceClient,
															   @Value("${scheduling.template.default.conferencing.sys.id}") Long conferencingSysId,
															   @Value("${scheduling.template.default.uri.prefix}") String uriPrefix,
															   @Value("${scheduling.template.default.uri.domain}") String uriDomain,
															   @Value("${scheduling.template.default.host.pin.required}") boolean hostPinRequired,
															   @Value("${scheduling.template.default.host.pin.range.low}") Long hostPinRangeLow,
															   @Value("${scheduling.template.default.host.pin.range.high}") Long hostPinRangeHigh,
															   @Value("${scheduling.template.default.guest.pin.required}") boolean guestPinRequired,
															   @Value("${scheduling.template.default.guest.pin.range.low}") Long guestPinRangeLow,
															   @Value("${scheduling.template.default.guest.pin.range.high}") Long guestPinRangeHigh,
															   @Value("${scheduling.template.default.vmravailable.before}") int vMRAvailableBefore,
															   @Value("${scheduling.template.default.max.participants}") int maxParticipants,
															   @Value("${scheduling.template.default.end.meeting.on.end.time}") boolean endMeetingOnEndTime,
															   @Value("${scheduling.template.default.uri.number.range.low}") Long uriNumberRangeLow,
															   @Value("${scheduling.template.default.uri.number.range.high}") Long uriNumberRangeHigh,
															   @Value("${scheduling.template.default.ivr.theme}") String ivrTheme) {

		return new SchedulingTemplateServiceImpl(
				schedulingTemplateRepository,
				organisationService,
				meetingUserService,
				organisationTreeServiceClient,
				conferencingSysId,
				uriPrefix,
				uriDomain,
				hostPinRequired,
				hostPinRangeLow,
				hostPinRangeHigh,
				guestPinRequired,
				guestPinRangeLow,
				guestPinRangeHigh,
				vMRAvailableBefore,
				maxParticipants,
				endMeetingOnEndTime,
				uriNumberRangeLow,
				uriNumberRangeHigh,
				ivrTheme
		);

	}

	@Bean
	public OrganisationService organisationService(UserContextService userContextService, OrganisationRepository organisationRepository, OrganisationStrategy organisationStrategy) {
		return new OrganisationServiceImpl(userContextService, organisationRepository, organisationStrategy);
	}

	@Bean
	public MeetingUserService meetingUserService(MeetingUserRepository meetingUserRepository, UserContextService userContextService, OrganisationService organisationService) {
		return new MeetingUserServiceImpl(meetingUserRepository, userContextService, organisationService);
	}

	@Bean
	public MeetingService meetingService(MeetingRepository meetingRepository,
										 MeetingUserService meetingUserService,
										 SchedulingInfoService schedulingInfoService,
										 SchedulingStatusService schedulingStatusService,
										 OrganisationService organisationService,
										 UserContextService userService,
										 MeetingLabelRepository meetingLabelRepository,
										 OrganisationRepository organisationProxy,
										 OrganisationTreeServiceClient organisationTreeServiceClient,
										 AuditService auditClient, SchedulingInfoEventPublisher schedulingInfoEventPublisher) {
		return new MeetingServiceImpl(
				meetingRepository,
				meetingUserService,
				schedulingInfoService,
				schedulingStatusService,
				organisationService,
				userService,
				meetingLabelRepository,
				organisationProxy,
				organisationTreeServiceClient,
				auditClient,
				schedulingInfoEventPublisher);
	}

	@Bean
	public SchedulingInfoService schedulingInfoService(SchedulingInfoRepository schedulingInfoRepository,
													   SchedulingTemplateRepository schedulingTemplateRepository,
													   SchedulingTemplateService schedulingTemplateService,
													   SchedulingStatusService schedulingStatusService,
													   MeetingUserService meetingUserService,
													   OrganisationRepository organisationRepository,
													   OrganisationStrategy organisationStrategy,
													   UserContextService userContextService,
													   @Value("${overflow.pool.organisation.id}") String overflowPoolOrganisationId,
													   OrganisationTreeServiceClient organisationTreeServiceClient,
													   AuditService auditService,
													   CustomUriValidator customUriValidator,
													   SchedulingInfoEventPublisher schedulingInfoEventPublisher,
													   NewProvisionerOrganisationFilter newProvisionerOrganisationFilter,
													   PoolFinderService poolFinderService,
													   @Value("${scheduling.info.citizen.portal}") String citizenPortal) {
		return new SchedulingInfoServiceImpl(
				schedulingInfoRepository,
				schedulingTemplateRepository,
				schedulingTemplateService,
				schedulingStatusService,
				meetingUserService,
				organisationRepository,
				organisationStrategy,
				userContextService,
				overflowPoolOrganisationId,
				organisationTreeServiceClient,
				auditService,
				customUriValidator,
				schedulingInfoEventPublisher,
				newProvisionerOrganisationFilter,
				poolFinderService,
				citizenPortal
		);
	}

	@Bean
	public SchedulingStatusService schedulingStatusService(SchedulingStatusRepository schedulingStatusRepository) {
		return new SchedulingStatusServiceImpl(
				schedulingStatusRepository
		);
	}

	@Bean
	public PoolInfoService poolInfoService(OrganisationRepository organisationRepository,
										   SchedulingInfoRepository schedulingInfoRepository,
										   SchedulingTemplateRepository schedulingTemplateRepository,
										   OrganisationStrategy organisationStrategy,
										   PoolInfoRepository poolInfoRepository) {
		return new PoolInfoServiceImpl(
				organisationRepository,
				schedulingInfoRepository,
				schedulingTemplateRepository,
				organisationStrategy,
				poolInfoRepository
		);
	}

	@Bean
	public PoolService poolService(PoolInfoService poolInfoService,
								   SchedulingInfoService schedulingInfoService,
								   MeetingUserRepository meetingUserRepository,
								   OrganisationRepository organisationRepository,
								   NewProvisionerOrganisationFilter newProvisionerOrganisationFilter,
								   @Value("${pool.fill.organisation}") String poolOrganisation,
								   @Value("${pool.fill.organisation.user}") String poolOrganisationUser) {
		return new PoolServiceImpl(poolInfoService, schedulingInfoService, meetingUserRepository, organisationRepository, newProvisionerOrganisationFilter, poolOrganisation, poolOrganisationUser);
	}

	@Bean
	public PoolFinderService poolFinderService(SchedulingInfoRepository schedulingInfoRepository, @Value("${pool.meeting.minimumAgeSec:60}") int minimumAgeSec) {
		return new PoolFinderServiceImpl(schedulingInfoRepository, minimumAgeSec);
	}

	@Bean
	public NewProvisionerOrganisationFilter organisationFilter(@Value("${event.organisation.filter:#{null}}") List<String> filterOrganisations) {
		return new NewProvisionerOrganisationFilterImpl(filterOrganisations == null ? Collections.emptyList() : filterOrganisations);
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
		return new OrganisationInterceptor(organisationStrategy, organisationRepository, organisationServiceClient);
	}

	@Bean
	public SchedulingInfoEventPublisher schedulingInfoEventPublisher(@Qualifier("natsEventPublisher") MessagePublisher eventPublisher, EntitiesIvrThemeDao entitiesIvrThemeDao, NewProvisionerOrganisationFilter filterOrganisations) {
		return new SchedulingInfoEventPublisherImpl(eventPublisher, entitiesIvrThemeDao, filterOrganisations);
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

	@Bean
	public PrometheusScrapeEndpoint prometheus() {
		return new PrometheusScrapeEndpoint(collectorRegistry);
	}
}
