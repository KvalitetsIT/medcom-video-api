alter table meetings add  column   external_id varchar(200) NULL;

create unique index organisation_external_id on meetings(organisation_id, external_id);
