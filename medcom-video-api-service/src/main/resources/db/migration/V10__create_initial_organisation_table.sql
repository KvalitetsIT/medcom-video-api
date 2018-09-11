CREATE TABLE organisation (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  organisation_id varchar(30) NOT NULL,
  name varchar(100),
  PRIMARY KEY (id),
  UNIQUE KEY (organisation_id)
) 