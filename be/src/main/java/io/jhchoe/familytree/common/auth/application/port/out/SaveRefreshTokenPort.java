package io.jhchoe.familytree.common.auth.application.port.out;

import io.jhchoe.familytree.common.auth.domain.RefreshToken;

/**
 * Refresh Token 저장을 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface SaveRefreshTokenPort {

    /**
     * Refresh Token을 저장합니다.
     * 기존 사용자 토큰이 있다면 교체(Upsert)됩니다.
     *
     * @param refreshToken 저장할 Refresh Token 도메인 객체
     * @return 저장된 토큰의 ID
     */
    Long save(RefreshToken refreshToken);
}
