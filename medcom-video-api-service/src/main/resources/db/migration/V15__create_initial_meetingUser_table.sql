CREATE TABLE meeting_users (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  organisation_id bigint(20) NOT NULL,  
  email varchar(45) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (organisation_id, email),
    FOREIGN KEY (organisation_id) REFERENCES organisation(id)
) 