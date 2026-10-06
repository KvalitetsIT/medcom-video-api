ALTER TABLE participant
    ADD COLUMN name VARCHAR(255) NOT NULL DEFAULT '' AFTER participant_id;

ALTER TABLE participant
    ALTER COLUMN name DROP DEFAULT;
