package dk.medcom.video.api.interceptor;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.organisation.OrganisationServiceClient;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.dao.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class OrganisationInterceptor extends HandlerInterceptorAdapter {
    private final OrganisationStrategy organisationFacade;
    private final OrganisationRepository organisationRepository;
    private final OrganisationServiceClient organisationServiceClient;

    @Autowired
    private UserContextService userContextService;

    public OrganisationInterceptor(OrganisationStrategy organisationFacade,
                                   OrganisationRepository organisationRepository,
                                   OrganisationServiceClient organisationServiceClient) {
        this.organisationFacade = organisationFacade;
        this.organisationRepository = organisationRepository;
        this.organisationServiceClient = organisationServiceClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var userContext = userContextService.getUserContext();

        var autoCreateOrganisation = userContext.getAutoCreateOrganisation();
        var organisationCode = userContext.getUserOrganisation();

        Organisation organisation = organisationFacade.findOrganisationByCode(organisationCode);

        if(organisation == null && autoCreateOrganisation.isPresent()) {
            var organisationToCreate = new Organisation();
            organisationToCreate.setCode(organisationCode);
            organisation = organisationServiceClient.createOrganisation(autoCreateOrganisation.get(), organisationToCreate);
        }

        if(organisation != null) {
            ensureCreatedLocally(organisation);
        }

        return true;
    }

    private void ensureCreatedLocally(Organisation organisation) {
        dk.medcom.video.api.dao.entity.Organisation dbOrganisation = organisationRepository.findByOrganisationId(organisation.getCode());

        if(dbOrganisation == null) {
            dbOrganisation = new dk.medcom.video.api.dao.entity.Organisation();
            dbOrganisation.setOrganisationId(organisation.getCode());
            organisationRepository.save(dbOrganisation);
        }
    }
}
