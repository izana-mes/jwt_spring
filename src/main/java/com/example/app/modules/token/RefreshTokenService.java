package com.example.app.modules.token;

import com.example.app.common.exception.TokenRefreshException;
import com.example.app.modules.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    private final TokenRepository tokenRepository;

    public RefreshToken createRefreshToken(User user) {
        // Delete existing refresh tokens for this user
        deleteByUserId(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshTokenHash(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000))
                .isRevoked(false)
                .build();

        return tokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return tokenRepository.findByRefreshTokenHash(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new TokenRefreshException(token.getRefreshTokenHash(),
                    "Refresh token was expired. Please make a new signin request");
        }

        // Update last used timestamp
        token.setLastUsedAt(LocalDateTime.now());
        return tokenRepository.save(token);
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        tokenRepository.deleteByUser_Id(userId);
    }

    @Transactional
    public void deleteByToken(String token) {
        tokenRepository.findByRefreshTokenHash(token).ifPresent(tokenRepository::delete);
    }
}
