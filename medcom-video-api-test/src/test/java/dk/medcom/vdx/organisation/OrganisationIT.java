package dk.medcom.vdx.organisation;

import dk.medcom.video.api.test.IntegrationWithOrganisationServiceTest;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.OrganisationApi;
import io.swagger.client.model.OrganisationGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OrganisationIT extends IntegrationWithOrganisationServiceTest {
    private OrganisationApi organisationApi;

    @Before
    public void setupApiClient() {
        var organisationApiClient = new ApiClient()
                .setBasePath(String.format("http://%s:%s/api", videoApi.getContainerIpAddress(), videoApiPort))
                .setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));
        organisationApi = new OrganisationApi(organisationApiClient);
    }

    @Test
    public void testOrganisationByUri_StatusPROVISIONED_OK() throws ApiException {

        OrganisationGroup organisation = organisationApi.servicesOrganisationUriUriGet("1239@test.dk");

        Assert.assertNotNull(organisation);
        Assert.assertEquals("pool-test-org", organisation.getCode());
        Assert.assertEquals("company name another-test-org", organisation.getName());
        Assert.assertEquals(Long.valueOf(7), organisation.getGroupId());
    }

    @Test
    public void testOrganisationByUri_StatusNotPROVISIONED_OK() throws ApiException {

        try {
            organisationApi.servicesOrganisationUriUriGet("1230@test.dk");
            fail();
        }
        catch(ApiException e) {
            assertEquals(404, e.getCode());
        }
    }
}
