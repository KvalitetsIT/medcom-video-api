package dk.medcom.video.api.service;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.entity.Organisation;

public interface OrganisationService {
    Organisation getUserOrganisation() throws PermissionDeniedException;

    Integer getPoolSizeForOrganisation(String organisationId);

    Integer getPoolSizeForUserOrganisation();

    boolean isPolicyServerEnabledForOrganisation(String organisationId);

    boolean isPolicyServerEnabledForUserOrganisation();
}
