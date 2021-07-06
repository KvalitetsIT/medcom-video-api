ALTER TABLE scheduling_template
ADD COLUMN vmr_type varchar(11),
ADD COLUMN host_view varchar(30),
ADD COLUMN guest_view varchar(30),
ADD COLUMN vmr_quality varchar(7),
ADD COLUMN enable_overlay_text bit(1) not NULL default 1,
ADD COLUMN guests_can_present bit(1) not NULL default 1,
ADD COLUMN force_presenter_into_main bit(1) not NULL default 1,
ADD COLUMN force_encryption bit(1) not NULL default 0,
ADD COLUMN mute_all_guests bit(1) not NULL default 0;

ALTER TABLE scheduling_info
ADD COLUMN vmr_type varchar(11),
ADD COLUMN host_view varchar(30),
ADD COLUMN guest_view varchar(30),
ADD COLUMN vmr_quality varchar(7),
ADD COLUMN enable_overlay_text bit(1) not NULL default 1,
ADD COLUMN guests_can_present bit(1) not NULL default 1,
ADD COLUMN force_presenter_into_main bit(1) not NULL default 1,
ADD COLUMN force_encryption bit(1) not NULL default 0,
ADD COLUMN mute_all_guests bit(1) not NULL default 0;

-- set defaults scheduling_template
update scheduling_template
set  vmr_type = 'CONFERENCE'
where vmr_type IS NULL;

update scheduling_template
set  host_view = 'ONE_MAIN_SEVEN_PIPS'
where host_view IS NULL;

update scheduling_template
set  guest_view = 'ONE_MAIN_SEVEN_PIPS'
where guest_view IS NULL;

update scheduling_template
set  vmr_quality = 'HD'
where vmr_quality IS NULL;

-- set defaults scheduling_info
update scheduling_info
set  vmr_type = 'CONFERENCE'
where vmr_type IS NULL;

update scheduling_info
set  host_view = 'ONE_MAIN_SEVEN_PIPS'
where host_view IS NULL;

update scheduling_info
set  guest_view = 'ONE_MAIN_SEVEN_PIPS'
where guest_view IS NULL;

update scheduling_info
set  vmr_quality = 'HD'
where vmr_quality IS NULL;
