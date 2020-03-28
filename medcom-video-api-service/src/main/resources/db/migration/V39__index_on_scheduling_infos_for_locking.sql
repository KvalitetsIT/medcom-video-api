/* USED so that the entire table is not locked when trying to find a provisioned si from the pool */
create unique index idx_si_org_status_meeting_ud on scheduling_info(organisation_id, provision_status, meetings_id, id);
