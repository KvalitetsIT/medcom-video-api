package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.organisation.OrganisationTree;

import java.util.Optional;

public class OrganisationFinder {
    public OrganisationTree findPoolOrganisation(String code, OrganisationTree organisationTree) {
        var organisation = findOrganisation(code, organisationTree);
        if(organisation.getPoolSize() != 0) {
            return organisation;
        }

        var parent = findParentOrganisation(code, organisationTree);
        while(parent.isPresent()) {
            if(parent.get().getPoolSize() != 0) {
                return parent.get();
            }
            else {
                parent = findParentOrganisation(parent.get().getCode(), organisationTree);
            }
        }

        return organisationTree;
    }

    private OrganisationTree findOrganisation(String code, OrganisationTree organisationTree) {
        if(code.equals(organisationTree.getCode())) {
            return organisationTree;
        }

        var children = organisationTree.getChildren();
        OrganisationTree result = null;
        for (OrganisationTree organisation : children) {
            result =  findOrganisation(code, organisation);
        }

        return result;
    }

    public Optional<OrganisationTree> findParentOrganisation(String code, OrganisationTree organisationTree) {
        if(organisationTree.getChildren().stream().anyMatch(x -> code.equals(x.getCode()))) {
            return Optional.of(organisationTree);
        }

        var children = organisationTree.getChildren();
        Optional<OrganisationTree> result = Optional.empty();
        for(OrganisationTree child : children) {
            result = findParentOrganisation(code, child);
        }

        return result;
    }

}
