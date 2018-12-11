ALTER TABLE meetings
ADD COLUMN organized_by bigint(20),
ADD COLUMN project_code varchar(20),
ADD FOREIGN KEY (organized_by) REFERENCES meeting_users(id);

ALTER TABLE scheduling_info
ADD COLUMN portal_link varchar(200),
ADD COLUMN ivr_theme varchar(100);

ALTER TABLE scheduling_template
ADD COLUMN ivr_theme varchar(100);

-- set default template to 10
update scheduling_template
set  ivr_theme = '10'
where organisation_id IS NULL;

-- set any existing meeting organized = created before making column not nullable
update meetings
set organized_by = created_by;

ALTER TABLE meetings
MODIFY COLUMN organized_by bigint(20) NOT NULL;

---

ALTER TABLE meetings
ADD COLUMN updated_by bigint(20),
ADD COLUMN created_time datetime,
ADD COLUMN updated_time datetime,
ADD FOREIGN KEY (updated_by) REFERENCES meeting_users(id);

ALTER TABLE scheduling_info
ADD COLUMN created_by bigint(20),
ADD COLUMN updated_by bigint(20),
ADD COLUMN created_time datetime,
ADD COLUMN updated_time datetime,
ADD FOREIGN KEY (created_by) REFERENCES meeting_users(id),
ADD FOREIGN KEY (updated_by) REFERENCES meeting_users(id);
