CREATE TABLE pool_history (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  organisation_code varchar(30) NOT NULL,
  desired_pool_size int(4) null,
  available_pool_rooms int(4) null,
  status_time datetime,
  created_time timestamp,
  PRIMARY KEY (id)
);

create or replace view view_pool_history
as select h.organisation_code
         ,o.name as organisation_name
         ,h.desired_pool_size
         ,h.available_pool_rooms
         ,h.status_time
      from pool_history h left outer join organisation as o on o.organisation_id = h.organisation_code
;