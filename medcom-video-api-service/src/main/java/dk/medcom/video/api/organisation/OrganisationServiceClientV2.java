package dk.medcom.video.api.organisation;

import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.organisation.model.OrganisationSimple;

import java.util.List;

public interface OrganisationServiceClientV2 {
    List<OrganisationSimple> getDescendantsOfOrganisation(String code);

    Organisation getOrganisationByCode(String code);

    Organisation ensureOrganisationExists(String code);
}
