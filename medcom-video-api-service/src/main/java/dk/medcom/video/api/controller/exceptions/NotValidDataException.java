package dk.medcom.video.api.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "testtest")
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NotValidDataException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotValidDataException(String msg) {
		super(msg);
	}
}