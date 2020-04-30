alter table meetings add  column   short_id varchar(20) NULL UNIQUE;

update meetings set short_id = substr(sha2(uuid, 256), 1, 8);

alter table meetings
    modify short_id varchar(20) not null UNIQUE;