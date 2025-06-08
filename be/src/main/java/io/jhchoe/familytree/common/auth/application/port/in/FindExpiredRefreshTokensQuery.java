package io.jhchoe.familytree.common.auth.application.port.in;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 만료된 Refresh Token들을 조회하기 위한 쿼리 객체입니다.
 * 배치 작업에서 만료된 토큰을 정리할 때 사용됩니다.
 */
public final class FindExpiredRefreshTokensQuery {

    private final LocalDateTime currentDateTime;

    /**
     * FindExpiredRefreshTokensQuery 객체를 생성합니다.
     *
     * @param currentDateTime 현재 시간 (이 시간보다 이전에 만료된 토큰을 조회)
     * @throws IllegalArgumentException currentDateTime이 null인 경우
     */
    public FindExpiredRefreshTokensQuery(final LocalDateTime currentDateTime) {
        Objects.requireNonNull(currentDateTime, "currentDateTime must not be null");
        this.currentDateTime = currentDateTime;
    }

    /**
     * 현재 시간을 반환합니다.
     *
     * @return 현재 시간
     */
    public LocalDateTime getCurrentDateTime() {
        return currentDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FindExpiredRefreshTokensQuery that = (FindExpiredRefreshTokensQuery) o;
        return Objects.equals(currentDateTime, that.currentDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentDateTime);
    }

    @Override
    public String toString() {
        return "FindExpiredRefreshTokensQuery{" +
                "currentDateTime=" + currentDateTime +
                '}';
    }
}
