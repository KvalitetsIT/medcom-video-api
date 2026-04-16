package dk.medcom.video.api.controller.v2.exception;

import org.springframework.http.HttpStatus;

public class PermissionDeniedException extends AbstractBasicApiException {
    public PermissionDeniedException(String errorMessage) {
        super(HttpStatus.FORBIDDEN, errorMessage);
    }
}
