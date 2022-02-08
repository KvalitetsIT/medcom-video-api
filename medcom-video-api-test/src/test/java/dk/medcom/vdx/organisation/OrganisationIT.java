package dk.medcom.vdx.organisation;

import dk.medcom.video.api.test.IntegrationWithOrganisationServiceTest;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.OrganisationApi;
import io.swagger.client.model.OrganisationUri;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrganisationIT extends IntegrationWithOrganisationServiceTest {
    private OrganisationApi sut;

    @Before
    public void setupApiClient() {
        var organisationApiClient = new ApiClient()
                .setBasePath(String.format("http://%s:%s/api/", videoApi.getContainerIpAddress(), videoApiPort))
                .setOffsetDateTimeFormat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss X"));
        sut = new OrganisationApi(organisationApiClient);
    }

    @Test
    public void testOrganisationByUri_StatusPROVISIONED_OK() throws ApiException {
        // Given
        List<String> uris = new ArrayList<>();
        uris.add("1239@test.dk");

        // When
        OrganisationUri organisations = sut.servicesOrganisationUriPost(uris);

        // Then
        Assert.assertFalse(organisations.isEmpty());
        Assert.assertEquals("pool-test-org", organisations.get(0).getCode());
        Assert.assertEquals("company name another-test-org", organisations.get(0).getName());
        Assert.assertEquals(Long.valueOf(7), organisations.get(0).getGroupId());
        Assert.assertEquals(uris.get(0), organisations.get(0).getUri());
    }

    @Test
    public void testOrganisationByUri_StatusNotPROVISIONED_OK() throws ApiException {
        // Given
        List<String> uris = new ArrayList<>();
        uris.add("1230@test.dk");

        // When
        OrganisationUri organisations = sut.servicesOrganisationUriPost(uris);

        // Then
        Assert.assertTrue(organisations.isEmpty());
    }

    @Test
    public void testReadOrganisation() throws ApiException {
        var response = sut.servicesOrganisationCodeGet("test-org");

        assertNotNull(response);
        assertEquals("test-org", response.getCode());
        assertEquals("company name test-org", response.getName());
        assertEquals("MinAfsender", response.getSmsSenderName());
    }

    @Test
    public void testReadOrganisationNoSmsSenderName() throws ApiException {
        var response = sut.servicesOrganisationCodeGet("kvak");

        assertNotNull(response);
        assertEquals("kvak", response.getCode());
        assertEquals("company name kvak", response.getName());
        assertNull(response.getSmsSenderName());
    }
}
