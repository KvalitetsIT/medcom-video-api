package dk.medcom.video.api.controller.exceptions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NotValidDataExceptionTest {
	
	NotValidDataException subject;
	
	@Before
	public void setup() {
		subject = new NotValidDataException("Test");
	}

	@Test
	public void testNotValidDataException() {
		// Given
		
		// When
		
		// Then
		Assert.assertNotNull(subject);
		Assert.assertEquals("Test", subject.getMessage());

	}

}
