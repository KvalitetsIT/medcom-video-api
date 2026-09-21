ALTER TABLE scheduling_template ADD COLUMN breakout_rooms tinyint(1) NOT NULL DEFAULT 0;
ALTER TABLE scheduling_info ADD COLUMN breakout_rooms tinyint(1) NOT NULL DEFAULT 0;
