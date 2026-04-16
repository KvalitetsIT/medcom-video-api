package dk.medcom.video.api.controller.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NotAcceptableExceptionTest {
	
	NotAcceptableException subject;
	
	@BeforeEach
	public void setup() {
		subject = new NotAcceptableException(NotAcceptableErrors.URI_ASSIGNMENT_FAILED_NOT_POSSIBLE_TO_CREATE_UNIQUE);
	}

	@Test
	public void testNotAcceptableException() {
		// Given
		
		// When
		
		// Then
		assertNotNull(subject);
		assertEquals(21, subject.getErrorCode());

	}

}
