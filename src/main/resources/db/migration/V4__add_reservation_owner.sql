ALTER TABLE reservations
ADD COLUMN app_user_id BIGINT;

ALTER TABLE reservations
ADD CONSTRAINT fk_reservations_app_user
FOREIGN KEY (app_user_id)
REFERENCES app_users(id);

CREATE INDEX idx_reservations_app_user_id
ON reservations(app_user_id);