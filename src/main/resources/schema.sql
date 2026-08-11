-- Schema initialization for Aiven MySQL (sql_require_primary_key=ON compliance)

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
) ENGINE=InnoDB;
