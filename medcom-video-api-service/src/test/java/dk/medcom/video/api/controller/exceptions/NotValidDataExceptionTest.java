package dk.medcom.video.api.controller.exceptions;

import org.junit.Assert;
import org.junit.Test;

public class NotValidDataExceptionTest {
	
	NotValidDataException subject;

	@Test
	public void testNotValidDataException() {
		// Given
		String var1 = "test1";
		String var2 = "test2";
		subject = new NotValidDataException(NotValidDataErrors.SCHEDULING_TEMPLATE_NOT_IN_ORGANISATION, var1, var2);

		// When
		
		// Then
		Assert.assertNotNull(subject);
		Assert.assertEquals(String.format("Scheduling template %s does not belong to organisation %s.", var1, var2), subject.getErrorText());
	}

	@Test
	public void testNotValidDataExceptionNoStringFormat() {
		// Given
		subject = new NotValidDataException(NotValidDataErrors.EXTERNAL_ID_NOT_UNIQUE);

		// When

		// Then
		Assert.assertNotNull(subject);
		Assert.assertEquals(NotValidDataErrors.EXTERNAL_ID_NOT_UNIQUE.getErrorText(null), subject.getErrorText());
	}

}
