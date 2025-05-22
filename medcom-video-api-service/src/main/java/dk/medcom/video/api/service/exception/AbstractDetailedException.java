package dk.medcom.video.api.service.exception;

import org.openapitools.model.DetailedError;

public class AbstractDetailedException extends RuntimeException {
    private final DetailedError.DetailedErrorCodeEnum detailedErrorCodeEnum;
    private final String detailedError;

    public AbstractDetailedException(DetailedError.DetailedErrorCodeEnum detailedErrorCodeEnum, String detailedErrorCode) {
        this.detailedErrorCodeEnum = detailedErrorCodeEnum;
        this.detailedError = detailedErrorCode;
    }

    public DetailedError.DetailedErrorCodeEnum getDetailedErrorCode() {
        return detailedErrorCodeEnum;
    }

    public String getDetailedError() {
        return detailedError;
    }}
