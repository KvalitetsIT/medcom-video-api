package dk.medcom.video.api.dao.rowmapper;

import dk.medcom.video.api.entity.PoolInfoEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PoolInfoRowMapper implements RowMapper<PoolInfoEntity> {

	@Override
	public PoolInfoEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        if(resultSet.wasNull()) {
        	return null;
        }
        var pInfo = new PoolInfoEntity();
        pInfo.setOrganisationCode(resultSet.getString("organisationCode"));
        pInfo.setOrganisationName(resultSet.getString("organisationName"));
        pInfo.setAvailablePoolSize(resultSet.getInt("available"));
        pInfo.setWantedPoolSize(resultSet.getInt("wanted"));
        return pInfo;
	}
}
