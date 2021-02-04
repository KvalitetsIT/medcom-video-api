package dk.medcom.video.api.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;

@Configuration
@ComponentScan("dk.medcom.video.api.dao.impl")
@EnableAutoConfiguration
@EntityScan(basePackages = { "dk.medcom.video.api.dao" })
@EnableJpaRepositories(basePackageClasses = {MeetingRepository.class, MeetingUserRepository.class, SchedulingTemplateRepository.class, OrganisationRepository.class, SchedulingInfoRepository.class})
@PropertySource("db.properties")
@EnableTransactionManagement
public class DatabaseConfiguration {

	@Value("${jdbc.driverClassName}")
	private String jdbcDriverClassName;

	@Value("${jdbc.url}")
	private String jdbcUrl;

	@Value("${jdbc.user}")
	private String jdbcUser;

	@Value("${jdbc.pass}")
	private String jdbcPass;

	@Bean
	@ConditionalOnProperty(value="baseline.flyway", havingValue = "true")
	public FlywayMigrationStrategy cleanMigrateStrategy() {
		return flyway -> {
			flyway.baseline();
			throw new RuntimeException("Remove baseline.flyway configuration parameter again and start service.");
		};
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(jdbcDriverClassName);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(jdbcUser);
		dataSource.setPassword(jdbcPass);

		return dataSource;
	}
}
