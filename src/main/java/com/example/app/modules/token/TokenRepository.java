package com.example.app.modules.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByRefreshTokenHash(String refreshTokenHash);

    void deleteByUser_Id(UUID userId);
}
