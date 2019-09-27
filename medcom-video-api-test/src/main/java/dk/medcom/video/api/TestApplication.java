package dk.medcom.video.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

@EnableAutoConfiguration
@Configuration
@ComponentScan({ "dk.medcom.video.api.test", "dk.medcom.video.api.configuration"})
public class TestApplication extends SpringBootServletInitializer {
	private DataSource dataSource;

	public static void main(String[] args) {
		MySQLContainer mysql = new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234");
		mysql.start();
		String jdbcUrl = mysql.getJdbcUrl();
		System.setProperty("jdbc.url", jdbcUrl);

		SpringApplication.run(new Object[] { TestApplication.class }, args);
	}

	public TestApplication(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void setSchedulingTemplateTestData() throws SQLException  {
		dataSource.getConnection().createStatement();
	}
}
