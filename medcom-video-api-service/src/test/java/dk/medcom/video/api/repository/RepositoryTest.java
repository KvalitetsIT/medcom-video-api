package dk.medcom.video.api.repository;

import jakarta.transaction.Transactional;


import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import dk.medcom.video.api.configuration.DatabaseConfiguration;
import dk.medcom.video.api.configuration.TestConfiguration;
import org.testcontainers.containers.MariaDBContainer;

import java.util.TimeZone;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("test.properties")
@ContextConfiguration(
  classes = { TestConfiguration.class, DatabaseConfiguration.class }, 
  loader = AnnotationConfigContextLoader.class)
@Transactional
abstract public class RepositoryTest {
	private static boolean initialized = false;

	static {
		// Make sure unit test is running in same timezone as the default one in the container. This is needed when using Connector/J version 8.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@BeforeClass
	public static void setupMariaDbJdbcUrl() {
		if(!initialized) {
			MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:10.6")
					.withDatabaseName("videodb")
					.withUsername("videouser")
					.withPassword("secret1234");
			mariadb.start();

			String jdbcUrl = mariadb.getJdbcUrl() + "?useSSL=false&serverTimeZone=UTC";
			System.setProperty("jdbc.url", jdbcUrl);

			initialized = true;
		}
	}
}
