package io.jhchoe.familytree.common.auth.application.port.in;

import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;

/**
 * JWT 토큰 수정을 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface ModifyJwtTokenUseCase {

    /**
     * Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.
     * 기존 Refresh Token은 무효화되고 새로운 Refresh Token이 생성됩니다.
     *
     * @param command 토큰 갱신에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @return 새로 발급된 JWT 토큰 정보 (AccessToken + RefreshToken)
     * @throws io.jhchoe.familytree.common.exception.FTException 토큰이 유효하지 않거나 만료된 경우
     */
    JwtTokenResponse modify(ModifyJwtTokenCommand command);
}
