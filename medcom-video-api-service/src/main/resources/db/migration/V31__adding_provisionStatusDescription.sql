ALTER TABLE scheduling_status
ADD COLUMN provision_status_description varchar(200);

ALTER TABLE scheduling_info
ADD COLUMN provision_status_description varchar(200);