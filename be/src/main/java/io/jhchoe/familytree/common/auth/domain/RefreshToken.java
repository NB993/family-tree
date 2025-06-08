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
     * @param expiresAt 토큰 만료 시간
     * @return 새로운 RefreshToken 객체
     */
    public static RefreshToken create(
        final Long userId,
        final String tokenHash,
        final LocalDateTime expiresAt
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new RefreshToken(null, userId, tokenHash, expiresAt, now, now);
    }

    /**
     * ID를 포함한 Refresh Token을 생성합니다. (재구성용)
     *
     * @param id 토큰 ID
     * @param userId 사용자 ID
     * @param tokenHash 토큰 해시값
     * @param expiresAt 토큰 만료 시간
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return RefreshToken 객체
     */
    public static RefreshToken withId(
        final Long id,
        final Long userId,
        final String tokenHash,
        final LocalDateTime expiresAt,
        final LocalDateTime createdAt,
        final LocalDateTime updatedAt
    ) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, createdAt, updatedAt);
    }

    /**
     * 토큰 ID를 반환합니다.
     *
     * @return 토큰 ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 사용자 ID를 반환합니다.
     *
     * @return 사용자 ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 토큰 해시값을 반환합니다.
     *
     * @return 토큰 해시값
     */
    public String getTokenHash() {
        return tokenHash;
    }

    /**
     * 토큰 만료 시간을 반환합니다.
     *
     * @return 토큰 만료 시간
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * 생성 시간을 반환합니다.
     *
     * @return 생성 시간
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 수정 시간을 반환합니다.
     *
     * @return 수정 시간
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 토큰이 만료되었는지 확인합니다.
     *
     * @param currentDateTime 현재 시간
     * @return 만료 여부
     */
    public boolean isExpired(LocalDateTime currentDateTime) {
        Objects.requireNonNull(currentDateTime, "currentDateTime must not be null");
        return expiresAt.isBefore(currentDateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(tokenHash, that.tokenHash) &&
                Objects.equals(expiresAt, that.expiresAt) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, tokenHash, expiresAt, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", tokenHash='[PROTECTED]'" +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
