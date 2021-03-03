package dk.medcom.vdx.organisation.service;

import dk.medcom.vdx.organisation.api.OrganisationTreeDto;
import dk.medcom.vdx.organisation.dao.entity.Organisation;

import java.util.List;

public interface OrganisationTreeBuilderService {
    OrganisationTreeDto buildOrganisationTree(List<Organisation> organisationList);
}
