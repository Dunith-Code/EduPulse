CREATE TABLE fee_charges (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institute_id  UUID           NOT NULL REFERENCES institutes (id),
    enrollment_id UUID           NOT NULL REFERENCES enrollments (id),
    student_id    UUID           NOT NULL REFERENCES students (id),
    batch_id      UUID           NOT NULL REFERENCES batches (id),
    period        DATE           NOT NULL,
    amount        NUMERIC(10, 2) NOT NULL CHECK (amount >= 0),
    due_date      DATE           NOT NULL,
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT now(),
    UNIQUE (enrollment_id, period)
);

CREATE INDEX idx_fee_charges_institute_period ON fee_charges (institute_id, period);
CREATE INDEX idx_fee_charges_student ON fee_charges (student_id);

CREATE TABLE audit_log (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institute_id UUID         NOT NULL REFERENCES institutes (id),
    actor_id     UUID         NOT NULL REFERENCES users (id),
    action       VARCHAR(50)  NOT NULL,
    entity_type  VARCHAR(50)  NOT NULL,
    entity_id    UUID,
    details      VARCHAR(1000),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_institute_created ON audit_log (institute_id, created_at DESC);