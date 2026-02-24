CREATE TABLE email_verification_tokens (
                                           verification_token_id UUID PRIMARY KEY,

                                           users_id UUID NOT NULL,

                                           token_hash TEXT NOT NULL,

                                           is_used BOOLEAN NOT NULL DEFAULT FALSE,

                                           expires_at TIMESTAMP NOT NULL,

                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                           CONSTRAINT fk_email_verification_token_user
                                               FOREIGN KEY (users_id)
                                                   REFERENCES users(users_id)
                                                   ON DELETE CASCADE
);

CREATE INDEX idx_email_verification_token_hash
    ON email_verification_tokens(token_hash);
