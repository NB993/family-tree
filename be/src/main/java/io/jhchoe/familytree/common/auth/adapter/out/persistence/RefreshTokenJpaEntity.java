package io.jhchoe.familytree.common.auth.adapter.out.persistence;

import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import io.jhchoe.familytree.common.support.ModifierBaseEntity;
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
public class RefreshTokenJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * RefreshTokenJpaEntity 객체를 생성하는 생성자입니다.
     */
    private RefreshTokenJpaEntity(
        Long id,
        Long userId,
        String tokenHash,
        LocalDateTime expiresAt,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
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
            refreshToken.getCreatedBy(),
            refreshToken.getCreatedAt(),
            refreshToken.getModifiedBy(),
            refreshToken.getModifiedAt()
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
            getCreatedBy(),
            getCreatedAt(),
            getModifiedBy(),
            getModifiedAt()
        );
    }
}
