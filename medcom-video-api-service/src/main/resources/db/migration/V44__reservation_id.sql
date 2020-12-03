alter table scheduling_info add column reservation_id varchar(36) NULL;

create unique index idx_si_reservation_id on scheduling_info(reservation_id);
