package dk.medcom.video.api.service.exception;

public class KeycloakClientException extends RuntimeException {
    public KeycloakClientException(String message) {
        super(message);
    }
}
