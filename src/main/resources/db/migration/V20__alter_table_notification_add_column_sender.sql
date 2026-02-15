ALTER TABLE notifications
ADD COLUMN sender_id BIGINT;

ALTER TABLE notifications
ADD CONSTRAINT fk_sender_id FOREIGN KEY (sender_id) REFERENCES users(id);