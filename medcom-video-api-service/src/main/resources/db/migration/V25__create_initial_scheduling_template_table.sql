CREATE TABLE scheduling_template (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  conferencing_sys_id bigint(20) NOT NULL,
  uri_prefix varchar(100) NOT NULL,
  uri_domain varchar(100) NOT NULL,
  host_pin_required bit NOT NULL,
  host_pin_range_low bigint(20) NOT NULL,
  host_pin_range_high bigint(20) NOT NULL,
  guest_pin_required bit NOT NULL,
  guest_pin_range_low bigint(20) NOT NULL,
  guest_pin_range_high bigint(20) NOT NULL,
  vmravailable_before int NOT NULL,
  max_participants int NOT NULL,
  end_meeting_on_end_time bit NOT NULL,
  uri_number_range_low bigint(20) NOT NULL,
  uri_number_range_high bigint(20) NOT NULL,
  PRIMARY KEY (id)  
) 
