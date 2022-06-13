package dk.medcom.video.api.service.impl;

public class MessagingException extends RuntimeException {
    public MessagingException(String message, Exception e) {
        super(message, e);
    }
}
