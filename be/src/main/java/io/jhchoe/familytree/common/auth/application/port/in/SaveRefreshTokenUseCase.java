package io.jhchoe.familytree.common.auth.application.port.in;

/**
 * Refresh Token 저장을 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface SaveRefreshTokenUseCase {

    /**
     * Refresh Token을 저장합니다.
     * 기존 사용자 토큰이 있다면 교체(Upsert)됩니다.
     *
     * @param command 토큰 저장에 필요한 정보를 포함하는 커맨드 객체
     * @return 저장된 토큰의 ID
     */
    Long save(SaveRefreshTokenCommand command);
}
