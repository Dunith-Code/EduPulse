CREATE TABLE batches (
                         id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         institute_id UUID           NOT NULL REFERENCES institutes (id),
                         name         VARCHAR(100)   NOT NULL,
                         subject      VARCHAR(100)   NOT NULL,
                         monthly_fee  NUMERIC(10, 2) NOT NULL CHECK (monthly_fee >= 0),
                         day_of_week  VARCHAR(10)    NOT NULL,
                         start_time   TIME           NOT NULL,
                         end_time     TIME           NOT NULL,
                         active       BOOLEAN        NOT NULL DEFAULT TRUE,
                         created_at   TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_batches_institute ON batches (institute_id);

CREATE TABLE students (
                          id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          institute_id UUID         NOT NULL REFERENCES institutes (id),
                          full_name    VARCHAR(150) NOT NULL,
                          parent_name  VARCHAR(150) NOT NULL,
                          parent_phone VARCHAR(20)  NOT NULL,
                          qr_code      VARCHAR(64)  NOT NULL,
                          active       BOOLEAN      NOT NULL DEFAULT TRUE,
                          created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          UNIQUE (institute_id, qr_code)
);

CREATE INDEX idx_students_institute ON students (institute_id);