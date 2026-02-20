CREATE TABLE refresh_token (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(250) NOT NULL,
    expire_date TIMESTAMP,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_user_refresh_token FOREIGN KEY (user_id) REFERENCES users(id)

);