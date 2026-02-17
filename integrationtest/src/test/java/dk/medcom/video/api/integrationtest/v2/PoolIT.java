package dk.medcom.video.api.integrationtest.v2;

import dk.medcom.video.api.integrationtest.AbstractIntegrationTest;
import dk.medcom.video.api.integrationtest.v2.helper.HeaderBuilder;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.PoolV2Api;

import static org.junit.jupiter.api.Assertions.*;

class PoolIT extends AbstractIntegrationTest {


    @Test
    void errorIfNoJwtToken_v2PoolGetWithHttpInfo() {
        final PoolV2Api poolV2ApiNoHeader = createClient(null);
        assertStatus(401, poolV2ApiNoHeader::v2PoolGetWithHttpInfo);
    }

    @Test
    void errorIfInvalidJwtToken_v2PoolGetWithHttpInfo() {
        final PoolV2Api poolV2ApiInvalidToken = createClient(HeaderBuilder.getInvalidJwt());
        assertStatus(401, poolV2ApiInvalidToken::v2PoolGetWithHttpInfo);
    }

    @Test
    void errorIfNoRoleAttInToken_v2PoolGetWithHttpInfo() {
        final PoolV2Api poolV2ApiNoRoleAtt = createClient(HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl()));
        assertStatus(401, poolV2ApiNoRoleAtt::v2PoolGetWithHttpInfo);
    }

    @Test
    void testV2PoolGet() throws ApiException {

        var poolV2Api = createClient( HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl()));

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

    private PoolV2Api createClient(String token) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        if (token != null) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + token);
        }
        return new PoolV2Api(apiClient);
    }


}
