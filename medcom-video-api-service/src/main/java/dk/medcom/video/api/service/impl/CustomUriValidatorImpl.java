package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.controller.exceptions.NotValidDataErrors;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.service.CustomUriValidator;

public class CustomUriValidatorImpl implements CustomUriValidator {
    @Override
    public void validate(String uri) throws NotValidDataException {
        if(!uri.matches("[a-zA-Z0-9]+")) {
            throw new NotValidDataException(NotValidDataErrors.URI_IS_INVALID, uri);
        }
    }
}
