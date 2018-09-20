CREATE TABLE scheduling_status (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  time_stamp datetime NOT NULL,
  provision_status int NOT NULL,
  meetings_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (meetings_id) REFERENCES meetings(id)
)