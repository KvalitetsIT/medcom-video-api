package dk.medcom.video.api.service;

import java.util.UUID;

public interface IdGenerator {
    String generateId(UUID uuid);
}
