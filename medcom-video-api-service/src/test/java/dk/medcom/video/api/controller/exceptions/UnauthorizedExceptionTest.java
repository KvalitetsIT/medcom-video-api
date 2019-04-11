package dk.medcom.video.api.controller.exceptions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnauthorizedExceptionTest {
	
	UnauthorizedException subject;
	
	@Before
	public void setup() {
		subject = new UnauthorizedException();
	}

	@Test
	public void testUnauthorizedException() {
		// Given
		
		// When
		
		// Then
		Assert.assertNotNull(subject);
		Assert.assertEquals(null, subject.getMessage());

	}

}
