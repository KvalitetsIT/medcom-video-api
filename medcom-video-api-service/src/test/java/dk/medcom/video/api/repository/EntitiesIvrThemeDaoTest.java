package dk.medcom.video.api.repository;


import dk.medcom.video.api.dao.EntitiesIvrThemeDao;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class EntitiesIvrThemeDaoTest extends RepositoryTest {

	@Resource
    private EntitiesIvrThemeDao subject;

	@Test
	public void testGetThemeNotFound() {
		// Given
		var uuid = UUID.randomUUID().toString();

		// When
		var theme = subject.getTheme(uuid);
		
		// Then
		Assert.assertNotNull(theme);
		assertTrue(theme.isEmpty());

	}
}