package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.controller.v2.exception.AbstractApiException;
import dk.medcom.video.api.controller.v2.exception.AbstractBasicApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.openapitools.model.DetailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;


@ControllerAdvice
public class ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<DetailedError> handleValidationException(ValidationException e, HttpServletRequest request) {
        logger.debug("Handling ValidationException.", e);

        var error = getDetailedBadRequestErrorMessage(request, e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DetailedError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        logger.debug("Handling MethodArgumentNotValidException.", e);

        var error = getDetailedBadRequestErrorMessage(request, validationErrorToString(e));
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AbstractApiException.class)
    public ResponseEntity<DetailedError> handleApiException(AbstractApiException e, HttpServletRequest request) {
        logger.debug("Handling ApiException: {}", e.getHttpStatus());
        var error = new DetailedError()
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .status(e.getHttpStatus().value())
                .error(e.getHttpStatus().getReasonPhrase())
                .detailedErrorCode(e.getDetailedErrorCode())
                .detailedError(e.getDetailedError());

        return ResponseEntity.status(e.getHttpStatus().value()).body(error);
    }

    @ExceptionHandler(AbstractBasicApiException.class)
    public ResponseEntity<DetailedError> handleBasicApiException(AbstractBasicApiException e, HttpServletRequest request) {
        logger.debug("Handling BasicApiException: {}", e.getHttpStatus());
        var error = new DetailedError()
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .status(e.getHttpStatus().value())
                .error(e.getHttpStatus().getReasonPhrase())
                .detailedErrorCode(DetailedError.DetailedErrorCodeEnum._11)
                .detailedError(e.getErrorMessage());

        return ResponseEntity.status(e.getHttpStatus().value()).body(error);
    }

    private DetailedError getDetailedBadRequestErrorMessage(HttpServletRequest request, String detailedErrorMessage) {
        return new DetailedError()
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detailedErrorCode(DetailedError.DetailedErrorCodeEnum._10)
                .detailedError(detailedErrorMessage);
    }

    private String validationErrorToString(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getFieldError();
        return fieldError == null ? e.getMessage()
                : fieldError.getField() +
                ": " + fieldError.getDefaultMessage();
    }
}