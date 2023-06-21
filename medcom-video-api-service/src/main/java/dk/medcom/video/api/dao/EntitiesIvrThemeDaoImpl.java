package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.EntitiesIvrThemeDao;
import dk.medcom.video.api.dao.entity.EntitiesIvrTheme;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Optional;

public class EntitiesIvrThemeDaoImpl implements EntitiesIvrThemeDao {
    private final NamedParameterJdbcTemplate template;

    public EntitiesIvrThemeDaoImpl(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<EntitiesIvrTheme> getTheme(String uuid) {
        var sql = "select * from entities_ivrtheme where uuid = :uuid";

        try {
            return Optional.ofNullable(template.queryForObject(sql, Collections.singletonMap("uuid", uuid), BeanPropertyRowMapper.newInstance(EntitiesIvrTheme.class)));
        }
        catch(EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
