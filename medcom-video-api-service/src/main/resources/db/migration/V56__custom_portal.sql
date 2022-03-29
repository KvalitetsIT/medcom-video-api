alter table scheduling_template add column custom_portal_guest varchar(200) null;
alter table scheduling_template add column custom_portal_host varchar(200) null;
alter table scheduling_info add column custom_portal_guest varchar(200) null after portal_link;
alter table scheduling_info add column custom_portal_host varchar(200) null after custom_portal_guest;
