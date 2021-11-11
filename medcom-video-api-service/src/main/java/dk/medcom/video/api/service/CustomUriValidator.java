package dk.medcom.video.api.service;

import dk.medcom.video.api.controller.exceptions.NotValidDataException;

public interface CustomUriValidator {
    void validate(String uri) throws NotValidDataException;
}
