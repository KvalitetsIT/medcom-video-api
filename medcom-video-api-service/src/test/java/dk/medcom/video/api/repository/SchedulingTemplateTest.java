package dk.medcom.video.api.repository;

import java.sql.SQLException;
import java.sql.Statement;

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
import dk.medcom.video.api.dao.SchedulingTemplate;;

@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("test.properties")
@ContextConfiguration(
  classes = { TestConfiguration.class, DatabaseConfiguration.class }, 
  loader = AnnotationConfigContextLoader.class)
@Transactional
public class SchedulingTemplateTest {

	@ClassRule
	public static MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5").withDatabaseName("videodb").withUsername("videouser").withPassword("secret1234");

	@Resource
    private SchedulingTemplateRepository subject;
	
	@Autowired
	private DataSource dataSource;

	private static boolean testDataInitialised = false;
	
	@BeforeClass
	public static void setupMySqlJdbcUrl() {
		String jdbcUrl = mysql.getJdbcUrl();
		System.setProperty("jdbc.url", jdbcUrl);
	}
	
	@Before
	public void setupTestData() throws SQLException {

		if (!testDataInitialised) {
			Statement statement = dataSource.getConnection().createStatement();
			statement.execute("INSERT INTO scheduling_template (id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, uri_number_range_low, uri_number_range_high) "
					+ "VALUES (1, 22, 'abc', 'test.dk/', 1, 1, 91, 0, 100, 991, 15, 10, 1000, 9991)");
			statement.execute("INSERT INTO scheduling_template (id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, uri_number_range_low, uri_number_range_high) "
					+ "VALUES (2, 33, 'def', 'test2.dk/', 0, 2, 92, 1, 102, 992, 30, 12, 1002, 9992)");

			testDataInitialised = true;
		}
	}
	
	@Test
	public void testSchedulingTemplate() {
		
		// Given
		Long conferencingSysId = 7L; //
		String uriPrefix = "abcd";
		String uriDomain = "test7.dk"; //
		boolean hostPinRequired = true; //
		Long hostPinRangeLow = 7L;
		Long hostPinRangeHigh = 97L;
		boolean guestPinRequired = false; //
		Long guestPinRangeLow = 107L;
		Long guestPinRangeHigh = 997L;
		int vMRAvailableBefore = 10; //
		int maxParticipants = 17;
		Long uriNumberRangeLow = 1007L;
		Long uriNumberRangeHigh = 9997L;
		
		
		SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
		schedulingTemplate.setConferencingSysId(conferencingSysId);
		schedulingTemplate.setUriPrefix(uriPrefix);
		schedulingTemplate.setUriDomain(uriDomain);
		schedulingTemplate.setHostPinRequired(hostPinRequired);
		schedulingTemplate.setHostPinRangeLow(hostPinRangeLow);
		schedulingTemplate.setHostPinRangeHigh(hostPinRangeHigh);
		schedulingTemplate.setGuestPinRequired(guestPinRequired);
		schedulingTemplate.setGuestPinRangeLow(guestPinRangeLow);
		schedulingTemplate.setGuestPinRangeHigh(guestPinRangeHigh);
		schedulingTemplate.setVMRAvailableBefore(vMRAvailableBefore);
		schedulingTemplate.setMaxParticipants(maxParticipants);
		schedulingTemplate.setUriNumberRangeLow(uriNumberRangeLow);
		schedulingTemplate.setUriNumberRangeHigh(uriNumberRangeHigh);
		
		// When
		schedulingTemplate = subject.save(schedulingTemplate);
		
		// Then
		Assert.assertNotNull(schedulingTemplate);
		Assert.assertNotNull(schedulingTemplate.getId());
		Assert.assertEquals(conferencingSysId, schedulingTemplate.getConferencingSysId());
		Assert.assertEquals(uriPrefix, schedulingTemplate.getUriPrefix());
		Assert.assertEquals(uriDomain, schedulingTemplate.getUriDomain());
		Assert.assertEquals(hostPinRequired, schedulingTemplate.getHostPinRequired());
		Assert.assertEquals(hostPinRangeLow, schedulingTemplate.getHostPinRangeLow());
		Assert.assertEquals(hostPinRangeHigh, schedulingTemplate.getHostPinRangeHigh());
		Assert.assertEquals(guestPinRequired, schedulingTemplate.getGuestPinRequired());
		Assert.assertEquals(guestPinRangeLow, schedulingTemplate.getGuestPinRangeLow());
		Assert.assertEquals(guestPinRangeHigh, schedulingTemplate.getGuestPinRangeHigh());
		Assert.assertEquals(vMRAvailableBefore, schedulingTemplate.getVMRAvailableBefore());
		Assert.assertEquals(maxParticipants, schedulingTemplate.getMaxParticipants());
		Assert.assertEquals(uriNumberRangeLow, schedulingTemplate.getUriNumberRangeLow());
		Assert.assertEquals(uriNumberRangeHigh, schedulingTemplate.getUriNumberRangeHigh());
				
	}
	
	@Test
	public void testFindAllSchedulingTemplate() {
		// Given
		
		// When
		Iterable<SchedulingTemplate> schedulingTemplates = subject.findAll();
		
		// Then
		Assert.assertNotNull(schedulingTemplates);
		int numberOfSchedulingTemplates = 0;
		for (SchedulingTemplate schedulingTemplate : schedulingTemplates) {
			Assert.assertNotNull(schedulingTemplate);
			numberOfSchedulingTemplates++;
		}
		Assert.assertEquals(2, numberOfSchedulingTemplates);
	}
	
	@Test
	public void testFindSchedulingTemplateWithExistingId() {
		// Given
		Long id = new Long(1);
		
		// When
		SchedulingTemplate schedulingTemplate = subject.findOne(id);
		
		// Then
		Assert.assertNotNull(schedulingTemplate);
		Assert.assertEquals(id, schedulingTemplate.getId());
		Assert.assertEquals(22L, schedulingTemplate.getConferencingSysId().longValue());
		Assert.assertEquals("abc", schedulingTemplate.getUriPrefix());
		Assert.assertEquals("test.dk/", schedulingTemplate.getUriDomain());
		Assert.assertEquals(true, schedulingTemplate.getHostPinRequired());
		Assert.assertEquals(1L, schedulingTemplate.getHostPinRangeLow().longValue());
		Assert.assertEquals(91L, schedulingTemplate.getHostPinRangeHigh().longValue());
		Assert.assertEquals(false, schedulingTemplate.getGuestPinRequired());
		Assert.assertEquals(100L, schedulingTemplate.getGuestPinRangeLow().longValue());
		Assert.assertEquals(991L, schedulingTemplate.getGuestPinRangeHigh().longValue());
		Assert.assertEquals(15, schedulingTemplate.getVMRAvailableBefore());
		Assert.assertEquals(10, schedulingTemplate.getMaxParticipants());
		Assert.assertEquals(1000L, schedulingTemplate.getUriNumberRangeLow().longValue());
		Assert.assertEquals(9991L, schedulingTemplate.getUriNumberRangeHigh().longValue());
	}

	@Test
	public void testFindSchedulingTemplateWithNonExistingId() {
		// Given
		Long id = new Long(1999);
		
		// When
		SchedulingTemplate schedulingTemplate = subject.findOne(id);
		
		// Then
		Assert.assertNull(schedulingTemplate);
	}

}
