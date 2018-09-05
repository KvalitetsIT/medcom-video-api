CREATE TABLE meetings (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  uuid varchar(40) NOT NULL,
  subject varchar(100) NOT NULL,
  organisation_id varchar(30) NOT NULL,
  created_by bigint(20),
  UNIQUE KEY (uuid),
  PRIMARY KEY (id),
  FOREIGN KEY (created_by) REFERENCES meeting_users(id)
) 
