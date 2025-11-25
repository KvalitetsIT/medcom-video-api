package dk.medcom.video.api.controller.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RessourceNotFoundExceptionTest {
	
	RessourceNotFoundException subject;
	
	@BeforeEach
	public void setup() {
		subject = new RessourceNotFoundException("Test1", "Test2");
	}

	@Test
	public void testRessourceNotFoundException() {
		// Given
		
		// When
		
		// Then
		assertNotNull(subject);
        assertNull(subject.getMessage());
		assertEquals("Test1", subject.getRessource());
		assertEquals("Test2", subject.getField());

	}
	
	@Test
	public void testRessourceNotFoundExceptionUpdated() {
		// Given
		subject.setRessource("Ressource1");
		subject.setField("Field1");
		
		// When
		
		// Then
		assertNotNull(subject);
        assertNull(subject.getMessage());
		assertEquals("Ressource1", subject.getRessource());
		assertEquals("Field1", subject.getField());

	}


}
