package dk.medcom.video.api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dk.medcom.video.api.actuator.VdxApiMetrics;
import dk.medcom.video.api.service.PoolInfoService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;

@Configuration
public class MonitoringConfiguration {

	
	@Autowired
	PrometheusConfig prometheusConfig;
	
	@Autowired
	Clock clock;

	@Autowired
	CollectorRegistry collectorRegistry;

	@Autowired
	PoolInfoService poolInfoService;

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
