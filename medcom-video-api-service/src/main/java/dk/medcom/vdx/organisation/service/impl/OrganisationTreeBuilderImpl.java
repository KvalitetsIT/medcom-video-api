package dk.medcom.vdx.organisation.service.impl;

import dk.medcom.vdx.organisation.api.OrganisationTreeDto;
import dk.medcom.vdx.organisation.dao.entity.Organisation;
import dk.medcom.vdx.organisation.service.OrganisationTreeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganisationTreeBuilderImpl implements OrganisationTreeBuilder {
    public OrganisationTreeDto buildOrganisationTree(List<Organisation> organisationList) {
        if (organisationList == null || organisationList.isEmpty()) {
            return null;
        }

        Long rootGroupId = null;
        Map<Long, OrganisationTreeDto> treeMap = new HashMap<>();
        for (Organisation organisation : organisationList) {
            OrganisationTreeDto tree = mapOrganisationTree(organisation);
            treeMap.merge(organisation.getGroupId(), tree, this::merge);

            OrganisationTreeDto parentOrganisationTreeDto;
            if(organisation.getParentId() != null) {
                parentOrganisationTreeDto = treeMap.getOrDefault(organisation.getParentId(), new OrganisationTreeDto());
                parentOrganisationTreeDto.getChildren().add(tree);
                treeMap.put(organisation.getParentId(), parentOrganisationTreeDto);
            }
            else {
                rootGroupId = organisation.getGroupId();
            }
        }

        return treeMap.get(rootGroupId);
    }

    private OrganisationTreeDto merge(OrganisationTreeDto existingOrganisation, OrganisationTreeDto organisation) {
        organisation.getChildren().addAll(existingOrganisation.getChildren());

        return organisation;
    }

    private OrganisationTreeDto mapOrganisationTree(Organisation organisation) {
        OrganisationTreeDto organisationTreeDto = new OrganisationTreeDto();
        organisationTreeDto.setCode(organisation.getOrganisationId() != null ? organisation.getOrganisationId() : organisation.getGroupId().toString());
        organisationTreeDto.setName(organisation.getOrganisationName() != null ? organisation.getOrganisationName() : organisation.getGroupName());
        organisationTreeDto.setPoolSize(organisation.getPoolSize() != null ? organisation.getPoolSize() : 0);

        return organisationTreeDto;
    }
}
