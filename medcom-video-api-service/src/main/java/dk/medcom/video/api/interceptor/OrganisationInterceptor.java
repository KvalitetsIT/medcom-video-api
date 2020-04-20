package dk.medcom.video.api.interceptor;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.organisation.Organisation;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class OrganisationInterceptor extends HandlerInterceptorAdapter {
    private final OrganisationStrategy organisationFacade;
    private final OrganisationRepository organisationRepository;

    @Autowired
    private UserContextService userContextService;

    public OrganisationInterceptor(OrganisationStrategy organisationFacade, OrganisationRepository organisationRepository) {
        this.organisationFacade = organisationFacade;
        this.organisationRepository = organisationRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String organisationCode = userContextService.getUserContext().getUserOrganisation();

        Organisation organisation = organisationFacade.findOrganisationByCode(organisationCode);

        if(organisation != null) {
            ensureCreatedLocally(organisation);
        }

        return true;
    }

    private void ensureCreatedLocally(Organisation organisation) {
        dk.medcom.video.api.dao.Organisation dbOrganisation = organisationRepository.findByOrganisationId(organisation.getCode());

        if(dbOrganisation == null) {
            dbOrganisation = new dk.medcom.video.api.dao.Organisation();
            dbOrganisation.setOrganisationId(organisation.getCode());
            organisationRepository.save(dbOrganisation);
        }
    }
}
