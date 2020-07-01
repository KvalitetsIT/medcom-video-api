package dk.medcom.video.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import dk.medcom.video.api.service.PoolInfoService;

@RestController
public class MetricsController {
	private static Logger LOGGER = LoggerFactory.getLogger(MeetingController.class);

	@Autowired
	PoolInfoService poolInfoService;

	/*
	@RequestMapping(value = "/metrics/app", method = RequestMethod.GET)
	public Gauge <MeetingDto> getAppMetrics()  {
		LOGGER.debug("Entry of metrics.get");

		List<PoolInfoEntity> poolInfos = poolInfoService.getAllPoolInfo();

		SimpleMeterRegistry registry = new SimpleMeterRegistry();
		Gauge gauge = Gauge.builder("cache.size", poolInfos, List::size)
				  .register(registry);
		
		LOGGER.debug("Exit of metrics.get");
		return gauge;
	}
*/
}
