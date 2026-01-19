package dk.medcom.video.api.controller.v2.exception;

import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;

public class NotAcceptableException extends AbstractApiException {
    public NotAcceptableException(DetailedError.DetailedErrorCodeEnum detailedErrorCodeEnum, String detailedErrorCode) {
        super(HttpStatus.NOT_ACCEPTABLE, detailedErrorCodeEnum, detailedErrorCode);
    }
}
