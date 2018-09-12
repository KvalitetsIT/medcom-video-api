CREATE TABLE scheduling_template (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  conferencing_sys_id bigint(20) NOT NULL,
  uri_prefix varchar(64) NOT NULL,
  uri_domain varchar(128) NOT NULL,
  host_pin_required bit NOT NULL,
  host_pin_range_low bigint(20) NOT NULL,
  host_pin_range_high bigint(20) NOT NULL,
  guest_pin_required bit NOT NULL,
  guest_pin_range_low bigint(20) NOT NULL,
  guest_pin_range_high bigint(20) NOT NULL,
  vmravailable_before int NOT NULL,
  max_participants int NOT NULL,
  uri_number_range_low bigint(20) NOT NULL,
  uri_number_range_high bigint(20) NOT NULL,
  PRIMARY KEY (id)  
) 
