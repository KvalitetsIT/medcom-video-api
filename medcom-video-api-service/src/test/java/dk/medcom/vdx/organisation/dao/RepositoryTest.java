package dk.medcom.vdx.organisation.dao;

import dk.medcom.video.api.configuration.TestConfiguration;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {dk.medcom.video.api.configuration.DatabaseConfiguration.class, TestConfiguration.class}, loader = AnnotationConfigContextLoader.class)
@Transactional
public abstract class RepositoryTest{
    private static boolean initialized;

    @BeforeClass
    public static void setupMySqlJdbcUrl() {
        if (!initialized) {
            MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.7")
                    .withDatabaseName("videodb")
                    .withUsername("videouser")
                    .withPassword("secret1234");
            mysql.start();

            String jdbcUrl = mysql.getJdbcUrl() + "?useSSL=false";
            System.setProperty("jdbc.url", jdbcUrl);
            System.setProperty("jdbc.user", "videouser");
            System.setProperty("jdbc.pass", "secret1234");

            initialized = true;
        }
    }
}
