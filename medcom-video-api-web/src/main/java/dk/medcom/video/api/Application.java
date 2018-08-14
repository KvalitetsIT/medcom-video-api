package dk.medcom.video.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan({"dk.medcom.video.api.context", "dk.medcom.video.api.configuration"})
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(new Object[] { Application.class }, args);
	}
}
