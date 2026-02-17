package dk.medcom.video.api.integrationtest.v2;

import dk.medcom.video.api.integrationtest.AbstractIntegrationTest;
import dk.medcom.video.api.integrationtest.v2.helper.HeaderBuilder;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.PoolV2Api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

    //----------- CORS tests -----------
    @Test
    void testV2PoolGetCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/pool", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://allowed:4100")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var headers = response.headers().map();
        assertTrue(headers.get("Access-Control-Allow-Methods").contains("GET"));
        assertTrue(headers.get("Access-Control-Allow-Origin").contains("http://allowed:4100"));
        assertEquals(200, response.statusCode());
    }

    @Test
    void testV2PoolGetCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/pool", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }
}
