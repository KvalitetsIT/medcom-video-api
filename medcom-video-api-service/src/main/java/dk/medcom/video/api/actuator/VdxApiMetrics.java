package dk.medcom.video.api.actuator;

import dk.medcom.video.api.entity.PoolInfoEntity;
import dk.medcom.video.api.service.PoolInfoService;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;

import java.util.List;

@WebEndpoint(id = "appmetrics")
public class VdxApiMetrics extends PrometheusScrapeEndpoint {

	private static final String ORG_ID_KEY = "ORGANISATION_CODE";
	private static final String ORG_NAME_KEY = "ORGANISATION_NAME";

	private final Gauge wantedPoolSize;
	private final Gauge availablePoolSize;

	PoolInfoService poolInfoService;

	public VdxApiMetrics(CollectorRegistry collectorRegistry, PoolInfoService poolInfoService) {
		super(collectorRegistry);
		this.poolInfoService = poolInfoService;
		
		this.wantedPoolSize = Gauge.build().name("wanted_poolsize").help("Size of the wanted pool").labelNames(ORG_ID_KEY, ORG_NAME_KEY).register(collectorRegistry);
		this.availablePoolSize = Gauge.build().name("available_poolsize").help("Size of the available pool").labelNames(ORG_ID_KEY, ORG_NAME_KEY).register(collectorRegistry);
	}

	@Override
	@ReadOperation(produces = TextFormat.CONTENT_TYPE_004)
	public String scrape() {
		setPoolGauges();
		return super.scrape();
	}

	private void setPoolGauges() {
		wantedPoolSize.clear();
		availablePoolSize.clear();
		List<PoolInfoEntity> poolInfos = poolInfoService.getAllPoolInfo();
		for (PoolInfoEntity poolInfo : poolInfos) {
			wantedPoolSize.labels(poolInfo.getOrganisationCode(), poolInfo.getOrganisationName()).set(poolInfo.getWantedPoolSize());
			availablePoolSize.labels(poolInfo.getOrganisationCode(), poolInfo.getOrganisationName()).set(poolInfo.getAvailablePoolSize());
		}
	}
}
