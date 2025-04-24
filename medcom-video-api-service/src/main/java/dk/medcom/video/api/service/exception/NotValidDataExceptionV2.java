package dk.medcom.video.api.service.exception;

import org.openapitools.model.DetailedError;

public class NotValidDataExceptionV2 extends AbstractDetailedException {
    public NotValidDataExceptionV2(DetailedError.DetailedErrorCodeEnum detailedErrorCodeEnum, String detailedErrorCode) {
        super(detailedErrorCodeEnum, detailedErrorCode);
    }
}
