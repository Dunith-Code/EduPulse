CREATE TABLE enrollments (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institute_id UUID        NOT NULL REFERENCES institutes (id),
    student_id   UUID        NOT NULL REFERENCES students (id),
    batch_id     UUID        NOT NULL REFERENCES batches (id),
    enrolled_on  DATE        NOT NULL,
    active       BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (student_id, batch_id)
);

CREATE INDEX idx_enrollments_institute ON enrollments (institute_id);
CREATE INDEX idx_enrollments_batch ON enrollments (batch_id);

CREATE TABLE attendance (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institute_id UUID        NOT NULL REFERENCES institutes (id),
    batch_id     UUID        NOT NULL REFERENCES batches (id),
    student_id   UUID        NOT NULL REFERENCES students (id),
    session_date DATE        NOT NULL,
    status       VARCHAR(10) NOT NULL CHECK (status IN ('PRESENT', 'ABSENT', 'LATE')),
    method       VARCHAR(10) NOT NULL CHECK (method IN ('QR', 'MANUAL')),
    marked_by    UUID        NOT NULL REFERENCES users (id),
    marked_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (batch_id, student_id, session_date)
);

CREATE INDEX idx_attendance_institute ON attendance (institute_id);
CREATE INDEX idx_attendance_batch_date ON attendance (batch_id, session_date);