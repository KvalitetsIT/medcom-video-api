package dk.medcom.video.api.repository;


import dk.medcom.video.api.dao.EntitiesIvrThemeDao;
import org.junit.jupiter.api.Test;

import jakarta.annotation.Resource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertNotNull(theme);
		assertTrue(theme.isEmpty());

	}
}