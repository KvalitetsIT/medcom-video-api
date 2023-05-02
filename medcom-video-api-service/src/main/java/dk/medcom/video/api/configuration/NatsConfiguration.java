package dk.medcom.video.api.configuration;

import dk.kvalitetsit.audit.client.actuator.NatsHealthIndicator;
import dk.kvalitetsit.audit.client.messaging.MessagePublisher;
import dk.kvalitetsit.audit.client.messaging.nats.NatsPublisher;
import io.nats.client.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class NatsConfiguration {
    @Bean
    public NatsHealthIndicator eventPublisherNatsHealthIndicator(Connection natsConnection) {
        return new NatsHealthIndicator(natsConnection);
    }

    @Bean("natsEventPublisher")
    public MessagePublisher eventPublisher(Connection jetStream, @Value("${events.nats.subject.scheduling-info}") String subject) throws IOException {
        return new NatsPublisher(jetStream.jetStream(), subject);
    }
}
