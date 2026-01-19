package dk.medcom.video.api.service.exception;

public class ResourceNotFoundExceptionV2 extends RuntimeException {
    public ResourceNotFoundExceptionV2(String resource, String field) {
        super("Resource: %s in field: %s not found.".formatted(resource, field));
    }
}
