package com.example.app.modules.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailRefreshTokenRepository extends JpaRepository<EmailRefreshToken, UUID> {
    Optional<EmailRefreshToken> findByTokenHash(String tokenHash);

    void deleteByUser_Id(UUID userId);
}
