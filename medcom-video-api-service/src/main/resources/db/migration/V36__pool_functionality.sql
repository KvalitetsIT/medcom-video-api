alter table organisation add column pool_size int(4) null;

alter table scheduling_info modify column uuid varchar(40) null;
alter table scheduling_info modify column meetings_id bigint(20) null;
alter table scheduling_info modify column vmrstart_time datetime null;

alter table scheduling_info add  column   organisation_id bigint(20) NULL;

update scheduling_info s
   set organisation_id = (select organisation_id from meetings m where m.id = s.meetings_id )
 where s.organisation_id is null;

alter table scheduling_info modify column organisation_id bigint(20) not null;
