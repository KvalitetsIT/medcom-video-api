package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.Participant;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ParticipantDaoImpl implements ParticipantDao {
    private final NamedParameterJdbcTemplate template;

    public ParticipantDaoImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    private static final DataClassRowMapper<Participant> rowMapper =
            new DataClassRowMapper<>(Participant.class);

    private static final String SELECT =
            "select p.id, p.uuid, p.meeting_id, p.type, p.external_id, p.organisation, p.role, " +
                    "m.uuid as meeting_uuid " +
                    "from participant p join meetings m on m.id = p.meeting_id ";

    @Override
    public Participant save(Participant participant) {
        return participant.id() == null ? insert(participant) : update(participant);
    }

    private Participant insert(Participant participant) {
        var uuid = participant.uuid() != null ? participant.uuid() : UUID.randomUUID();

        var sql = "insert into participant(uuid, meeting_id, type, external_id, organisation, role) " +
                "values(:uuid, :meeting_id, :type, :external_id, :organisation, :role)";

        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, params(participant).addValue("uuid", uuid), keyHolder, new String[]{"id"});
        long newId = keyHolder.getKey().longValue();

        return new Participant(
                newId,
                uuid,
                participant.meetingId(),
                participant.meetingUuid(),
                participant.type(),
                participant.externalId(),
                participant.organisation(),
                participant.role());
    }

    private Participant update(Participant participant) {
        var sql = "update participant set meeting_id = :meeting_id, type = :type, " +
                "external_id = :external_id, organisation = :organisation, role = :role " +
                "where id = :id";
        template.update(sql, params(participant).addValue("id", participant.id()));
        return participant;
    }

    private MapSqlParameterSource params(Participant p) {
        return new MapSqlParameterSource()
                .addValue("meeting_id", p.meetingId())
                .addValue("type", p.type() != null ? p.type().name() : null)
                .addValue("external_id", p.externalId())
                .addValue("organisation", p.organisation())
                .addValue("role", p.role() != null ? p.role().name() : null);
    }

    @Override
    public Optional<Participant> findByUuId(UUID uuid) {
        return template.query(SELECT + "where p.uuid = :uuid",
                new MapSqlParameterSource("uuid", uuid), rowMapper).stream().findFirst();
    }

    @Override
    public List<Participant> findByMeeting(Meeting meeting) {
        return template.query(SELECT + "where p.meeting_id = :meeting_id",
                new MapSqlParameterSource("meeting_id", meeting.getId()), rowMapper);
    }

    @Override
    public long count() {
        return template.queryForObject("select count(*) from participant",
                new MapSqlParameterSource(), Long.class);
    }

    @Override
    public void deleteById(Long id) {
        template.update("delete from participant where id = :id",
                new MapSqlParameterSource("id", id));
    }

    @Override
    public void delete(Participant participant) {
        deleteById(participant.id());
    }
}