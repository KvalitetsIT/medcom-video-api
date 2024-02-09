package dk.medcom.video.api.service.exception;

public class MessagingException extends RuntimeException {
    public MessagingException(String message, Exception e) {
        super(message, e);
    }
}
