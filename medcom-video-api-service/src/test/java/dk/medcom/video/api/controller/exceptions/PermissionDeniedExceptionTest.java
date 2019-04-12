package dk.medcom.video.api.controller.exceptions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PermissionDeniedExceptionTest {
	
	PermissionDeniedException subject;
	
	@Before
	public void setup() {
		subject = new PermissionDeniedException();
	}

	@Test
	public void testPermissionDeniedException() {
		// Given
		
		// When
		
		// Then
		Assert.assertNotNull(subject);
		Assert.assertEquals(null, subject.getMessage());

	}

}
