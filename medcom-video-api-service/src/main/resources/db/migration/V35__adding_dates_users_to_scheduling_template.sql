ALTER TABLE scheduling_template
ADD COLUMN created_by bigint(20),
ADD COLUMN updated_by bigint(20),
ADD COLUMN deleted_by bigint(20),
ADD COLUMN created_time datetime,
ADD COLUMN updated_time datetime,
ADD COLUMN deleted_time datetime,
ADD FOREIGN KEY (created_by) REFERENCES meeting_users(id),
ADD FOREIGN KEY (updated_by) REFERENCES meeting_users(id),
ADD FOREIGN KEY (deleted_by) REFERENCES meeting_users(id);
