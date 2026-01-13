
ALTER TABLE post_like
ADD CONSTRAINT fk_post_like_user
FOREIGN KEY (user_id)
REFERENCES users(id)
ON DELETE CASCADE;


ALTER TABLE post_like
ADD CONSTRAINT fk_post_like_post
FOREIGN KEY (post_id)
REFERENCES posts(id)
ON DELETE CASCADE;


ALTER TABLE post_like
ADD CONSTRAINT uk_user_post_like
UNIQUE (user_id, post_id);
