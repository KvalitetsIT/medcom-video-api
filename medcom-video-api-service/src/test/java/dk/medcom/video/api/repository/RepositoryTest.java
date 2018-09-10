package dk.medcom.video.api.repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testcontainers.containers.MySQLContainer;

import dk.medcom.video.api.configuration.DatabaseConfiguration;
import dk.medcom.video.api.configuration.TestConfiguration;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingUser;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("test.properties")
@ContextConfiguration(
  classes = { TestConfiguration.class, DatabaseConfiguration.class }, 
  loader = AnnotationConfigContextLoader.class)
@Transactional
abstract public class RepositoryTest {

	@ClassRule
	public static MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234");

//	@Resource
//    private MeetingRepository subject;
	
//	@Resource
    //private MeetingUserRepository subject2;

//	@Autowired
//	private DataSource dataSource;

	//private static boolean testDataInitialised = false;
	
//	@BeforeClass
//	public static void setupMySqlJdbcUrl() {
//		String jdbcUrl = mysql.getJdbcUrl();
//		System.setProperty("jdbc.url", jdbcUrl);
//	}
	
//	@Before
//	public void setupTestData() throws SQLException {
//
//		if (!testDataInitialised) {
//			Statement statement = dataSource.getConnection().createStatement();
//			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (1,  'test-org', 'me@me1.dk')");
//			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (2,  'another-test-org', 'me@me2.dk')");
//			statement.execute("INSERT INTO meeting_users (id, organisation_id, email) VALUES (3,  'test-org', 'me@me3.dk')");
//			
//			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (1, uuid(), 'TestMeeting-xyz', 'test-org', 1, '2018-10-02 15:00:00', '2018-10-02 16:00:00', 'Mødebeskrivelse 1')");
//			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (2, uuid(), 'MyMeeting', 'another-test-org', 2, '2018-11-02 15:00:00', '2018-11-02 16:00:00', 'Mødebeskrivelse 2')");
//			statement.execute("INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (3, '7cc82183-0d47-439a-a00c-38f7a5a01fce', 'TestMeeting-123', 'test-org', 1,  '2018-12-02 15:00:00', '2018-12-02 16:00:00', 'Mødebeskrivelse 3')");
//			testDataInitialised = true;
//		}
//	}
	

}
