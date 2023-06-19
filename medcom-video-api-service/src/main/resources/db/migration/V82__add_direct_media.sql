ALTER TABLE scheduling_template ADD COLUMN direct_media varchar(11);
ALTER TABLE scheduling_info ADD COLUMN direct_media varchar(11);

update scheduling_template set direct_media = 'never';
update scheduling_info set direct_media = 'never';

alter table scheduling_template
    modify direct_media varchar(11) not null;

alter table scheduling_info
    modify direct_media varchar(11) not null;
