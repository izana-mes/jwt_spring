package com.example.app.modules.token;

import com.example.app.modules.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_refresh_tokens")
@EntityListeners(AuditingEntityListener.class)
public class EmailRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "email_refresh_tokens_id", updatable = false, nullable = false)
    private UUID emailRefreshTokenId;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "users_id", nullable = false)
    private User user;

    @Column(name = "tokens_hash", nullable = false, columnDefinition = "TEXT")
    private String tokenHash;

    @Column(name = "is_revoked")
    @Builder.Default
    private boolean isRevoked = false;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
