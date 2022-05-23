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

---- * view entities meetingrooms *
--CREATE OR REPLACE view view_entities_meetingroom AS
--  SELECT    t1.uuid                                 AS uuid,
--            t1.group_id                             AS group_id,
--            t1.name                                 AS name,
--            t1.description                          AS description,
--            t1.pin                                  AS pin,
--            t1.guest_pin                            AS guest_pin,
--            t1.allow_guests                         AS allow_guests,
--            t1.participant_limit                    AS participant_limit,
--            t1.enable_overlay_text                  AS enable_overlay_text,
--            t1.guests_can_present                   AS guests_can_present,
--            t1.enable_chat                          AS enable_chat,
--            t1.force_presenter_into_main            AS force_presenter_into_main,
--            t1.mute_all_guests                      AS mute_all_guests,
--            t1.force_encryption                     AS force_encryption,
--            t1.type                                 AS type,
--            t1.host_layout                          AS host_layout,
--            t1.guest_layout                         AS guest_layout,
--            t1.quality                              AS quality,
--            t1.theme_id                             AS theme_id,
--            t1.last_use                             AS last_use,
--            group_concat(t2.alias SEPARATOR ',')    AS aliases,
--            group_concat(t2.last_use SEPARATOR ',') AS aliases_last_use,
--            t1.provision_status                     AS provision_status,
--            t1.provision_status_description         AS provision_status_description,
--            t1.provision_id                         AS provision_id,
--            t1.provision_timestamp                  AS provision_timestamp,
--            t1.created_time                         AS created_time,
--            t1.created_by                           AS created_by,
--            t1.updated_time                         AS updated_time,
--            t1.updated_by                           AS updated_by,
--            t1.deleted_time                         AS deleted_time,
--            t1.deleted_by                           AS deleted_by
--  FROM      (entities_meetingroom t1
--  LEFT JOIN entities_meetingroom_alias t2
--  ON       ((t2.relation_uuid = t1.uuid)))
--  GROUP BY  t1.uuid

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