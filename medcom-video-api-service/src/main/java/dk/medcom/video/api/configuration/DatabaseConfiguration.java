package dk.medcom.video.api.configuration;

import dk.medcom.video.api.dao.*;
import dk.medcom.video.api.dao.EntitiesIvrThemeDaoImpl;
import dk.medcom.video.api.dao.PoolHistoryDaoImpl;
import dk.medcom.video.api.dao.PoolInfoRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

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
	public PoolHistoryDao poolHistoryDao(DataSource dataSource) {
		return new PoolHistoryDaoImpl(dataSource);
	}

	@Bean
	public PoolInfoRepository poolInfoRepository(DataSource dataSource) {
		return new PoolInfoRepositoryImpl(dataSource);
	}

	@Bean
	public EntitiesIvrThemeDao entitiesIvrThemeDao(DataSource dataSource) {
		return new EntitiesIvrThemeDaoImpl(dataSource);
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
