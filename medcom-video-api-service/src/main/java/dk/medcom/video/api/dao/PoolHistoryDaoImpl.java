package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.PoolHistory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;

public class PoolHistoryDaoImpl implements PoolHistoryDao {
    private final DataSource dataSource;

    public PoolHistoryDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void create(PoolHistory poolHistory) {
        var sql = "insert into pool_history(organisation_code, desired_pool_size, available_pool_rooms, status_time, created_time) " +
                "values(:organisation_code, :desired_pool_size, :available_pool_rooms, :status_time, :created_time)";

        var template = new NamedParameterJdbcTemplate(dataSource);
        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("organisation_code", poolHistory.getOrganisationCode());
        parameterMap.put("desired_pool_size", poolHistory.getDesiredPoolSize());
        parameterMap.put("available_pool_rooms", poolHistory.getAvailablePoolRooms());
        parameterMap.put("status_time", new Timestamp(poolHistory.getStatusTime().toEpochMilli()));
        parameterMap.put("created_time", new Timestamp(poolHistory.getCreatedTime().toEpochMilli()));

        template.update(sql, parameterMap);
    }
}
