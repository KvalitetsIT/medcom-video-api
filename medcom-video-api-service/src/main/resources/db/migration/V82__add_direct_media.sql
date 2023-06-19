ALTER TABLE scheduling_template ADD COLUMN direct_media varchar(11) not null default 'never';
ALTER TABLE scheduling_info ADD COLUMN direct_media varchar(11) not null default 'never';
