alter table scheduling_info
    modify uri_without_domain varchar(100) null;

update scheduling_info
    set uri_without_domain = null
 where provision_status = 'DEPROVISION_OK';

