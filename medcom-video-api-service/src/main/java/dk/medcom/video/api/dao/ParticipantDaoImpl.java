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
            "select p.id, p.uuid, p.meeting_id, p.type, p.participant_id, p.organisation, p.role, " +
                    "p.created_at, p.created_by, p.updated_at, p.updated_by, " +
                    "m.uuid as meeting_uuid " +
                    "from participant p join meetings m on m.id = p.meeting_id ";

    @Override
    public Participant save(Participant participant) {
        return participant.id() == null ? insert(participant) : update(participant);
    }

    private Participant insert(Participant participant) {
        var uuid = participant.uuid() != null ? participant.uuid() : UUID.randomUUID();
        var sql = "insert into participant(uuid, meeting_id, type, participant_id, organisation, role, created_by, updated_by) " +
                "values(:uuid, :meeting_id, :type, :participant_id, :organisation, :role, :created_by, :updated_by)";
        var keyHolder = new GeneratedKeyHolder();
        template.update(sql, params(participant).addValue("uuid", uuid), keyHolder, new String[]{"id"});
        long newId = keyHolder.getKey().longValue();

        return findByUuId(uuid).orElseThrow(() ->
                new IllegalStateException("Participant with id " + newId + " could not be found after insert"));
    }

    private Participant update(Participant participant) {
        var sql = "update participant set meeting_id = :meeting_id, type = :type, " +
                "participant_id = :participant_id, organisation = :organisation, role = :role, " +
                "updated_by = :updated_by " +
                "where id = :id";
        template.update(sql, params(participant).addValue("id", participant.id()));
        return findByUuId(participant.uuid()).orElseThrow(() ->
                new IllegalStateException("Participant with id " + participant.id() + " could not be found after update"));
    }

    private MapSqlParameterSource params(Participant p) {
        return new MapSqlParameterSource()
                .addValue("meeting_id", p.meetingId())
                .addValue("type", p.type() != null ? p.type().name() : null)
                .addValue("participant_id", p.participantId())
                .addValue("organisation", p.organisation())
                .addValue("role", p.role() != null ? p.role().name() : null)
                .addValue("created_by", p.createdBy())
                .addValue("updated_by", p.updatedBy());
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