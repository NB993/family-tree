package io.jhchoe.familytree.common.auth.application.port.out;

import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Refresh Token 조회를 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface FindRefreshTokenPort {

    /**
     * 사용자 ID로 Refresh Token을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 조회된 Refresh Token (없으면 Optional.empty())
     */
    Optional<RefreshToken> findByUserId(Long userId);

    /**
     * 만료된 Refresh Token들을 조회합니다.
     *
     * @param currentDateTime 현재 시간 (이 시간보다 이전에 만료된 토큰을 조회)
     * @return 만료된 Refresh Token 목록
     */
    List<RefreshToken> findExpiredTokens(LocalDateTime currentDateTime);
}
