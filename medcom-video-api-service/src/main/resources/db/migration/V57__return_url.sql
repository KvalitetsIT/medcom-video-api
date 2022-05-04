alter table scheduling_template add column return_url varchar(200) null;
alter table scheduling_info add column return_url varchar(200) null after custom_portal_host;
