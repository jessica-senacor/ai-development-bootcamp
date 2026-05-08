CREATE TABLE todo (
    id          UUID         PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    completed   BOOLEAN      NOT NULL DEFAULT FALSE,
    due_date    VARCHAR(255)
);
