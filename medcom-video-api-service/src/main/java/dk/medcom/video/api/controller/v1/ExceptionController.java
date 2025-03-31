package dk.medcom.video.api.controller.v1;

import dk.medcom.video.api.api.ApiError;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiError notAcceptableException(NotAcceptableException ex, HttpServletRequest req) {
        String path = req.getRequestURI();
        int errorCode = ex.getErrorCode();
        String errorText = ex.getErrorText();
        return new ApiError(HttpStatus.NOT_ACCEPTABLE, path, errorCode, errorText);
    }

    @ExceptionHandler(NotValidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError notValidDataException(NotValidDataException ex, HttpServletRequest req){
        String path = req.getRequestURI();
        int errorCode = ex.getErrorCode();
        String errorText = ex.getErrorText();
        return new ApiError(HttpStatus.BAD_REQUEST, path, errorCode, errorText);
    }
}
