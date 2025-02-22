package dk.medcom.video.api.configuration;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dk.kvalitetsit.audit.client.AuditClient;
import dk.kvalitetsit.audit.client.messaging.MessagePublisher;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserContextServiceImpl;
import dk.medcom.video.api.dao.EntitiesIvrThemeDao;
import dk.medcom.video.api.dao.MeetingAdditionalInfoRepository;
import dk.medcom.video.api.dao.MeetingLabelRepository;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.PoolHistoryDao;
import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.SchedulingStatusRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.interceptor.OrganisationInterceptor;
import dk.medcom.video.api.interceptor.UserSecurityInterceptor;
import dk.medcom.video.api.organisation.OrganisationServiceClient;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClientImpl;
import dk.medcom.video.api.service.AuditService;
import dk.medcom.video.api.service.AuditServiceImpl;
import dk.medcom.video.api.service.CustomUriValidator;
import dk.medcom.video.api.service.CustomUriValidatorImpl;
import dk.medcom.video.api.service.MeetingService;
import dk.medcom.video.api.service.MeetingServiceImpl;
import dk.medcom.video.api.service.MeetingUserService;
import dk.medcom.video.api.service.MeetingUserServiceImpl;
import dk.medcom.video.api.service.NewProvisionerOrganisationFilter;
import dk.medcom.video.api.service.NewProvisionerOrganisationFilterImpl;
import dk.medcom.video.api.service.OrganisationService;
import dk.medcom.video.api.service.OrganisationServiceImpl;
import dk.medcom.video.api.service.PoolFinderService;
import dk.medcom.video.api.service.PoolFinderServiceImpl;
import dk.medcom.video.api.service.PoolHistoryService;
import dk.medcom.video.api.service.PoolHistoryServiceImpl;
import dk.medcom.video.api.service.PoolInfoService;
import dk.medcom.video.api.service.PoolInfoServiceImpl;
import dk.medcom.video.api.service.PoolService;
import dk.medcom.video.api.service.PoolServiceImpl;
import dk.medcom.video.api.service.SchedulingInfoEventPublisher;
import dk.medcom.video.api.service.SchedulingInfoEventPublisherImpl;
import dk.medcom.video.api.service.SchedulingInfoService;
import dk.medcom.video.api.service.SchedulingInfoServiceImpl;
import dk.medcom.video.api.service.SchedulingStatusService;
import dk.medcom.video.api.service.SchedulingStatusServiceImpl;
import dk.medcom.video.api.service.SchedulingTemplateService;
import dk.medcom.video.api.service.SchedulingTemplateServiceImpl;
import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.prometheus.metrics.model.registry.PrometheusRegistry;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan({"dk.medcom.video.api.service", "dk.medcom.video.api.controller", "dk.medcom.video.api.aspect", "dk.kvalitetsit.audit"})
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
										 AuditService auditClient,
										 SchedulingInfoEventPublisher schedulingInfoEventPublisher,
										 MeetingAdditionalInfoRepository meetingAdditionalInfoRepository) {
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
				schedulingInfoEventPublisher,
				meetingAdditionalInfoRepository);
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
	public SchedulingInfoEventPublisher schedulingInfoEventPublisher(@Qualifier("natsEventPublisher") MessagePublisher eventPublisher, EntitiesIvrThemeDao entitiesIvrThemeDao) {
		return new SchedulingInfoEventPublisherImpl(eventPublisher, entitiesIvrThemeDao);
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
	private PrometheusRegistry prometheusRegistry;

	@Bean
	public PrometheusScrapeEndpoint prometheus() {
		// Previously, the constructor took only the prometheusRegistry object.
		// The exporterProperties field is now mandatory, but if set to null,
		// it behaves the same as the old constructor.
		return new PrometheusScrapeEndpoint(prometheusRegistry, null);
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseTrailingSlashMatch(true);
	}
}
