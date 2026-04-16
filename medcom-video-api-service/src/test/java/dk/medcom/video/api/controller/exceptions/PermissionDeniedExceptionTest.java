package dk.medcom.video.api.controller.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PermissionDeniedExceptionTest {
	
	PermissionDeniedException subject;
	
	@BeforeEach
	public void setup() {
		subject = new PermissionDeniedException();
	}

	@Test
	public void testPermissionDeniedException() {
		// Given
		
		// When
		
		// Then
		assertNotNull(subject);
        assertNull(subject.getMessage());

	}

}
