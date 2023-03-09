package dk.medcom.video.api;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Consumer;

@EnableAutoConfiguration
@Configuration
@ComponentScan({ "dk.medcom.video.api.test", "dk.medcom.video.api.configuration","dk.medcom.vdx.organisation"})
public class TestApplication extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(TestApplication.class);
    private static GenericContainer testOrganisationFrontend;
    private static GenericContainer natsService;
    private static String natsPath;
    private final DataSource dataSource;

	public static void main(String[] args) {
        Network n = Network.newNetwork();
	    createOrganisationService(n);

        startNats(n);

		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.7")
                .withDatabaseName("videodb")
                .withUsername("videouser")
                .withPassword("secret1234")
                .withNetwork(n);

        mysql.start();
        String jdbcUrl = mysql.getJdbcUrl();
        System.setProperty("jdbc.url", jdbcUrl + "?useSSL=false");
        System.setProperty("organisation.service.endpoint", String.format("http://localhost:%s/services/", testOrganisationFrontend.getMappedPort(80)));
        System.setProperty("short.link.base.url", "http://shortlink");
        System.setProperty("overflow.pool.organisation.id", "overflow");
        System.setProperty("organisationtree.service.endpoint", "http://localhost:8081");

        System.setProperty("audit.nats.url", natsPath);
        System.setProperty("audit.nats.subject", "natsSubject");
        System.setProperty("audit.nats.cluster.id", "test-cluster");
        System.setProperty("audit.nats.client.id", "natsClientId");
//        System.setProperty("audit.nats.disabled", "true");

        int phpMyAdminPort = 8123;
        int phpMyAdminContainerPort = 80;
        Consumer<CreateContainerCmd> cmd = e -> e.withPortBindings(new PortBinding(Ports.Binding.bindPort(phpMyAdminPort), new ExposedPort(phpMyAdminContainerPort)));

        System.out.println("------------------------");
        System.out.println(mysql.getNetworkAliases().get(0));

        HashMap<String, String> environmentMap = new HashMap<>();
        environmentMap.put("PMA_HOST", (String) mysql.getNetworkAliases().get(0));
        environmentMap.put("PMA_USER", "videouser");
        environmentMap.put("PMA_PASSWORD", "secret1234");
        GenericContainer phpMyAdmin = new GenericContainer<>("phpmyadmin/phpmyadmin:latest").
                withEnv(environmentMap).
                withNetwork(n).
                withCreateContainerCmdModifier(cmd);
        phpMyAdmin.start();

        System.setProperty("SERVER_SERVLET_CONTEXT_PATH", "/videoapi");

        System.setProperty("ALLOWED_ORIGINS", "http://allowed");

		SpringApplication.run(TestApplication.class, args);
	}

    private static void startNats(Network n) {
        var natsContainerName = "nats-streaming";
        var natsContainerVersion = "0.19.0";

        natsService = new GenericContainer<>(natsContainerName + ":" + natsContainerVersion)
                .withNetwork(n)
                .withNetworkAliases("nats")
                .withExposedPorts(4222)
                .withExposedPorts(8222)
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Streaming Server is ready.*"))
        ;

        natsService.start();

        natsPath = "nats://" + natsService.getContainerIpAddress() + ":" + natsService.getMappedPort(4222);
        var natsHttpPath = "http://" + natsService.getContainerIpAddress() + ":" + natsService.getMappedPort(8222);
        logger.info("NATS path: " + natsPath);
        logger.info("NATS http path: " + natsHttpPath);
    }

    private static void createOrganisationService(Network n) {
        MySQLContainer organisationMysql = (MySQLContainer) new MySQLContainer("mysql:5.7")
                .withDatabaseName("organisationdb")
                .withUsername("orguser")
                .withPassword("secret1234")
                .withNetwork(n)
                .withNetworkAliases("organisationdb")
                ;

        organisationMysql.start();

        GenericContainer organisationContainer = new GenericContainer("kvalitetsit/medcom-vdx-organisation:0.0.3")
                .withNetwork(n)
                .withNetworkAliases("organisationservice")
                .withEnv("jdbc_url", "jdbc:mysql://organisationdb/organisationdb")
                .withEnv("jdbc_user", "orguser")
                .withEnv("jdbc_pass", "secret1234")
                .withEnv("usercontext_header_name", "X-Test-Auth")
                .withEnv("userattributes_role_key", "UserRoles")
                .withEnv("userattributes_org_key", "organisation")
                .withEnv("userrole_admin_values", "adminrole")
                .withEnv("userrole_user_values", "userrole1,userrole2")
                .withEnv("userrole_monitor_values", "monitorrole")
                .withEnv("userrole_provisioner_values", "provisionerrole")
                .withEnv("spring.flyway.locations", "classpath:db/migration,filesystem:/app/sql")
                .withClasspathResourceMapping("organisation/V901__organisation_test_data.sql", "/app/sql/V901__organisation_test_data.sql", BindMode.READ_ONLY)
                .withExposedPorts(8080)
                ;

        organisationContainer.start();
        organisationContainer.withLogConsumer(outputFrame -> System.out.println(outputFrame));
        testOrganisationFrontend = new GenericContainer("kvalitetsit/gooioidwsrest:1.1.14")
                .withNetwork(n)
                .withNetworkAliases("organisationfrontend")
                .withCommand("-config", "/caddy/config.json")
                .withClasspathResourceMapping("organisation/caddy.json", "/caddy/config.json", BindMode.READ_ONLY)
                .withExposedPorts(80)
                .waitingFor(Wait.forLogMessage(".*", 1));

        testOrganisationFrontend.start();

    }

    public TestApplication(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void setSchedulingTemplateTestData() throws SQLException  {
		dataSource.getConnection().createStatement();
	}
}
