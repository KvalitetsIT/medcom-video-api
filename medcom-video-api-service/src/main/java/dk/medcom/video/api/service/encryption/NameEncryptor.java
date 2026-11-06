package dk.medcom.video.api.service.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public class NameEncryptor {

    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public NameEncryptor(String secretKeyBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Name encryption key must be 256 bits (32 bytes) base64 encoded.");
        }
        this.secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    public String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        try {
            var iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            var cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            var cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            var result = ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array();
            return Base64.getEncoder().encodeToString(result);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to encrypt name.", e);
        }
    }

    public String decrypt(String encrypted) {
        if (encrypted == null) {
            return null;
        }
        try {
            var input = Base64.getDecoder().decode(encrypted);

            var cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BITS, input, 0, IV_LENGTH_BYTES));
            var plainText = cipher.doFinal(input, IV_LENGTH_BYTES, input.length - IV_LENGTH_BYTES);

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalStateException("Unable to decrypt name.", e);
        }
    }
}
