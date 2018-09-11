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
		
		SpringApplication.run(new Object[] { TestApplication.class }, args); //TODO Lene: ??afklar
	}
	
	@PostConstruct
	public void setSchedulingTemplateTestData() throws SQLException  {

		Statement statement;
		statement = dataSource.getConnection().createStatement();
		statement.execute("INSERT INTO scheduling_template (id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, uri_number_range_low, uri_number_range_high) "
					+ "VALUES (1, 22, 'abc', 'test.dk/', 1, 1, 9999, 0, 10000000000000, 99999999999999, 15, 10, 1000, 9991)");


		
	}
}
