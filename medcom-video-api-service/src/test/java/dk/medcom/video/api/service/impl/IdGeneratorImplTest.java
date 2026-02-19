package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.service.IdGeneratorImpl;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class IdGeneratorImplTest {
    @Test
    public void testGenerate() {
        var input = UUID.randomUUID();

        var generator = new IdGeneratorImpl();
        var result = generator.generateId(input);

        assertNotNull(result);
        assertEquals(12, result.length(), result);
    }

    @Test
    public void testGenerateSameResult() {
        var input = UUID.randomUUID();

        var generator = new IdGeneratorImpl();
        var firstResult = generator.generateId(input);
        var secondResult = generator.generateId(input);

        assertNotNull(firstResult);
        assertNotNull(secondResult);
        assertEquals(firstResult, secondResult);
    }
}
