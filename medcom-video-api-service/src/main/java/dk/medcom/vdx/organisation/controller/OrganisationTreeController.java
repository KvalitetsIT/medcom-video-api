package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.api.OrganisationTreeDto;
import dk.medcom.vdx.organisation.controller.exceptions.ResourceNotFoundException;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeBuilder;
import dk.medcom.vdx.organisation.service.OrganisationTreeService;
import dk.medcom.video.api.aspect.APISecurityAnnotation;
import dk.medcom.video.api.context.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrganisationTreeController {
    private final static Logger logger = LoggerFactory.getLogger(OrganisationTreeController.class);
    private final OrganisationTreeService organisationTreeService;
    private final OrganisationTreeBuilder organisationTreeBuilder;

    public OrganisationTreeController(OrganisationTreeService organisationTreeService, OrganisationTreeBuilder organisationTreeBuilder) {
        this.organisationTreeService = organisationTreeService;
        this.organisationTreeBuilder = organisationTreeBuilder;
    }

    @APISecurityAnnotation({UserRole.ADMIN})
    @RequestMapping(value = "/services/organisationtree/{code}", method = RequestMethod.GET)
    public OrganisationTreeDto getOrganisationTree(@PathVariable("code")String code)  {
        logger.debug("Enter getOrganisationTree(code: {})", code);
        try {
            List<Organisation> organisations = organisationTreeService.findOrganisations(code).orElseThrow(() -> new ResourceNotFoundException("The code: "+code+" does not identify an organisation"));
            return organisationTreeBuilder.buildOrganisationTree(organisations);
        } finally {
            logger.debug("Done getOrganisationTree(code: {})", code);
        }
    }
}
