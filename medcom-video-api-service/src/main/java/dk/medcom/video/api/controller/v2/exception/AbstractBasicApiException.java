package dk.medcom.video.api.controller.v2.exception;

import org.springframework.http.HttpStatus;

public class AbstractBasicApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorMessage;

    public AbstractBasicApiException(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }


    public String getErrorMessage() {
        return errorMessage;
    }
}
