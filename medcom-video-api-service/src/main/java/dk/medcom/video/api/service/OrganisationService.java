package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;

public interface OrganisationService {
    Organisation getUserOrganisation() throws PermissionDeniedExceptionV2;

    Organisation getParticipantOrganisation(String participantId);

    Integer getPoolSizeForOrganisation(String organisationId);

    Integer getPoolSizeForUserOrganisation();

    boolean userIsPermittedForOrganisation(String organisationId);
}
