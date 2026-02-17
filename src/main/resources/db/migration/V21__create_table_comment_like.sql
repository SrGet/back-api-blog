CREATE TABLE IF NOT EXISTS comment_like (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    comment_id BIGINT NOT NULL,

    liked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_like_comment FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comment_id_like FOREIGN KEY (comment_id) REFERENCES comments(id),
    CONSTRAINT uk_user_comment UNIQUE (user_id, comment_id)

);