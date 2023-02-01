ALTER TABLE scheduling_template
ADD COLUMN is_pool_template tinyint(1) NOT NULL default false;