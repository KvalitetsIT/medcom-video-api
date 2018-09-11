package dk.medcom.video.api.repository;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private DataSource dataSource;
	
	private static boolean testDataInitialised = false;


	@BeforeClass
	public static void setupMySqlJdbcUrl() {
		//TODO tjek warning "resource leak"
		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234");
		mysql.start();
		String jdbcUrl = mysql.getJdbcUrl();
		System.setProperty("jdbc.url", jdbcUrl);
	}
	
	@Before
	public void setupTestData() throws SQLException {

		if (!testDataInitialised) {
			Statement statement = dataSource.getConnection().createStatement();
			
			//* meeting_users *
			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (101,  'test-org', 'me@me101.dk')");
			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (102,  'another-test-org', 'me@me102.dk')");
			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (103,  'test-org', 'me@me103.dk')");
			
			// * meetings *						
			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) "
					+ "VALUES (1, uuid(), 'TestMeeting-xyz', 'test-org', 101, '2018-10-02 15:00:00', '2018-10-02 16:00:00', 'Mødebeskrivelse 1')");
			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) "
					+ "VALUES (2, uuid(), 'MyMeeting', 'another-test-org', 102, '2018-11-02 15:00:00', '2018-11-02 16:00:00', 'Mødebeskrivelse 2')");
			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) "
					+ "VALUES (3, '7cc82183-0d47-439a-a00c-38f7a5a01fce', 'TestMeeting-123', 'test-org', 101,  '2018-12-02 15:00:00', '2018-12-02 16:00:00', 'Mødebeskrivelse 3')");

			// * scheduling_info *
			//TODO Lene: gafl uuid fra anden tabel
			//statement.execute("INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, max_participants, meetings_id) VALUES (201, 'a', 1001, 2001, 15, 10, 1)");
			statement.execute("INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, max_participants, meetings_id) "
					+ "VALUES (201, (select uuid from meetings where id = 1) , 1001, 2001, 15, 10, 1)");
			statement.execute("INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, max_participants, meetings_id) "
					+ "VALUES (202, (select uuid from meetings where id = 2), 1001, 2001, 20, 10, 2)");
			statement.execute("INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, max_participants, meetings_id) "
					+ "VALUES (203, (select uuid from meetings where id = 3), 1001, 2001, 30, 10, 3)");

			// *organisation *
			statement.execute("INSERT INTO organisation (id, organisation_id, name) VALUES (1, 'company 1', 'company name 1')");
			statement.execute("INSERT INTO organisation (id, organisation_id, name) VALUES (2, 'company 2', 'company name 2')");
			statement.execute("INSERT INTO organisation (id, organisation_id, name) VALUES (3, 'company 3', 'company name 3')");

			// * scheduling_template * 			
			statement.execute("INSERT INTO scheduling_template (id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, uri_number_range_low, uri_number_range_high) "
					+ "VALUES (1, 22, 'abc', 'test.dk/', 1, 1, 91, 0, 100, 991, 15, 10, 1000, 9991)");
			statement.execute("INSERT INTO scheduling_template (id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, uri_number_range_low, uri_number_range_high) "
					+ "VALUES (2, 33, 'def', 'test2.dk/', 0, 2, 92, 1, 102, 992, 30, 12, 1002, 9992)");
	
			testDataInitialised = true;
		}
	}

}
