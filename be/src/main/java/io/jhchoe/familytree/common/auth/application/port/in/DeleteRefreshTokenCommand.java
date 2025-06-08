package io.jhchoe.familytree.common.auth.application.port.in;

import java.util.Objects;

/**
 * Refresh Token 삭제를 위한 커맨드 객체입니다.
 * 로그아웃 시 사용자의 토큰을 삭제합니다.
 */
public final class DeleteRefreshTokenCommand {

    private final Long userId;

    /**
     * DeleteRefreshTokenCommand 객체를 생성합니다.
     *
     * @param userId 삭제할 토큰의 사용자 ID
     * @throws IllegalArgumentException userId가 null인 경우
     */
    public DeleteRefreshTokenCommand(final Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        this.userId = userId;
    }

    /**
     * 사용자 ID를 반환합니다.
     *
     * @return 사용자 ID
     */
    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteRefreshTokenCommand that = (DeleteRefreshTokenCommand) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "DeleteRefreshTokenCommand{" +
                "userId=" + userId +
                '}';
    }
}
