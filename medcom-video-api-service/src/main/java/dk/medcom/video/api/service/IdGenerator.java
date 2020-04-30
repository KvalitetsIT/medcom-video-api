package dk.medcom.video.api.service;

import java.util.UUID;

interface IdGenerator {
    String generateId(UUID uuid);
}
