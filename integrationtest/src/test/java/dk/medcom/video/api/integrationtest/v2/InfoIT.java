package dk.medcom.video.api.integrationtest.v2;

import dk.medcom.video.api.integrationtest.AbstractIntegrationTest;
import dk.medcom.video.api.integrationtest.v2.helper.HeaderBuilder;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.InfoV2Api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class InfoIT extends AbstractIntegrationTest {

    @Test
    void errorIfNoJwtToken_v2InfoGet() {
        final InfoV2Api infoV2ApiNoHeader = createClient(null);
        assertStatus(401, infoV2ApiNoHeader::v2InfoGet);
    }

    @Test
    void errorIfInvalidJwtToken_v2InfoGet() {
        final InfoV2Api infoV2ApiInvalidToken = createClient(HeaderBuilder.getInvalidJwt());
        assertStatus(401, infoV2ApiInvalidToken::v2InfoGet);
    }

    @Test
    void errorIfNoRoleAttInToken_v2InfoGet() {
        final InfoV2Api infoV2ApiNoRoleAtt = createClient(HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl()));
        assertStatus(401, infoV2ApiNoRoleAtt::v2InfoGet);
    }

    @Test
    void testV2InfoGet() throws ApiException {
        final InfoV2Api infoV2Api = createClient(HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl()));

        var result = infoV2Api.v2InfoGet();

        assertNotNull(result);
        assertNotNull(result.getInfo());
        assertFalse(result.getInfo().isEmpty());
        assertTrue(result.getInfo().stream().anyMatch(x -> x.getKey().equals("git")));
    }

    //----------- CORS tests -----------
    @Test
    void testV2InfoGetCorsAllowed() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/info", getApiBasePath())))
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
    void testV2InfoGetCorsDenied() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(String.format("%s/v2/info", getApiBasePath())))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .header("Origin", "http://denied:4200")
                .header("Access-Control-Request-Method", "GET")
                .build();

        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    private InfoV2Api createClient(String token) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        if (token != null) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + token);
        }
        return new InfoV2Api(apiClient);
    }

}
