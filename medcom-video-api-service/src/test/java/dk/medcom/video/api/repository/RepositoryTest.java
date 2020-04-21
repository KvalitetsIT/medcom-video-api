package dk.medcom.video.api.repository;

import javax.transaction.Transactional;


import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testcontainers.containers.MySQLContainer;

import dk.medcom.video.api.configuration.DatabaseConfiguration;
import dk.medcom.video.api.configuration.TestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("test.properties")
@ContextConfiguration(
  classes = { TestConfiguration.class, DatabaseConfiguration.class }, 
  loader = AnnotationConfigContextLoader.class)
@Transactional
abstract public class RepositoryTest {

//	@Autowired
//	private DataSource dataSource;
//	
//	private static boolean testDataInitialised = false;


	@BeforeClass
	public static void setupMySqlJdbcUrl() {
		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.7").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234");
		mysql.start();
				
		String jdbcUrl = mysql.getJdbcUrl() + "?useSSL=false";
		System.out.println(jdbcUrl);
		System.setProperty("jdbc.url", jdbcUrl);
	}
}
