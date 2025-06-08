package io.jhchoe.familytree.common.auth.application.port.in;

import java.util.Objects;

/**
 * 사용자 ID로 Refresh Token을 조회하기 위한 쿼리 객체입니다.
 */
public final class FindRefreshTokenByUserIdQuery {

    private final Long userId;

    /**
     * FindRefreshTokenByUserIdQuery 객체를 생성합니다.
     *
     * @param userId 조회할 사용자 ID
     * @throws IllegalArgumentException userId가 null인 경우
     */
    public FindRefreshTokenByUserIdQuery(final Long userId) {
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
        FindRefreshTokenByUserIdQuery that = (FindRefreshTokenByUserIdQuery) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "FindRefreshTokenByUserIdQuery{" +
                "userId=" + userId +
                '}';
    }
}
