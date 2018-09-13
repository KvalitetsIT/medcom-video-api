CREATE TABLE scheduling_info (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  uuid varchar(40) NOT NULL,  
  host_pin bigint(20),
  guest_pin bigint(20),
  vmravailable_before int NOT NULL,
  max_participants int NOT NULL,
  meetings_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (uuid),
  FOREIGN KEY (meetings_id) REFERENCES meetings(id)
)