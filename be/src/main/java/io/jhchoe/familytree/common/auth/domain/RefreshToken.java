package io.jhchoe.familytree.common.auth.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Refresh Token 도메인 엔티티입니다.
 * JWT Access Token 갱신을 위한 장기 토큰 정보를 관리합니다.
 */
public final class RefreshToken {

    private final Long id;
    private final Long userId;
    private final String tokenHash;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private RefreshToken(
        final Long id,
        final Long userId,
        final String tokenHash,
        final LocalDateTime expiresAt,
        final LocalDateTime createdAt,
        final LocalDateTime updatedAt
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(tokenHash, "tokenHash must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        
        if (tokenHash.isBlank()) {
            throw new IllegalArgumentException("tokenHash must not be blank");
        }

        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 새로운 Refresh Token을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param tokenHash 토큰 해시값