package dk.medcom.video.api.service;

import java.util.List;

public class NewProvisionerOrganisationFilterImpl implements NewProvisionerOrganisationFilter {
    private final List<String> organisations;

    public NewProvisionerOrganisationFilterImpl(List<String> organisations) {
        this.organisations = organisations;
    }

    @Override
    public boolean newProvisioner(String organisation) {
        return organisations.isEmpty() || organisations.contains(organisation);
    }
}
