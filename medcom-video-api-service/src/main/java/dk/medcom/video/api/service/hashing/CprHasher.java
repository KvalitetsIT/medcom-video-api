package dk.medcom.video.api.service.hashing;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

public class CprHasher {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final SecretKeySpec secretKey;

    public CprHasher(String secretKeyBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        this.secretKey = new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
    }

    public String hash(String cpr) {
        try {
            var mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);
            var hashBytes = mac.doFinal(cpr.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Unable to hash CPR number.", e);
        }
    }
}
