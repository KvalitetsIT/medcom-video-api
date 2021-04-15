CREATE TABLE entities (
  entity_id bigint(20) NOT NULL AUTO_INCREMENT,
  relation_id bigint(20) NOT NULL,
  jsondata longtext NOT NULL,
  blobdata longblob,
  blob_updated_time datetime DEFAULT NULL,
  type varchar(20) NOT NULL,
  provision_status varchar(30) DEFAULT 'AWAITS_PROVISION',
  provision_status_description varchar(250) DEFAULT NULL,
  provision_id varchar(50) DEFAULT NULL,
  provision_timestamp datetime DEFAULT NULL,
  created_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by varchar(250) NOT NULL DEFAULT 'system',
  updated_time datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  updated_by varchar(250) DEFAULT NULL,
  deleted_time datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  deleted_by varchar(250) DEFAULT NULL,
  PRIMARY KEY (entity_id),
  KEY relationid_type_deletedtime (relation_id,type,deleted_time),
  KEY idx_provisionstatus_type (provision_status,type)
);

-- * view entities meetingrooms *
CREATE OR REPLACE VIEW view_entities_meetingrooms AS
select
    e1.entity_id AS entity_id,
    e1.relation_id AS relation_id,
    json_unquote(json_extract(e1.jsondata,
    '$.name')) AS name,
    json_unquote(json_extract(e1.jsondata,
    '$.description')) AS description,
    group_concat(json_unquote(json_extract(e2.jsondata, '$.alias')) separator ',') AS aliases,
    e1.jsondata AS jsondata,
    e1.type AS type,
    e1.provision_status AS provision_status,
    e1.provision_status_description AS provision_status_description,
    e1.provision_id AS provision_id,
    e1.provision_timestamp AS provision_timestamp,
    e1.created_time AS created_time,
    e1.created_by AS created_by,
    e1.updated_time AS updated_time,
    e1.updated_by AS updated_by
from
    (entities e1 straight_join entities e2 on
    ((e2.relation_id = e1.entity_id)))
where
    ((e1.type = 'meetingroom')
        and (e2.type = 'alias')
            and (e1.deleted_time = '0001-01-01')
                and (e2.deleted_time = '0001-01-01'))
group by
    e1.entity_id;

-- * view entities registeredclients *
CREATE OR REPLACE VIEW view_entities_registeredclients AS
select
    entities.entity_id AS entity_id,
    entities.relation_id AS relation_id,
    json_unquote(json_extract(entities.jsondata,
    '$.alias')) AS alias,
    json_unquote(json_extract(entities.jsondata,
    '$.description')) AS description,
    json_unquote(json_extract(entities.jsondata,
    '$.username')) AS username,
    entities.jsondata AS jsondata,
    entities.type AS type,
    entities.provision_status AS provision_status,
    entities.provision_status_description AS provision_status_description,
    entities.provision_id AS provision_id,
    entities.provision_timestamp AS provision_timestamp,
    entities.created_time AS created_time,
    entities.created_by AS created_by,
    entities.updated_time AS updated_time,
    entities.updated_by AS updated_by
from
    entities
where
    ((entities.type = 'registeredclient')
        and (entities.deleted_time = '0001-01-01'));

-- * view groups *
CREATE OR REPLACE VIEW view_groups AS
select
    groups.group_id AS group_id,
    groups.parent_id AS parent_id,
    if((not(exists(
    select
        organisation.id
    from
        organisation
    where
        (groups.group_id = organisation.group_id)))),
    groups.group_name,
    convert(organisation.name
        using utf8mb4)) AS group_name,
    groups.group_type AS group_type,
    if((groups.group_type = 1),
    'group',
    if((groups.group_type = 2),
    'organisation',
    if((groups.group_type = 3),
    'praksis',
    ''))) AS group_type_name,
    if((groups.deleted_time > '0001-01-01'),
    1,
    0) AS Deleted,
    organisation.id AS organisation_id,
    organisation.organisation_id AS organisation_id_name,
    groups.created_time AS created_time,
    groups.created_by AS created_by,
    groups.updated_time AS updated_time,
    groups.updated_by AS updated_by,
    groups.deleted_time AS deleted_time,
    groups.deleted_by AS deleted_by
from
    (groups
left join organisation on
    ((groups.group_id = organisation.group_id)))
order by
    groups.group_id;

-- * view groups domains *
CREATE OR REPLACE VIEW view_groups_domains AS
select
    ginfo.group_id AS group_id,
    ginfo.parent_id AS parent_id,
    ginfo.organisation_id AS organisation_id,
    ginfo.organisation_id_name AS organisation_id_name,
    ginfo.group_name AS group_name,
    ginfo.group_type AS group_type,
    ginfo.group_type_name AS group_type_name,
    dinfo.domain AS domain
from
    (view_groups ginfo straight_join groups_domains dinfo on
    ((ginfo.group_id = dinfo.group_id)))
where
    (ginfo.Deleted = 0);