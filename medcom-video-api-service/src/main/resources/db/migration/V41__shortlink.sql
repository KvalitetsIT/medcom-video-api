alter table meetings add  column   short_id varchar(12) NULL UNIQUE;

update meetings set short_id = substr(sha2(uuid, 256), 1, 12);

alter table meetings
    modify short_id varchar(12) not null UNIQUE;