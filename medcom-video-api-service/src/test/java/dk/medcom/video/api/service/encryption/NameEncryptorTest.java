package dk.medcom.video.api.service.encryption;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class NameEncryptorTest {
    private static final String KEY = "dGVzdC1rZXktZm9yLWNpdGl6ZW4tbmFtZS1hZXMyNTY=";

    private final NameEncryptor subject = new NameEncryptor(KEY);

    @Test
    public void testEncryptDecrypt() {
        var encrypted = subject.encrypt("Jens Hænsen");

        assertNotEquals("Jens Hænsen", encrypted);
        assertEquals("Jens Hænsen", subject.decrypt(encrypted));
    }

    @Test
    public void testEncryptUsesRandomIv() {
        assertNotEquals(subject.encrypt("Jens Hansen"), subject.encrypt("Jens Hansen"));
    }

    @Test
    public void testNull() {
        assertNull(subject.encrypt(null));
        assertNull(subject.decrypt(null));
    }

    @Test
    public void testDecryptWithWrongKeyFails() {
        var encrypted = subject.encrypt("Jens Hansen");
        var otherKey = Base64.getEncoder().encodeToString("another-key-for-citizen-name-aes".getBytes());

        assertThrows(IllegalStateException.class, () -> new NameEncryptor(otherKey).decrypt(encrypted));
    }

    @Test
    public void testInvalidKeyLength() {
        var shortKey = Base64.getEncoder().encodeToString("too-short".getBytes());

        assertThrows(IllegalArgumentException.class, () -> new NameEncryptor(shortKey));
    }
}
