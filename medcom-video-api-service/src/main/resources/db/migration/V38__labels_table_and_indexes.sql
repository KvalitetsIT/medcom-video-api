CREATE TABLE meeting_labels (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  meeting_id bigint(20) not null,
  label varchar(1024) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (meeting_id) REFERENCES meetings(id)
);

create index start_time on meetings(start_time);

create index subject on meetings(subject);

create index uri_with_domain on scheduling_info(uri_with_domain);

create index label on meeting_labels(label);
