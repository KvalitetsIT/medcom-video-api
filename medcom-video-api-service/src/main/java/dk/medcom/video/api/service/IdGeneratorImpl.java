package dk.medcom.video.api.service;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class IdGeneratorImpl implements IdGenerator {
    @Override
    public String generateId(UUID uuid) {
        String sha256hex = DigestUtils.sha256Hex(uuid.toString());

        return sha256hex.substring(0, 8);
    }
}
