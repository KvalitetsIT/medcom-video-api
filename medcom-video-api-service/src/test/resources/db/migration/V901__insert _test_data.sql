-- groups
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (1, 'company 1', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (2, 'company 2', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (3, 'company 3', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (4, 'kvak', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (5, 'test-org', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (6, 'another-test-org', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (7, 'pool-test-org', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (8, 'pool-test-org2', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (9, 'overflow', 2, null);
insert into groups(group_id, group_name, group_type, parent_id) values(10, 'super_parent', 2, null);
insert into groups(group_id, group_name, group_type, parent_id) values(11, 'parent', 2, 10);
insert into groups(group_id, group_name, group_type, parent_id) values(12, 'child_one', 2, 11);
insert into groups(group_id, group_name, group_type, parent_id, deleted_time, deleted_by) values(14, 'child_one-deleted', 2, 11, now(), 'some-user');
insert into groups(group_id, group_name, group_type, parent_id) values(13, 'child', 2, 12);

-- * organisation *
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (1, 'company 1', 'company name 1', 1);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (2, 'company 2', 'company name 2', 2);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (3, 'company 3', 'company name 3', 3);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (4, 'kvak', 'company name kvak', 4);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (5, 'test-org', 'company name test-org', 5);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (6, 'another-test-org', 'company name another-test-org', 6);
INSERT INTO organisation (id, organisation_id, name, pool_size, group_id) VALUES (7, 'pool-test-org', 'company name another-test-org', 10, 7);
INSERT INTO organisation (id, organisation_id, name, pool_size, group_id) VALUES (8, 'pool-test-org2', 'company name another-test-org2', 30, 8);
INSERT INTO organisation (id, organisation_id, name, pool_size, group_id) VALUES (9, 'overflow', 'overflow pool org', 10, 9);
insert into organisation(id, organisation_id, name, pool_size, group_id) values(10, 'parent', 'parent org', 20, 11);
insert into organisation(id, organisation_id, name, group_id) values(11, 'child', 'child org', 13);

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
INSERT INTO meetings (id, uuid, subject, organisation_id, created_by, start_time, end_time , description, project_code, organized_by, updated_by, updated_time, created_time, short_id) VALUES (8, uuid(), 'MyMeeting8', 5, 104, '2019-10-02 15:00:00', '2019-10-02 16:00:00', 'Mødebeskrivels 8', 'PRJCDE1', 101, NULL, NULL, '2019-10-01 15:00:00', substr(sha2(uuid, 256), 1, 12));

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
INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, pool_overflow)
VALUES (201, (select uuid from meetings where id = 1) , 1001, 2001, 15, '2018-10-02 14:45:00', 10, 1, 1, '1230@test.dk', '1230', 1, 'AWAITS_PROVISION', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1230@test.dk&pin=2001&start_dato=2018-10-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 101, '2018-09-02 15:00:00', 101, '2018-09-02 15:00:00', 5, 0);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, pool_overflow)
VALUES (202, (select uuid from meetings where id = 2), 1001, 2001, 20, '2018-11-02 14:40:00', 10, 1, 2, '1231@test.dk', '1231', 1, 'AWAITS_PROVISION', '', null, null, 'https://portal-test.vconf.dk/?url=1231@test.dk&pin=2001&start_dato=2018-11-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 102, '2018-09-02 15:00:00', 102, '2018-09-02 15:00:00', 6, 0);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, pool_overflow)
VALUES (203, (select uuid from meetings where id = 3), 1001, 2001, 30, '2018-12-02 14:30:00', 10, 1, 3, '1232@test.dk', '1232', 1, 'AWAITS_PROVISION', '', null, null, 'https://portal-test.vconf.dk/?url=1231@test.dk&pin=2001&start_dato=2018-12-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 101, '2018-09-02 15:00:00', 101, '2018-09-02 15:00:00', 5, 0);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, pool_overflow)
VALUES (204, (select uuid from meetings where id = 5) , 1001, 2001, 15, '2018-12-02 14:45:00', 10, 1, 5, '1233@test.dk', '1233', 1, 'AWAITS_PROVISION', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1233@test.dk&pin=2001&start_dato=2018-12-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 101, '2018-09-02 15:00:00', 101, '2018-09-02 15:00:00', 6, 0);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, pool_overflow)
VALUES (206, (select uuid from meetings where id = 6) , 1001, 2001, 15, '2018-10-02 14:45:00', 10, 1, 6, '1236@test.dk', '1236', 1, 'AWAITS_PROVISION', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1236@test.dk&pin=2001&start_dato=2018-10-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 104, NULL, NULL, NULL, 5, 0);

INSERT INTO scheduling_info (id, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, uuid, pool_overflow)
VALUES (207, 1001, 2001, 15, '2018-10-02 14:45:00', 10, 1, '1236@test.dk', '1237', 1, 'PROVISIONED_OK', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1236@test.dk&pin=2001&start_dato=2018-10-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 104, NULL, NULL, NULL, 7, '31293b98-0c51-447c-b10b-1886ab095450', 0);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, pool_overflow)
VALUES (208, (select uuid from meetings where id = 8) , 1001, 2001, 15, '2019-10-02 14:45:00', 10, 1, 8, '1238@test.dk', '1238', 1, 'PROVISIONED_OK', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1238@test.dk&pin=2001&start_dato=2019-10-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 104, NULL, NULL, NULL, 5, 0);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, pool_overflow)
VALUES (209, null, 1001, 2001, 15, '2020-10-02 14:45:00', 10, 1, null, '1239@test.dk', '1239', 1, 'PROVISIONED_OK', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1238@test.dk&pin=2001&start_dato=2019-10-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 104, NULL, NULL, NULL, 5, 0);

INSERT INTO scheduling_info (id, uuid, host_pin, guest_pin, vmravailable_before, vmrstart_time, max_participants, end_meeting_on_end_time, meetings_id, uri_with_domain, uri_without_domain, scheduling_template_id, provision_status, provision_status_description, provision_timestamp,  provisionvmrid, portal_link, ivr_theme, created_by, created_time, updated_by, updated_time, organisation_id, reservation_id, pool_overflow)
VALUES (210, null, 1001, 2001, 15, '2020-10-02 14:45:00', 10, 1, null, '1240@test.dk', '1240', 1, 'PROVISIONED_OK', 'all ok', null, null, 'https://portal-test.vconf.dk/?url=1238@test.dk&pin=2001&start_dato=2019-10-02T15:00:00', '/api/admin/configuration/v1/ivr_theme/10/', 104, NULL, NULL, NULL, 5, '1331e914-9488-482d-9d9e-d1302c44a1de', 0);


-- * scheduling_status *
INSERT INTO scheduling_status (id, time_stamp, provision_status, provision_status_description, meetings_id) VALUES (301, NOW(), 'AWAITS_PROVISION', 'all ok', 1);
INSERT INTO scheduling_status (id, time_stamp, provision_status, provision_status_description, meetings_id) VALUES (302, NOW(), 'AWAITS_PROVISION', '', 2);
INSERT INTO scheduling_status (id, time_stamp, provision_status, provision_status_description, meetings_id) VALUES (303, NOW(), 'AWAITS_PROVISION', '', 3);

-- * meeting_labels *
insert into meeting_labels(id, meeting_id, label) values(301, 7, 'first label');
insert into meeting_labels(id, meeting_id, label) values(302, 7, 'second label');
