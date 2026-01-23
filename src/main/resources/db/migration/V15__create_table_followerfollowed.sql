CREATE TABLE follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_follower_user
        FOREIGN KEY (follower_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_followed_user
        FOREIGN KEY (followed_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_follower_followed
        UNIQUE (follower_id, followed_id),

    CONSTRAINT chk_not_self_follow
        CHECK (follower_id <> followed_id)
);
