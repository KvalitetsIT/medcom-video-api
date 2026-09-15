ALTER TABLE meetings ADD COLUMN organisation_code varchar(250);
ALTER TABLE scheduling_template ADD COLUMN organisation_code varchar(250);
ALTER TABLE scheduling_info ADD COLUMN organisation_code varchar(250);
ALTER TABLE meeting_users ADD COLUMN organisation_code varchar(250);

UPDATE meetings m JOIN organisation o ON m.organisation_id = o.id SET m.organisation_code = o.organisation_id;
UPDATE scheduling_template st JOIN organisation o ON st.organisation_id = o.id SET st.organisation_code = o.organisation_id;
UPDATE scheduling_info si JOIN organisation o ON si.organisation_id = o.id SET si.organisation_code = o.organisation_id;
UPDATE meeting_users mu JOIN organisation o ON mu.organisation_id = o.id SET mu.organisation_code = o.organisation_id;

DROP INDEX idx_si_org_status_meeting_ud ON scheduling_info;
DROP INDEX idx_si_org_status_meeting_id_res_id_provis_time ON scheduling_info;


SET @fk := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'meetings'
              AND COLUMN_NAME = 'organisation_id' AND REFERENCED_TABLE_NAME = 'organisation' LIMIT 1);
SET @sql := CONCAT('ALTER TABLE meetings DROP FOREIGN KEY ', @fk);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

DROP INDEX organisation_external_id ON meetings;
ALTER TABLE meetings DROP COLUMN organisation_id, MODIFY COLUMN organisation_code varchar(250) NOT NULL;
CREATE UNIQUE INDEX organisation_external_id ON meetings(organisation_code, external_id);


SET @fk := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'scheduling_template'
              AND COLUMN_NAME = 'organisation_id' AND REFERENCED_TABLE_NAME = 'organisation' LIMIT 1);
SET @sql := CONCAT('ALTER TABLE scheduling_template DROP FOREIGN KEY ', @fk);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE scheduling_template DROP COLUMN organisation_id;


SET @fk := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'meeting_users'
              AND COLUMN_NAME = 'organisation_id' AND REFERENCED_TABLE_NAME = 'organisation' LIMIT 1);
SET @sql := CONCAT('ALTER TABLE meeting_users DROP FOREIGN KEY ', @fk);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx := (SELECT INDEX_NAME FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'meeting_users'
               AND COLUMN_NAME = 'organisation_id' AND SEQ_IN_INDEX = 1 LIMIT 1);
SET @sql := CONCAT('ALTER TABLE meeting_users DROP INDEX ', @idx);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE meeting_users DROP COLUMN organisation_id, MODIFY COLUMN organisation_code varchar(250) NOT NULL;
CREATE UNIQUE INDEX meeting_users_organisation_code_email ON meeting_users(organisation_code, email);

ALTER TABLE scheduling_info DROP COLUMN organisation_id, MODIFY COLUMN organisation_code varchar(250) NOT NULL;

CREATE UNIQUE INDEX idx_si_org_status_meeting_ud ON scheduling_info(organisation_code, provision_status, meetings_id, id, vmr_type, host_view, guest_view, vmr_quality, enable_overlay_text, guests_can_present, force_presenter_into_main, force_encryption, mute_all_guests);
CREATE INDEX idx_si_org_status_meeting_id_res_id_provis_time ON scheduling_info(organisation_code, provision_status, meetings_id, reservation_id, provision_timestamp);

CREATE OR REPLACE VIEW view_pool_history
AS SELECT h.organisation_code
        , CAST(NULL AS CHAR(100)) AS organisation_name
        , h.desired_pool_size
        , h.available_pool_rooms
        , h.status_time
     FROM pool_history h
;

DROP TABLE organisation;
