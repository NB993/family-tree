package io.jhchoe.familytree.common.auth.application.port.in;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Refresh Token 저장을 위한 커맨드 객체입니다.
 */
public final class SaveRefreshTokenCommand {

    private final Long userId;
    private final String tokenHash;
    private final LocalDateTime expiresAt;

    /**
     * SaveRefreshTokenCommand 객체를 생성합니다.
     *
     * @param userId 사용자 ID
     * @param tokenHash 토큰 해시값
     * @param expiresAt 토큰 만료 시간
     * @throws IllegalArgumentException 필수 파라미터가 null이거나 부적절한 경우
     */
    public SaveRefreshTokenCommand(
        final Long userId,
        final String tokenHash,
        final LocalDateTime expiresAt
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(tokenHash, "tokenHash must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        
        if (tokenHash.isBlank()) {
            throw new IllegalArgumentException("tokenHash must not be blank");
        }

        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaveRefreshTokenCommand that = (SaveRefreshTokenCommand) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(tokenHash, that.tokenHash) &&
                Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tokenHash, expiresAt);
    }

    @Override
    public String toString() {
        return "SaveRefreshTokenCommand{" +
                "userId=" + userId +
                ", tokenHash='[PROTECTED]'" +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
