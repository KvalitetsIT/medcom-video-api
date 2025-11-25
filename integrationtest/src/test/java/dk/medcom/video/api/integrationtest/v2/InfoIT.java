package dk.medcom.video.api.integrationtest.v2;

import dk.medcom.video.api.integrationtest.AbstractIntegrationTest;
import dk.medcom.video.api.integrationtest.v2.helper.HeaderBuilder;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.InfoV2Api;

import static org.junit.jupiter.api.Assertions.*;

class InfoIT extends AbstractIntegrationTest {

    private final InfoV2Api infoV2Api;
    private final InfoV2Api infoV2ApiNoHeader;
    private final InfoV2Api infoV2ApiInvalidToken;
    private final InfoV2Api infoV2ApiNoRoleAtt;

    InfoIT() {
        var apiClient = new ApiClient();
        apiClient.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtAllRoleAtt(getKeycloakUrl()));
        apiClient.setBasePath(getApiBasePath());
        infoV2Api = new InfoV2Api(apiClient);

        var apiClientNoHeader = new ApiClient();
        apiClientNoHeader.setBasePath(getApiBasePath());
        infoV2ApiNoHeader = new InfoV2Api(apiClientNoHeader);

        var apiClientInvalidToken = new ApiClient();
        apiClientInvalidToken.setBasePath(getApiBasePath());
        apiClientInvalidToken.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getInvalidJwt());
        apiClientInvalidToken.setBasePath(getApiBasePath());
        infoV2ApiInvalidToken = new InfoV2Api(apiClientInvalidToken);

        var apiClientNoRoleAtt = new ApiClient();
        apiClientNoRoleAtt.addDefaultHeader("Authorization", "Bearer " + HeaderBuilder.getJwtNoRoleAtt(getKeycloakUrl()));
        apiClientNoRoleAtt.setBasePath(getApiBasePath());
        infoV2ApiNoRoleAtt = new InfoV2Api(apiClientNoRoleAtt);
    }

    @Test
    void errorIfNoJwtToken_v2InfoGet() {
        var expectedException = assertThrows(ApiException.class, infoV2ApiNoHeader::v2InfoGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfInvalidJwtToken_v2InfoGet() {
        var expectedException = assertThrows(ApiException.class, infoV2ApiInvalidToken::v2InfoGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void errorIfNoRoleAttInToken_v2InfoGet() {
        var expectedException = assertThrows(ApiException.class, infoV2ApiNoRoleAtt::v2InfoGet);
        assertEquals(401, expectedException.getCode());
    }

    @Test
    void testV2InfoGet() throws ApiException {
        var result = infoV2Api.v2InfoGet();

        assertNotNull(result);
        assertNotNull(result.getInfo());
        assertFalse(result.getInfo().isEmpty());

        assertTrue(result.getInfo().stream().anyMatch(x -> x.getKey().equals("git")));
    }
}
