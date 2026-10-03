CREATE TABLE institutes (
                            id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            name       VARCHAR(150) NOT NULL,
                            created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);