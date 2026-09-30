ALTER TABLE participant
    CHANGE COLUMN external_id participant_id VARCHAR(255) NOT NULL,
    ADD COLUMN created_by BIGINT AFTER created_at,
    ADD COLUMN updated_by BIGINT AFTER updated_at,
    ADD CONSTRAINT fk_participant_created_by
        FOREIGN KEY (created_by) REFERENCES meeting_users (id),
    ADD CONSTRAINT fk_participant_updated_by
        FOREIGN KEY (updated_by) REFERENCES meeting_users (id);