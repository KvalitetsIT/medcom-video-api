-- * organisation *
INSERT INTO organisation (id, organisation_id, name) VALUES (1, 'company 1', 'company name 1'); 
INSERT INTO organisation (id, organisation_id, name) VALUES (2, 'company 2', 'company name 2');
INSERT INTO organisation (id, organisation_id, name) VALUES (3, 'company 3', 'company name 3');
INSERT INTO organisation (id, organisation_id, name) VALUES (4, 'kvak', 'company name kvak');
INSERT INTO organisation (id, organisation_id, name) VALUES (5, 'test-org', 'company name test-org');
INSERT INTO organisation (id, organisation_id, name) VALUES (6, 'another-test-org', 'company name another-test-org');

-- * meeting_users *
INSERT INTO meeting_users (id, organisation_id, email) VALUES (101,  5, 'me@me101.dk');
INSERT INTO meeting_users (id, organisation_id, email) VALUES (102,  6, 'me@me102.dk');
INSERT INTO meeting_users (id, organisation_id, email) VALUES (103,  5, 'me@me103.dk');
			
-- * meetings *	
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (1, uuid(), 'TestMeeting-xyz', 5, 101, '2018-10-02 15:00:00', '2018-10-02 16:00:00', 'Mødebeskrivelse 1');
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (2, uuid(), 'MyMeeting', 6, 102, '2018-11-02 15:00:00', '2018-11-02 16:00:00', 'Mødebeskrivelse 2');
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (3, '7cc82183-0d47-439a-a00c-38f7a5a01fce', 'TestMeeting-123', 5, 101,  '2018-12-02 15:00:00', '2018-12-02 16:00:00', 'Mødebeskrivelse 3');
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description) VALUES (4, uuid(), 'MyMeeting4', 6, 102, '2018-11-04 15:00:00', '2018-11-04 16:00:00', 'Mødebeskrivelse 4');

-- * scheduling_template * 			
INSERT INTO scheduling_template (id, organisation_id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, end_meeting_on_end_time, uri_number_range_low, uri_number_range_high) 
VALUES (1, 1, 22, 'abc', 'test.dk', 1, 1, 91, 0, 100, 991, 15, 10, 1, 1000, 9991);
-- INSERT INTO scheduling_template (id, organisation_id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, end_meeting_on_end_time, uri_number_range_low, uri_number_range_high) 
-- VALUES (2, 4, 22, 'abc', 'test.dk', 1, 1, 91, 0, 100, 991, 15, 10, 1, 1000, 9991);

-- * scheduling_info *
INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid) 
VALUES (201, (select uuid from meetings where id = 1) , 1001, 2001, 15, '2018-10-02 14:45:00', 10, 1, 1, '1230@test.dk', '1230', 1, 'AWAITS_PROVISION', 'all ok', null, null);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid)
VALUES (202, (select uuid from meetings where id = 2), 1001, 2001, 20, '2018-11-02 14:40:00', 10, 1, 2, '1231@test.dk', '1231', 1, 'AWAITS_PROVISION', '', null, null);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid)
VALUES (203, (select uuid from meetings where id = 3), 1001, 2001, 30, '2018-12-02 14:30:00', 10, 1, 3, '1232@test.dk', '1232', 1, 'AWAITS_PROVISION', '', null, null);

-- * scheduling_status * 			
INSERT INTO scheduling_status (id, time_stamp, provision_status, provision_status_description, meetings_id) VALUES (301, NOW(), 'AWAITS_PROVISION', 'all ok', 1);
INSERT INTO scheduling_status (id, time_stamp, provision_status, provision_status_description, meetings_id) VALUES (302, NOW(), 'AWAITS_PROVISION', '', 2);
INSERT INTO scheduling_status (id, time_stamp, provision_status, provision_status_description, meetings_id) VALUES (303, NOW(), 'AWAITS_PROVISION', '', 3);
