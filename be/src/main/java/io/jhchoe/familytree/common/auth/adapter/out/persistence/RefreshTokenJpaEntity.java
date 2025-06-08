package io.jhchoe.familytree.common.auth.adapter.out.persistence;

import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Refresh Token 엔티티를 DB에 저장하기 위한 JPA 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "refresh_tokens",
    indexes = {
        @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id", unique = true),
        @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
    }
)
public class RefreshTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * RefreshTokenJpaEntity 객체를 생성하는 생성자입니다.
     */
    public RefreshTokenJpaEntity(
        Long id,
        Long userId,
        String tokenHash,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * RefreshToken 도메인 객체로부터 JPA 엔티티를 생성합니다.
     *
     * @param refreshToken 도메인 객체
     * @return JPA 엔티티
     */
    public static RefreshTokenJpaEntity from(RefreshToken refreshToken) {
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
        
        return new RefreshTokenJpaEntity(
            refreshToken.getId(),
            refreshToken.getUserId(),
            refreshToken.getTokenHash(),
            refreshToken.getExpiresAt(),
            refreshToken.getCreatedAt(),
            refreshToken.getUpdatedAt()
        );
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return 도메인 객체
     */
    public RefreshToken toRefreshToken() {
        return RefreshToken.withId(
            id,
            userId,
            tokenHash,
            expiresAt,
            createdAt,
            updatedAt
        );
    }

    /**
     * 엔티티 저장 전 생성 시간을 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    /**
     * 엔티티 업데이트 전 수정 시간을 설정합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
