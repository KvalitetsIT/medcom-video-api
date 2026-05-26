CREATE TABLE participant
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    meeting_id   BIGINT                                             NOT NULL,
    type         ENUM ('ORGANISATION', 'USER', 'DEVICE', 'CITIZEN') NOT NULL,
    external_id  VARCHAR(255)                                       NOT NULL,
    organisation VARCHAR(255),
    role         ENUM ('GUEST', 'HOST')                             NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_participant_meeting
        FOREIGN KEY (meeting_id) REFERENCES meetings (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_participant_meeting_id ON participant (meeting_id);