package dk.medcom.video.api.service;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NewProvisionerOrganisationFilterImplTest {
    private NewProvisionerOrganisationFilterImpl organisationFilter;
    private ArrayList<String> organisations;

    @Before
    public void setup() {
        organisations = new ArrayList<>();
        organisationFilter = new NewProvisionerOrganisationFilterImpl(organisations);
    }

    @Test
    public void testAllNewProvisioner() {
        var result = organisationFilter.newProvisioner("some_org");

        assertTrue(result);
    }

    @Test
    public void testUseNewProvisioner() {
        organisations.add("use_new");

        var result = organisationFilter.newProvisioner("use_new");

        assertTrue(result);
    }

    @Test
    public void testDoNotUseNewProvisioner() {
        organisations.add("use_new");

        var result = organisationFilter.newProvisioner("some_other_org");

        assertFalse(result);
    }
}
