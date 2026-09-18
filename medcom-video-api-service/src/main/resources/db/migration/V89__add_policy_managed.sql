ALTER TABLE scheduling_info ADD COLUMN policy_managed tinyint(1) NOT NULL DEFAULT 0;
CREATE INDEX idx_scheduling_info_policy_lookup ON scheduling_info (uri_with_domain, policy_managed);
