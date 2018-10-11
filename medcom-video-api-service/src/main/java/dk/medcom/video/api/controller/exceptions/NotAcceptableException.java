package dk.medcom.video.api.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class NotAcceptableException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotAcceptableException(String msg) {
		super(msg);
	}
}