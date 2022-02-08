/* USED so that the entire table is not locked when trying to find a provisioned si from the pool */
drop index idx_si_org_status_meeting_ud on scheduling_info;
create unique index idx_si_org_status_meeting_ud on scheduling_info(organisation_id, provision_status, meetings_id, id, vmr_type, host_view, guest_view, vmr_quality, enable_overlay_text, guests_can_present, force_presenter_into_main, force_encryption, mute_all_guests);
