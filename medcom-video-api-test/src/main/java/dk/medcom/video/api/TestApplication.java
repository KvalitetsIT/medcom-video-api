package dk.medcom.video.api;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Consumer;

@EnableAutoConfiguration
@Configuration
@ComponentScan({ "dk.medcom.video.api.test", "dk.medcom.video.api.configuration"})
public class TestApplication extends SpringBootServletInitializer {
	private DataSource dataSource;

	public static void main(String[] args) {
        Network n = Network.newNetwork();
		MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.5")
                .withDatabaseName("videodb")
                .withUsername("videouser")
                .withPassword("secret1234")
                .withNetwork(n);

        mysql.start();
        String jdbcUrl = mysql.getJdbcUrl();
        System.setProperty("jdbc.url", jdbcUrl);

        int phpMyAdminPort = 8123;
        int phpMyAdminContainerPort = 80;
        Consumer<CreateContainerCmd> cmd = e -> e.withPortBindings(new PortBinding(Ports.Binding.bindPort(phpMyAdminPort), new ExposedPort(phpMyAdminContainerPort)));

        System.out.println("------------------------");
        System.out.println(mysql.getNetworkAliases().get(0));

        HashMap<String, String> environmentMap = new HashMap<>();
        environmentMap.put("PMA_HOST", (String) mysql.getNetworkAliases().get(0));
        environmentMap.put("PMA_USER", "root");
        environmentMap.put("PMA_PASSWORD", "test");
        GenericContainer phpMyAdmin = new GenericContainer<>("phpmyadmin/phpmyadmin:latest").
                withEnv(environmentMap).
                withNetwork(n).
                withCreateContainerCmdModifier(cmd);
        phpMyAdmin.start();

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
