package dk.medcom.video.api.controller.exceptions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NotAcceptableExceptionTest {
	
	NotAcceptableException subject;
	
	@Before
	public void setup() {
		subject = new NotAcceptableException("Test");
	}

	@Test
	public void testNotAcceptableException() {
		// Given
		
		// When
		
		// Then
		Assert.assertNotNull(subject);
		Assert.assertEquals("Test", subject.getMessage());

	}

}
