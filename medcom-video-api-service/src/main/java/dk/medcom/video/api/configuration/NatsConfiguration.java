package dk.medcom.video.api.configuration;

import dk.medcom.audit.client.actuator.NatsHealthIndicator;
import dk.medcom.audit.client.messaging.nats.NatsConnectionHandler;
import dk.medcom.audit.client.messaging.nats.NatsPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class NatsConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(NatsConfiguration.class);

    private NatsConnectionHandler consumerConnectionHandler;

    @Bean
    public NatsHealthIndicator eventPublisherNatsHealthIndicator(@Qualifier("eventPublisher") NatsConnectionHandler natsConnectionHandler) {
        return new NatsHealthIndicator(natsConnectionHandler);
    }

    @Bean("eventPublisher")
    public NatsConnectionHandler natsEventPublisherConnectionHandler(@Value("${events.nats.cluster.id}") String clusterId, @Value("${events.nats.client.id}") String clientId, @Value("${events.nats.url}") String natsUrl) throws IOException, InterruptedException {
        logger.info("Connecting to nats at {} with client id {} and cluster id {}.", natsUrl, clientId, clusterId);
        consumerConnectionHandler = new NatsConnectionHandler(natsUrl, clusterId, clientId + "-event-publisher");
        consumerConnectionHandler.connect();

        return consumerConnectionHandler;
    }

    @Bean("natsEventPublisher")
    public NatsPublisher eventPublisher(NatsConnectionHandler natsEventPublisherConnectionHandler, @Value("${events.nats.subject.scheduling-info}") String subject) {
        return new NatsPublisher(natsEventPublisherConnectionHandler, subject);
    }

    @PreDestroy
    public void destroy() {
        consumerConnectionHandler.close();
    }
}
