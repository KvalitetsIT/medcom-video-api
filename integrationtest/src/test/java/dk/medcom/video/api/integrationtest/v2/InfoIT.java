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
    private final InfoV2Api infoV2Api;
    private final InfoV2Api infoV2ApiNoRoleAtt;
    private final InfoV2Api infoV2ApiNoHeader;
    private final InfoV2Api infoV2ApiExpiredJwt;
    private final InfoV2Api infoV2ApiInvalidIssuerJwt;
    private final InfoV2Api infoV2ApiTamperedJwt;
    private final InfoV2Api infoV2ApiMissingSignatureJwt;
    private final InfoV2Api infoV2ApiDifferentSignedJwt;
    
    InfoIT() {
        var keycloakUrl = getKeycloakUrl();

        infoV2Api = createClient(HeaderBuilder.getJwtAllRoleAtt(keycloakUrl));
        infoV2ApiNoRoleAtt = createClient(HeaderBuilder.getJwtNoRoleAtt(keycloakUrl));
        infoV2ApiNoHeader = createClient(null);
        infoV2ApiExpiredJwt = createClient(HeaderBuilder.getExpiredJwt(keycloakUrl));
        infoV2ApiInvalidIssuerJwt = createClient(HeaderBuilder.getInvalidIssuerJwt());
        infoV2ApiTamperedJwt = createClient(HeaderBuilder.getTamperedJwt(keycloakUrl));
        infoV2ApiMissingSignatureJwt = createClient(HeaderBuilder.getMissingSignatureJwt(keycloakUrl));
        infoV2ApiDifferentSignedJwt = createClient(HeaderBuilder.getDifferentSignedJwt(keycloakUrl));
    }

    private InfoV2Api createClient(String token) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        if (token != null) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + token);
        }
        return new InfoV2Api(apiClient);
    }

    // --------- JWT errors ------
    @Test
    void errorIfNoJwtToken_v2InfoGet() {
        assertStatus(401, infoV2ApiNoHeader::v2InfoGet);
    }

    @Test
    void errorIfNoRoleAttInToken_v2InfoGet() {
        assertStatus(401, infoV2ApiNoRoleAtt::v2InfoGet);
    }

    @Test
    void errorIfExpiredJwtToken_v2InfoGet() {
        assertStatus(401, infoV2ApiExpiredJwt::v2InfoGet);
    }

    @Test
    void errorIfInvalidIssuerJwtToken_v2InfoGet() {
        assertStatus(401, infoV2ApiInvalidIssuerJwt::v2InfoGet);
    }

    @Test
    void errorIfTamperedJwtToken_v2InfoGet() {
        assertStatus(401, infoV2ApiTamperedJwt::v2InfoGet);
    }

    @Test
    void errorIfMissingSignatureJwtToken_v2InfoGet() {
        assertStatus(401, infoV2ApiMissingSignatureJwt::v2InfoGet);
    }

    @Test
    void errorIfDifferentSignedJwtToken_v2InfoGet() {
        assertStatus(401, infoV2ApiDifferentSignedJwt::v2InfoGet);
    }

    // --------- No JWT errors --------
    @Test
    void testV2InfoGet() throws ApiException {
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
}
