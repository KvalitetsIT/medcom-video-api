package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.EntitiesIvrTheme;

import java.util.Optional;

public interface EntitiesIvrThemeDao {
    Optional<EntitiesIvrTheme> getTheme(String uuid);
}
