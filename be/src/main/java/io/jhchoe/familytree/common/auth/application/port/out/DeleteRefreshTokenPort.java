package io.jhchoe.familytree.common.auth.application.port.out;

/**
 * Refresh Token 삭제를 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface DeleteRefreshTokenPort {

    /**
     * 사용자의 Refresh Token을 삭제합니다.
     *
     * @param userId 삭제할 토큰의 사용자 ID
     */
    void deleteByUserId(Long userId);
}
