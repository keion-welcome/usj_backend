CREATE TABLE profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    gender VARCHAR(255) NOT NULL,
    birthdate DATE NOT NULL,
    area VARCHAR(255) NOT NULL,
    occupation VARCHAR(255) NOT NULL,
    has_annual_pass BOOLEAN NOT NULL
);
