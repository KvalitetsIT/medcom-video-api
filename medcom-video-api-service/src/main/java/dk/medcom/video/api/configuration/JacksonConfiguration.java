package dk.medcom.video.api.configuration;

import dk.medcom.video.api.serializer.OffsetDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfiguration {

    @Bean
    public SimpleModule customDateTimeModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new OffsetDateTimeSerializer());
        return module;
    }
}
