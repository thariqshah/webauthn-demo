-- Create the app_user table
CREATE TABLE IF NOT EXISTS app_user
(
    id       UUID PRIMARY KEY NOT NULL,
    username VARCHAR(255) UNIQUE,
    email    VARCHAR(255) UNIQUE
);

-- Create the login_code table
CREATE TABLE IF NOT EXISTS login_code
(
    id              UUID PRIMARY KEY NOT NULL,
    user_id         UUID NOT NULL,
    expiration_time TIMESTAMP DEFAULT DATEADD(MINUTE, 5, CURRENT_TIMESTAMP),
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

-- Create the authenticator table
CREATE TABLE IF NOT EXISTS authenticator
(
    id                 VARCHAR(255) PRIMARY KEY NOT NULL,
    user_id            UUID NOT NULL,
    credentials_name   VARCHAR(255) NOT NULL,
    attestation_object BLOB DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);
