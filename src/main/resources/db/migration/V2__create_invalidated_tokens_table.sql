CREATE TABLE invalidated_tokens (
    token VARCHAR(255) NOT NULL PRIMARY KEY,
    expiry_date TIMESTAMP NOT NULL
);
