package dk.medcom.video.api.integrationtest.v2;

import dk.medcom.video.api.integrationtest.AbstractIntegrationTest;
import dk.medcom.video.api.integrationtest.v2.helper.HeaderBuilder;
import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.PoolV2Api;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;

public class PoolIT extends AbstractIntegrationTest {

    private final PoolV2Api poolV2Api;
    private final PoolV2Api poolV2ApiNoHeader;
    private final PoolV2Api poolV2ApiInvalidToken;
    private final PoolV2Api poolV2ApiNoRoleAtt;

    public PoolIT() {
        var apiClient = new ApiClient();
        apiClient.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl()));
        apiClient.setBasePath(getApiBasePath());
        poolV2Api = new PoolV2Api(apiClient);

        var apiClientNoHeader = new ApiClient();
        apiClientNoHeader.setBasePath(getApiBasePath());
        poolV2ApiNoHeader = new PoolV2Api(apiClientNoHeader);

        var apiClientInvalidToken = new ApiClient();
        apiClientInvalidToken.setBasePath(getApiBasePath());
        apiClientInvalidToken.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getInvalidJwt());
        apiClientInvalidToken.setBasePath(getApiBasePath());
        poolV2ApiInvalidToken = new PoolV2Api(apiClientInvalidToken);

        var apiClientNoRoleAtt = new ApiClient();
        apiClientNoRoleAtt.setBasePath(getApiBasePath());
        apiClientNoRoleAtt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl()));
        poolV2ApiNoRoleAtt = new PoolV2Api(apiClientNoRoleAtt);
    }

    @Test
    public void errorIfNoJwtToken_v2PoolGetWithHttpInfo() {
        var expectedException = assertThrows(ApiException.class, poolV2ApiNoHeader::v2PoolGetWithHttpInfo);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfInvalidJwtToken_v2PoolGetWithHttpInfo() {
        var expectedException = assertThrows(ApiException.class, poolV2ApiInvalidToken::v2PoolGetWithHttpInfo);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void errorIfNoRoleAttInToken_v2PoolGetWithHttpInfo() {
        var expectedException = assertThrows(ApiException.class, poolV2ApiNoRoleAtt::v2PoolGetWithHttpInfo);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    public void testV2PoolGet() throws ApiException {
        var result = poolV2Api.v2PoolGetWithHttpInfo();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getData());
        // mocked organisation service returns user-org-pool + new-provisioner-org, new-provisioner-org should be removed by filter.
        assertEquals(1, result.getData().size());
        var poolInfoResult = result.getData().getFirst();

        assertEquals("user-org-pool", poolInfoResult.getOrganisationId());
        assertNotNull(poolInfoResult.getAvailablePoolSize());
        assertNotNull(poolInfoResult.getDesiredPoolSize());
        assertEquals(10, poolInfoResult.getDesiredPoolSize(), 0);
        assertNotNull(poolInfoResult.getSchedulingInfoList());
        assertNotNull(poolInfoResult.getSchedulingTemplate());
    }
}
