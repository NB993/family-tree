package io.jhchoe.familytree.common.auth.application.port.in;

/**
 * JWT 토큰 삭제를 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface DeleteJwtTokenUseCase {

    /**
     * 사용자의 JWT 토큰을 삭제 처리합니다.
     * 해당 사용자의 모든 Refresh Token을 무효화합니다.
     *
     * @param command JWT 토큰 삭제에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @throws io.jhchoe.familytree.common.exception.FTException 사용자가 존재하지 않는 경우
     */
    void delete(DeleteJwtTokenCommand command);
}
