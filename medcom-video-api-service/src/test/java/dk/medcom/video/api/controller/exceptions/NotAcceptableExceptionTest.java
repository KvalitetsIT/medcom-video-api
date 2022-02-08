package dk.medcom.video.api.controller.exceptions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NotAcceptableExceptionTest {
	
	NotAcceptableException subject;
	
	@Before
	public void setup() {
		subject = new NotAcceptableException(NotAcceptableErrors.URI_ASSIGNMENT_FAILED_NOT_POSSIBLE_TO_CREATE_UNIQUE);
	}

	@Test
	public void testNotAcceptableException() {
		// Given
		
		// When
		
		// Then
		Assert.assertNotNull(subject);
		Assert.assertEquals(21, subject.getErrorCode());

	}

}
