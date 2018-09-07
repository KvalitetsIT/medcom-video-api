CREATE TABLE meetings (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  uuid varchar(40) NOT NULL,
  subject varchar(100) NOT NULL,
  organisation_id varchar(30) NOT NULL,
  created_by bigint(20) NOT NULL,
  start_time datetime NOT NULL,
  end_time datetime NOT NULL,
  description varchar(500),
  PRIMARY KEY (id),
  UNIQUE KEY (uuid),
  FOREIGN KEY (created_by) REFERENCES meeting_users(id)
) 
