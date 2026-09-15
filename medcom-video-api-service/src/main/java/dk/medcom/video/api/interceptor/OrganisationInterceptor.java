package dk.medcom.video.api.interceptor;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.organisation.OrganisationServiceClient;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OrganisationInterceptor implements HandlerInterceptor {
    private final OrganisationStrategy organisationFacade;
    private final OrganisationServiceClient organisationServiceClient;

    @Autowired
    private UserContextService userContextService;

    public OrganisationInterceptor(OrganisationStrategy organisationFacade,
                                   OrganisationServiceClient organisationServiceClient) {
        this.organisationFacade = organisationFacade;
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
            organisationServiceClient.createOrganisation(autoCreateOrganisation.get(), organisationToCreate);
        }

        return true;
    }
}
