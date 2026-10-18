
CREATE TABLE api_versions (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(150) NOT NULL,
    version VARCHAR(50) NOT NULL,
    owner VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sunset_date DATE,
    replacement_version VARCHAR(50),
    CONSTRAINT uk_api_version UNIQUE (api_name, version),
    CONSTRAINT chk_api_status
        CHECK (status IN ('ACTIVE', 'DEPRECATED', 'RETIRED'))
);

CREATE TABLE api_usage (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(150) NOT NULL,
    version VARCHAR(50) NOT NULL,
    consumer VARCHAR(150) NOT NULL,
    requested_at TIMESTAMPTZ NOT NULL
);


CREATE INDEX idx_api_usage_name_version
    ON api_usage (api_name, version);

CREATE INDEX idx_api_usage_consumer
    ON api_usage (consumer);

CREATE INDEX idx_api_versions_status
    ON api_versions (status);
