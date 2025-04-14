package dk.medcom.video.api.integrationtest.v2.helper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class HeaderBuilder {

    public static String getJwtAllScopes(String keycloakUrl) {
        return getJwt(keycloakUrl, "videoapi-all-scopes", "all-scopes-pass");
    }

    public static String getJwtNoScope(String keycloakUrl) {
        return getJwt(keycloakUrl, "videoapi-no-scopes", "no-scopes-pass");
    }

    public static String getJwtNotAdminScope(String keycloakUrl) {
        return getJwt(keycloakUrl, "videoapi-not-admin", "not-admin-pass");
    }

    public static String getJwtOnlyProvisionerScope(String keycloakUrl) {
        return getJwt(keycloakUrl, "videoapi-only-provisioner", "only-provisioner-pass");
    }

    public static String getJwtNotProvisionUserScope(String keycloakUrl) {
        return getJwt(keycloakUrl, "videoapi-not-provisioneruser", "not-provisioneruser-pass");
    }

    public static String getInvalidJwt() {
        //JWT from previous run.
        return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJyT3N0d08zN29HMTVCTTVoa1ppQmo0c2xiM1BrWk01NmFIbEwzbk1ieV9NIn0.eyJleHAiOjE3NDA0ODc4MTIsImlhdCI6MTc0MDQ4NzUxMiwianRpIjoiZDJjMTNhZGQtYWZiYi00OThhLThhZTItZTgzYjNhYTg4YTVlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDozMjkyNi9yZWFsbXMva2V5Y2xvYWt0ZXN0Iiwic3ViIjoiN2Y3YmFkNDEtOWM3NS00YTJlLTkwNjktNDkwYzJjNmFhYmQxIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidmlkZW9hcGktbWFuYWdlciIsInNjb3BlIjoidmlkZW9hcGktbWFuYWdlcjpyZWFkIHZpZGVvYXBpLW1hbmFnZXI6d3JpdGUiLCJvcmdhbmlzYXRpb25faWQiOiJwb29sLXRlc3Qtb3JnIiwiY2xpZW50SG9zdCI6IjE3Mi4yNC4wLjEiLCJjbGllbnRBZGRyZXNzIjoiMTcyLjI0LjAuMSIsImNsaWVudF9pZCI6InZpZGVvYXBpLW1hbmFnZXIiLCJlbWFpbCI6ImV2YUBrbGFrLmRrIn0.IguGlSMcKNwoUJUfH80753ZQ25mCv1yAZiroINaMADg6tW2g2n0wwQrUrGJNN7UzFc3j0XOpLspaF1hOvbHUfj-6xVpCBcjsiUpF_a2lzHoOzZmZeH44vEplCNQs-LARj_VFcL0VbBRbYdaL8BpNiqBbnUfvg7e4B5KcTU416i0YHt_TYimwjA5iEorLNf6uWRPYCy6PUEwhjfVbf5X_2y4C-kqerOqTpPUk9gDhv38IZbs5YC_PvMpolUYrG5rw71rDCQEoSSaCC6TGI16EUTlfbn7hHZyMUV_Ir1aJvV3d1S4XcoCukTCw0z1CmMz40PcJZzHP8bmiIYvM0NVjVw";
    }

    private static String getJwt(String keycloakUrl, String clientId, String clientSecret) {
        var restTemplate = new RestTemplate();
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var map = new LinkedMultiValueMap<>();
        map.put("grant_type", List.of("client_credentials"));
        map.put("client_id", List.of(clientId));
        map.put("client_secret", List.of(clientSecret));

        var request = new HttpEntity<>(map, httpHeaders);

        var token = restTemplate.postForObject(
                keycloakUrl + "/protocol/openid-connect/token",
                request,
                KeyCloakToken.class
        );

        assert token != null;
        return token.accessToken();
    }

    record KeyCloakToken(@JsonProperty("access_token") String accessToken) {
    }
}
