package dk.medcom.vdx.organisation.configuration;

import dk.medcom.vdx.organisation.dao.OrganisationDao;
import dk.medcom.vdx.organisation.dao.OrganisationViews;
import dk.medcom.vdx.organisation.dao.impl.OrganisationDaoImpl;
import dk.medcom.vdx.organisation.dao.impl.OrganisationViewsImpl;
import dk.medcom.vdx.organisation.service.OrganisationTreeBuilder;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeBuilderImpl;
import dk.medcom.vdx.organisation.service.impl.OrganisationTreeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import javax.sql.DataSource;

@Configuration
public class OrganisationConfiguration implements WebMvcConfigurer {
    @Bean
    public OrganisationTreeService organisationTreeService(OrganisationDao organisationDao) {
        return new OrganisationTreeServiceImpl(organisationDao);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    @Bean
    public OrganisationTreeBuilder organisationTreeBuilder() {
        return new OrganisationTreeBuilderImpl();
    }

    @Bean
    public OrganisationDao organisationDao(DataSource dataSource) {
        return new OrganisationDaoImpl(dataSource);
    }

    @Bean
    public OrganisationViews organisationViews(DataSource dataSource) { return new OrganisationViewsImpl(dataSource); }
}
