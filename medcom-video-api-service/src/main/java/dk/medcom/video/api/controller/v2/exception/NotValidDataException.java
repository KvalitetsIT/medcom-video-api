package dk.medcom.video.api.controller.v2.exception;

import org.openapitools.model.DetailedError;
import org.springframework.http.HttpStatus;

public class NotValidDataException extends AbstractApiException {
    public NotValidDataException(DetailedError.DetailedErrorCodeEnum detailedErrorCodeEnum, String detailedErrorCode) {
        super(HttpStatus.BAD_REQUEST, detailedErrorCodeEnum, detailedErrorCode);
    }
}
