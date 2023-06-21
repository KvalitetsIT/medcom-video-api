package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.service.IdGeneratorImpl;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IdGeneratorImplTest {
    @Test
    public void testGenerate() {
        var input = UUID.randomUUID();

        var generator = new IdGeneratorImpl();
        var result = generator.generateId(input);

        assertNotNull(result);
        assertEquals(result, 12, result.length());
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
