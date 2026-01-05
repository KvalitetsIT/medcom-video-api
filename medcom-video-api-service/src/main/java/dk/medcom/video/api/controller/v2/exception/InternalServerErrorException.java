package dk.medcom.video.api.controller.v2.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends AbstractBasicApiException {
    public InternalServerErrorException(String errorMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }
}
