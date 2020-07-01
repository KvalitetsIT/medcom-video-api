package dk.medcom.video.api.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dk.medcom.video.api.entity.PoolInfoEntity;

public class PoolInfoRowMapper implements RowMapper<PoolInfoEntity> {

	@Override
	public PoolInfoEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        if(resultSet.wasNull()) {
        	return null;
        }
        var pInfo = new PoolInfoEntity();
        pInfo.setOrganisationCode(resultSet.getString("organisationCode"));
        pInfo.setAvailablePoolSize(resultSet.getInt("available"));
        pInfo.setWantedPoolSize(resultSet.getInt("wanted"));
        return pInfo;
	}
}
