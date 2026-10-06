ALTER TABLE participant
    ADD COLUMN full_name VARCHAR(255) NOT NULL DEFAULT '' AFTER participant_id;

ALTER TABLE participant
    ALTER COLUMN full_name DROP DEFAULT;
