package dk.medcom.video.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;

@EnableAutoConfiguration
@Configuration
@ComponentScan({ "dk.medcom.video.api.test", "dk.medcom.video.api.configuration"})
public class TestApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234");
		mysql.start();
		String jdbcUrl = mysql.getJdbcUrl();
		System.setProperty("jdbc.url", jdbcUrl);
		SpringApplication.run(new Object[] { TestApplication.class }, args);
	}
}
