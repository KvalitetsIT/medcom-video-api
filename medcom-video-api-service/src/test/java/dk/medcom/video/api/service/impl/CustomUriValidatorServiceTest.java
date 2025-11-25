package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.service.CustomUriValidator;
import dk.medcom.video.api.service.CustomUriValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CustomUriValidatorServiceTest {
    private CustomUriValidator customUriValidator;

    @BeforeEach
    public void setup() {
        customUriValidator = new CustomUriValidatorImpl();
    }

    @Test
    public void testValidData() throws NotValidDataException {
        var input = getValidCharacters();

        customUriValidator.validate(input);
    }

    @Test
    public void testInvalidData() {
        var input = getValidCharacters();
        input += "+";

        try {
            customUriValidator.validate(input);
            fail();
        }
        catch(NotValidDataException e) {
            assertEquals(100, e.getErrorCode());
        }
    }

    @Test
    public void testEmptyUri() {
        var input = "";

        try {
            customUriValidator.validate(input);
            fail();
        }
        catch(NotValidDataException e) {
            assertEquals(100, e.getErrorCode());
        }
    }

    private String getValidCharacters() {
        var chars = new StringBuilder();

        var a = 'A';
        for(int i = 0; i < 26; i++) {
            chars.append(a);
            a++;
        }

        a = 'a';
        for(int i = 0; i < 26; i++) {
            chars.append(a);
            a++;
        }

        a = '0';
        for(int i = 0; i < 10; i++) {
            chars.append(a);
            a++;
        }

        return chars.toString();
    }

}
