package dk.medcom.video.api.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class PermissionDeniedException extends Exception {
	private static final long serialVersionUID = 1L;
}
