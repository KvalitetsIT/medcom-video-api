package dk.medcom.video.api.dao;

import dk.medcom.video.api.dao.entity.PoolInfoEntity;
import dk.medcom.video.api.dao.rowmapper.PoolInfoRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class PoolInfoRepositoryImpl implements PoolInfoRepository {
	private final DataSource dataSource;

	private final PoolInfoRowMapper poolInfoRowMapper = new PoolInfoRowMapper();

	public PoolInfoRepositoryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<PoolInfoEntity> getPoolInfos() {
		var sql = "select o.organisation_id as organisationCode, o.name as organisationName, o.pool_size wanted, count(s.id) as available  "
				+ "from   organisation as o "
				+ "  left join scheduling_info as s on s.organisation_id = o.id "
				+ "        and s.meetings_id is null "
				+ "        and s.provision_status = 'PROVISIONED_OK' "
				+ " 	   and s.reservation_id is null "
				+ "where o.pool_size > 0 "
			// Kopieret fra "job" + "  AND created_time < ADDDATE(UTC_TIMESTAMP, INTERVAL -1 MINUTE) "
				+ " group by o.organisation_id, o.name, o.pool_size";
		
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		return template.query(sql, poolInfoRowMapper);
	}
}
