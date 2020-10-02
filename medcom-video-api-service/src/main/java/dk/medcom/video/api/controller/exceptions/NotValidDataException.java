package dk.medcom.video.api.controller.exceptions;

public class NotValidDataException extends Exception {

	private static final long serialVersionUID = 1L;
	private final int errorCode;
	private final String errorText;

	public NotValidDataException(NotValidDataErrors error, String... values) {
		super(error.getErrorText(values));
		errorCode = error.getErrorCode();
		errorText = error.getErrorText(values);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorText() {
		return errorText;
	}
}