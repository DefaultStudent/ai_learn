CREATE TABLE IF NOT EXISTS mes_schema_version (version VARCHAR(32) PRIMARY KEY, applied_at TIMESTAMPTZ NOT NULL DEFAULT NOW());
INSERT INTO mes_schema_version(version) VALUES ('v0.1-bootstrap') ON CONFLICT DO NOTHING;
