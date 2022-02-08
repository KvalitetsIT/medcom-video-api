package dk.medcom.video.api.api;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public class ApiError {
    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final int errorCode;
    private final String errorText;

    public ApiError(HttpStatus httpStatus, String path, int errorCode, String errorText) {
        this.timestamp = Instant.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.path = path;
        this.errorCode = errorCode;
        this.errorText = errorText;
        this.message = this.errorText;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorText() {
        return errorText;
    }
}
