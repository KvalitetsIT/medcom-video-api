package dk.medcom.video.api;


import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	
	@Autowired
	private DataSource dataSource;


	public static void main(String[] args) {
		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234");
		mysql.start();
		String jdbcUrl = mysql.getJdbcUrl();
		System.setProperty("jdbc.url", jdbcUrl);
		
		SpringApplication.run(new Object[] { TestApplication.class }, args);
	}
	
	@PostConstruct
	public void setSchedulingTemplateTestData() throws SQLException  {

		Statement statement;
		statement = dataSource.getConnection().createStatement();
		
	}
}
