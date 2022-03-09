alter table scheduling_info add column uri_domain varchar(100) null after uri_without_domain;

update scheduling_info
    set uri_domain = substring(uri_with_domain, locate('@', uri_with_domain)+1)
    where provision_status != 'DEPROVISION_OK';

create unique index idx_uri_without_domain_uri_domain on scheduling_info(uri_without_domain, uri_domain);

drop index uri_without_domain on scheduling_info;