package dk.medcom.video.api.controller.exceptions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RessourceNotFoundExceptionTest {
	
	RessourceNotFoundException subject;
	
	@Before
	public void setup() {
		subject = new RessourceNotFoundException("Test1", "Test2");
	}

	@Test
	public void testRessourceNotFoundException() {
		// Given
		
		// When
		
		// Then
		Assert.assertNotNull(subject);
		Assert.assertEquals(null, subject.getMessage());
		Assert.assertEquals("Test1", subject.getRessource());
		Assert.assertEquals("Test2", subject.getField());

	}
	
	@Test
	public void testRessourceNotFoundExceptionUpdated() {
		// Given
		subject.setRessource("Ressource1");
		subject.setField("Field1");
		
		// When
		
		// Then
		Assert.assertNotNull(subject);
		Assert.assertEquals(null, subject.getMessage());
		Assert.assertEquals("Ressource1", subject.getRessource());
		Assert.assertEquals("Field1", subject.getField());

	}


}
