package io.jhchoe.familytree.common.auth.application.port.in;

/**
 * Refresh Token 삭제를 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface DeleteRefreshTokenUseCase {

    /**
     * 사용자의 Refresh Token을 삭제합니다.
     * 로그아웃 시 호출되어 토큰을 무효화합니다.
     *
     * @param command 삭제할 토큰의 사용자 ID를 포함하는 커맨드 객체
     */
    void delete(DeleteRefreshTokenCommand command);
}
