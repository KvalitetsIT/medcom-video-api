CREATE TABLE meeting_users (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  organisation_id varchar(30) NOT NULL,
  email varchar(45) NOT NULL,
  UNIQUE KEY (organisation_id, email),
  PRIMARY KEY (id)
) 