package dk.medcom.vdx.organisation.dao;

import dk.medcom.vdx.organisation.dao.entity.Organisation;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class OrganisationDaoImpl implements OrganisationDao {
    private final DataSource dataSource;

    public OrganisationDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Organisation findOrganisation(String code) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
//        CREATE ALGORITHM=UNDEFINED DEFINER=`vdxapi`@`%` SQL SECURITY DEFINER VIEW `view_groups` AS
//        select `groups`.`group_id` AS `group_id`,
//        `groups`.`parent_id` AS `parent_id`,
//        if((not(exists(select `organisation`.`id` from `organisation` where (`groups`.`group_id` = `organisation`.`group_id`)))), `groups`.`group_name`,`organisation`.`name`) AS `group_name`,
//        `groups`.`group_type` AS `group_type`,
//        if((`groups`.`group_type` = 1),'group',if((`groups`.`group_type` = 2),'organisation',if((`groups`.`group_type` = 3),'praksis',''))) AS `group_type_name`,
//        if((`groups`.`deleted_time` > '0001-01-01'),1,0) AS `Deleted`,
//        `organisation`.`id` AS `organisation_id`,
//        `organisation`.`organisation_id` AS `organisation_id_name`,
//        `groups`.`created_time` AS `created_time`,
//        `groups`.`created_by` AS `created_by`,
//        `groups`.`updated_time` AS `updated_time`,
//        `groups`.`updated_by` AS `updated_by`,
//        `groups`.`deleted_time` AS `deleted_time`,
//        `groups`.`deleted_by` AS `deleted_by` from (`groups` left join `organisation` on((`groups`.`group_id` = `organisation`.`group_id`))) order by `groups`.`group_id`

        var sql = "select o.pool_size, " +
                "g.parent_id, " +
                "g.group_id, " +
                "g.group_name, " +
                "o.organisation_id, " +
                "o.name organisation_name " +
                "from organisation o, groups g " +
                "where o.organisation_id = :organisation_id" +
                "  and g.group_id = o.group_id";

        Map<String, String> parameters = new HashMap<>();
        parameters.put("organisation_id", code);

        try {
            return template.queryForObject(sql, parameters, BeanPropertyRowMapper.newInstance(Organisation.class));
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
//        CREATE DEFINER=`root_user`@`%` PROCEDURE `getParentOrganisationIdWithPool`(IN `startId` INT)
//        BEGIN
//        set @nextId = (select `group_id` from view_groups where organisation_id = startId);
//        WHILE (@nextId IS NOT NULL) DO
//        If(SELECT count(*) FROM organisation WHERE group_id = @nextId AND pool_size IS NOT NULL) = 1 THEN
//        set @newOut_organisation_id = (select `id` from organisation where group_id = @nextId);
//        set @nextId = NULL;
//        ELSE
//        set @nextId = (select `parent_id` from view_groups where group_id = @nextId);
//        END IF;
//        END WHILE;
//        SELECT @newOut_organisation_id as "organisation_id";
//        END
    }

    @Override
    public Organisation findOrganisationByGroupId(long groupId) {
        var sql = "select o.pool_size, " +
                "g.parent_id, " +
                "g.group_id, " +
                "g.group_name, " +
                "o.organisation_id, " +
                "o.name organisation_name " +
                "from groups g left outer join organisation o on g.group_id = o.group_id " +
                "where g.group_id = :group_id";

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("group_id", groupId);

        return template.queryForObject(sql, parameters, BeanPropertyRowMapper.newInstance(Organisation.class));

    }
}
