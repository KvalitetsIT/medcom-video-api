package dk.medcom.video.api.service;

import dk.medcom.video.api.organisation.OrganisationTree;

import java.util.Optional;

public class OrganisationFinder {
    public OrganisationTree findPoolOrganisation(String code, OrganisationTree organistionTree) {
        var organisation = findOrganisation(code, organistionTree);
        if(organisation.getPoolSize() != 0) {
            return organisation;
        }

        var parent = findParent(code, organistionTree);
        while(parent.isPresent()) {
            if(parent.get().getPoolSize() != 0) {
                return parent.get();
            }
            else {
                parent = findParent(parent.get().getCode(), organistionTree);
            }
        }

        return organistionTree;
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

    private Optional<OrganisationTree> findParent(String code, OrganisationTree organisationTree) {
        if(organisationTree.getChildren().stream().anyMatch(x -> code.equals(x.getCode()))) {
            return Optional.of(organisationTree);
        }

        var children = organisationTree.getChildren();
        Optional<OrganisationTree> result = Optional.empty();
        for(OrganisationTree child : children) {
            result = findParent(code, child);
        }

        return result;
    }
}
