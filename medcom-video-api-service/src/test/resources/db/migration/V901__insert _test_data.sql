-- * organisation *
INSERT INTO organisation (id, organisation_id, name) VALUES (1, 'company 1', 'company name 1'); 
INSERT INTO organisation (id, organisation_id, name) VALUES (2, 'company 2', 'company name 2');
INSERT INTO organisation (id, organisation_id, name) VALUES (3, 'company 3', 'company name 3');
INSERT INTO organisation (id, organisation_id, name) VALUES (4, 'kvak', 'company name kvak');
INSERT INTO organisation (id, organisation_id, name) VALUES (5, 'test-org', 'company name test-org');
INSERT INTO organisation (id, organisation_id, name) VALUES (6, 'another-test-org', 'company name another-test-org');
INSERT INTO organisation (id, organisation_id, name, pool_size) VALUES (7, 'pool-test-org', 'company name another-test-org', 10);
INSERT INTO organisation (id, organisation_id, name, pool_size) VALUES (8, 'pool-test-org2', 'company name another-test-org2', 30);

-- * meeting_users *
INSERT INTO meeting_users (id, organisation_id, email) VALUES (101,  5, 'me@me101.dk');
INSERT INTO meeting_users (id, organisation_id, email) VALUES (102,  6, 'me@me102.dk');
INSERT INTO meeting_users (id, organisation_id, email) VALUES (103,  5, 'me@me103.dk');
INSERT INTO meeting_users (id, organisation_id, email) VALUES (104,  5, 'me@me105organizer.dk');
INSERT INTO meeting_users (id, organisation_id, email) VALUES (105,  5, 'me@me106organizer.dk');
-- testing schedulingTemplate
INSERT INTO meeting_users (id, organisation_id, email) VALUES (106,  2, 'me@me107.dk');
			
-- * meetings *	
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description, project_code, organized_by, updated_by, updated_time, created_time, short_id) VALUES (1, uuid(), 'TestMeeting-xyz', 5, 101, '2018-10-02 15:00:00', '2018-10-02 16:00:00', 'Mødebeskrivelse 1', 'PRJCDE1', 101, 101, '2018-09-02 15:00:00', '2018-09-02 15:00:00', substr(sha2(uuid, 256), 1, 12));
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description, project_code, organized_by, updated_by, updated_time, created_time, short_id) VALUES (2, uuid(), 'MyMeeting', 6, 102, '2018-11-02 15:00:00', '2018-11-02 16:00:00', 'Mødebeskrivelse 2', 'PRJCDE1', 102, 102, '2018-09-02 15:00:00', '2018-09-02 15:00:00', substr(sha2(uuid, 256), 1, 12));
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description, project_code, organized_by, updated_by, updated_time, created_time, short_id) VALUES (3, '7cc82183-0d47-439a-a00c-38f7a5a01fce', 'TestMeeting-123', 5, 101,  '2018-12-02 15:00:00', '2018-12-02 16:00:00', 'Mødebeskrivelse 3', 'PRJCDE1', 101, 101, '2018-09-02 15:00:00', '2018-09-02 15:00:00', substr(sha2(uuid, 256), 1, 12));
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description, project_code, organized_by, updated_by, updated_time, created_time, short_id) VALUES (4, uuid(), 'MyMeeting4', 6, 102, '2018-11-04 15:00:00', '2018-11-04 16:00:00', 'Mødebeskrivelse 4', 'PRJCDE1', 102, 102, '2018-09-02 15:00:00', '2018-09-02 15:00:00', substr(sha2(uuid, 256), 1, 12));
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description, project_code, organized_by, updated_by, updated_time, created_time, short_id) VALUES (5, uuid(), 'TestMeeting-xyz5', 5, 101, '2018-12-02 15:00:00', '2018-12-02 16:00:00', 'Mødebeskrivelse 5', 'PRJCDE1', 104, 101, '2018-09-02 15:00:00', '2018-09-02 15:00:00', substr(sha2(uuid, 256), 1, 12));
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description, project_code, organized_by, updated_by, updated_time, created_time, short_id) VALUES (6, uuid(), 'TestMeeting-xyz6', 5, 104, '2018-10-02 15:00:00', '2018-10-02 16:00:00', 'Mødebeskrivelse 6', 'PRJCDE1', 101, NULL, NULL, NULL, substr(sha2(uuid, 256), 1, 12));
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description, project_code, organized_by, updated_by, updated_time, created_time, short_id) VALUES (7, uuid(), 'TestMeeting-xyz7', 5, 104, '2019-10-02 15:00:00', '2019-10-02 16:00:00', 'Mødebeskrivelse 7', 'PRJCDE1', 101, NULL, NULL, NULL, 'abcdefgh');

-- * scheduling_template * 			
INSERT INTO scheduling_template (id, organisation_id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, end_meeting_on_end_time, uri_number_range_low, uri_number_range_high, ivr_theme) 
VALUES (1, 1, 22, 'abc', 'test.dk', 1, 1, 91, 0, 100, 991, 15, 10, 1, 1000, 9991, '/api/admin/configuration/v1/ivr_theme/10/');
INSERT INTO scheduling_template (id, organisation_id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, end_meeting_on_end_time, uri_number_range_low, uri_number_range_high, ivr_theme) 
VALUES (2, 2, 22, 'abc2a', 'test.dk', 1, 1, 91, 0, 100, 991, 20, 10, 1, 1000, 9991, '/api/admin/configuration/v1/ivr_theme/10/');
INSERT INTO scheduling_template (id, organisation_id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, end_meeting_on_end_time, uri_number_range_low, uri_number_range_high, ivr_theme) 
VALUES (3, 2, 22, 'abc2b', 'test.dk', 1, 1, 91, 0, 100, 991, 15, 10, 1, 1000, 9991, '/api/admin/configuration/v1/ivr_theme/10/');
INSERT INTO scheduling_template (id, organisation_id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, end_meeting_on_end_time, uri_number_range_low, uri_number_range_high, ivr_theme, is_default_template) 
VALUES (4, 3, 22, 'abc3a', 'test.dk', 1, 1, 91, 0, 100, 991, 15, 10, 1, 1000, 9991, '/api/admin/configuration/v1/ivr_theme/30/', true);
INSERT INTO scheduling_template (id, organisation_id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, end_meeting_on_end_time, uri_number_range_low, uri_number_range_high, ivr_theme, is_default_template) 
VALUES (5, 3, 22, 'abc3b', 'test.dk', 1, 1, 91, 0, 100, 991, 15, 10, 1, 1000, 9991, '/api/admin/configuration/v1/ivr_theme/30/', false);
INSERT INTO scheduling_template (id, organisation_id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, end_meeting_on_end_time, uri_number_range_low, uri_number_range_high, ivr_theme, is_default_template)
VALUES (6, 7, 22, 'abc3b', 'test.dk', 1, 1, 91, 0, 100, 991, 15, 10, 1, 1000, 9991, '/api/admin/configuration/v1/ivr_theme/30/', true);

-- INSERT INTO scheduling_template (id, organisation_id, conferencing_sys_id, uri_prefix, uri_domain, host_pin_required, host_pin_range_low, host_pin_range_high, guest_pin_required, guest_pin_range_low, guest_pin_range_high, vmravailable_before, max_participants, end_meeting_on_end_time, uri_number_range_low, uri_number_range_high, ivr_theme)
-- VALUES (2, 4, 22, 'abc', 'test.dk', 1, 1, 91, 0, 100, 991, 15, 10, 1, 1000, 9991, '');

-- * scheduling_info *
INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id)
VALUES (201, (select uuid from meetings where id = 1) , 1001, 2001, 15, '2018-10-02 14:45:00', 10, 1, 1, '1230@test.dk', '1230', 1, 'AWAITS_PROVISION', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1230@test.dk&pin=2001&start_dato=2018-10-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 101, '2018-09-02 15:00:00', 101, '2018-09-02 15:00:00', 5);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id)
VALUES (202, (select uuid from meetings where id = 2), 1001, 2001, 20, '2018-11-02 14:40:00', 10, 1, 2, '1231@test.dk', '1231', 1, 'AWAITS_PROVISION', '', null, null, 'https://portal-test.vconf.dk/?url=1231@test.dk&pin=2001&start_dato=2018-11-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 102, '2018-09-02 15:00:00', 102, '2018-09-02 15:00:00', 6);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id)
VALUES (203, (select uuid from meetings where id = 3), 1001, 2001, 30, '2018-12-02 14:30:00', 10, 1, 3, '1232@test.dk', '1232', 1, 'AWAITS_PROVISION', '', null, null, 'https://portal-test.vconf.dk/?url=1231@test.dk&pin=2001&start_dato=2018-12-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 101, '2018-09-02 15:00:00', 101, '2018-09-02 15:00:00', 5);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id)
VALUES (204, (select uuid from meetings where id = 5) , 1001, 2001, 15, '2018-12-02 14:45:00', 10, 1, 5, '1233@test.dk', '1233', 1, 'AWAITS_PROVISION', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1233@test.dk&pin=2001&start_dato=2018-12-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 101, '2018-09-02 15:00:00', 101, '2018-09-02 15:00:00', 6);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id)
VALUES (206, (select uuid from meetings where id = 6) , 1001, 2001, 15, '2018-10-02 14:45:00', 10, 1, 6, '1236@test.dk', '1236', 1, 'AWAITS_PROVISION', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1236@test.dk&pin=2001&start_dato=2018-10-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 104, NULL, NULL, NULL, 5);

INSERT INTO scheduling_info (id, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, uuid)
VALUES (207, 1001, 2001, 15, '2018-10-02 14:45:00', 10, 1, '1236@test.dk', '1238', 1, 'PROVISIONED_OK', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1236@test.dk&pin=2001&start_dato=2018-10-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 104, NULL, NULL, NULL, 7, '31293b98-0c51-447c-b10b-1886ab095450');


-- * scheduling_status * 			
INSERT INTO scheduling_status (id, time_stamp, provision_status, provision_status_description, meetings_id) VALUES (301, NOW(), 'AWAITS_PROVISION', 'all ok', 1);
INSERT INTO scheduling_status (id, time_stamp, provision_status, provision_status_description, meetings_id) VALUES (302, NOW(), 'AWAITS_PROVISION', '', 2);
INSERT INTO scheduling_status (id, time_stamp, provision_status, provision_status_description, meetings_id) VALUES (303, NOW(), 'AWAITS_PROVISION', '', 3);

-- * meeting_labels *
insert into meeting_labels(id, meeting_id, label) values(301, 7, 'first label');
insert into meeting_labels(id, meeting_id, label) values(302, 7, 'second label');
