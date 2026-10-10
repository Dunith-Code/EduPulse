CREATE TABLE users (
                       id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       institute_id  UUID         NOT NULL REFERENCES institutes (id),
                       full_name     VARCHAR(150) NOT NULL,
                       email         VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(100) NOT NULL,
                       role          VARCHAR(30)  NOT NULL,
                       active        BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_users_email ON users (lower(email));
CREATE INDEX idx_users_institute ON users (institute_id);