package dk.medcom.video.api.dao.impl;

import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.dao.rowmapper.PoolInfoRowMapper;
import dk.medcom.video.api.entity.PoolInfoEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class PoolInfoRepositoryImpl implements PoolInfoRepository {

	
	private DataSource dataSource;

	private PoolInfoRowMapper poolInfoRowMapper = new PoolInfoRowMapper();

	public PoolInfoRepositoryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<PoolInfoEntity> getPoolInfos() {
//		var sql = "select o.organisation_id as organisationCode, o.pool_size wanted, count(s.id) as available  "
		var sql = "select o.organisation_id as organisationCode, o.name as organisationName, o.pool_size wanted, count(s.id) as available  "
				+ "from   organisation as o "
				+ "  left join scheduling_info as s on s.organisation_id = o.id "
				+ "        and s.meetings_id is null "
				+ "        and s.provision_status = 'PROVISIONED_OK' "
				+ "where o.pool_size > 0 "
			// Kopieret fra "job" + "  AND created_time < ADDDATE(UTC_TIMESTAMP, INTERVAL -1 MINUTE) "
				+ " group by o.organisation_id";
		
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		return template.query(sql, poolInfoRowMapper);
	}
}
