package dk.medcom.video.api.service.exception;

import org.openapitools.model.DetailedError;

public class NotAcceptableExceptionV2 extends AbstractDetailedException {
    public NotAcceptableExceptionV2(DetailedError.DetailedErrorCodeEnum detailedErrorCodeEnum, String detailedErrorCode) {
        super(detailedErrorCodeEnum, detailedErrorCode);
    }
}
