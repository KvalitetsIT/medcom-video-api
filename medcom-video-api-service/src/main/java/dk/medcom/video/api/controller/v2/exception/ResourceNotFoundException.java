package dk.medcom.video.api.controller.v2.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AbstractBasicApiException {
    public ResourceNotFoundException(String errorMessage) {
        super(HttpStatus.NOT_FOUND, errorMessage);
    }
}
