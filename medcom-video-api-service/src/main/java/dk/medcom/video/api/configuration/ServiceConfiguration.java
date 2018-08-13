package dk.medcom.video.api.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan("dk.medcom.video.api.controller")
public class ServiceConfiguration extends WebMvcConfigurerAdapter {

}
