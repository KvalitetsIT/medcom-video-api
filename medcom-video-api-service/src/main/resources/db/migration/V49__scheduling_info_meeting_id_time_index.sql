ALTER TABLE scheduling_info DROP INDEX idx_si_org_status_meeting_id_res_id;
create index idx_si_org_status_meeting_id_res_id_provis_time on scheduling_info(organisation_id, provision_status, meetings_id, reservation_id, provision_timestamp);

alter table scheduling_info add column pool bit(1) not NULL default 0;