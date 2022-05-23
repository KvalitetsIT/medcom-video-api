CREATE TABLE pool_history (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  organisation_code varchar(30) NOT NULL,
  pool_enabled smallint,
  desired_pool_size int(4) null,
  available_pool_rooms int(4) null,
  status_time datetime,
  created_time timestamp,
  PRIMARY KEY (id)
);

create view view_pool_history
as select organisation_code
         ,pool_enabled
         ,desired_pool_size
         ,available_pool_rooms
         ,status_time
      from pool_history;
