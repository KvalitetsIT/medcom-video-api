package dk.medcom.video.api.controller.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnauthorizedExceptionTest {
	
	UnauthorizedException subject;
	
	@BeforeEach
	public void setup() {
		subject = new UnauthorizedException();
	}

	@Test
	public void testUnauthorizedException() {
		// Given
		
		// When
		
		// Then
		assertNotNull(subject);
        assertNull(subject.getMessage());

	}

}
