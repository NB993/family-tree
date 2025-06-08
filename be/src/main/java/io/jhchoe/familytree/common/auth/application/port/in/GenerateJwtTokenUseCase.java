package io.jhchoe.familytree.common.auth.application.port.in;

import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;

/**
 * JWT 토큰 생성 UseCase
 * OAuth2 로그인 성공 시 Access Token과 Refresh Token을 생성합니다.
 */
public interface GenerateJwtTokenUseCase {

    /**
     * 사용자 정보를 기반으로 JWT 토큰을 생성합니다.
     * 
     * Access Token과 Refresh Token을 모두 생성하며,
     * Refresh Token은 데이터베이스에 저장됩니다.
     *
     * @param command 토큰 생성에 필요한 사용자 정보를 포함한 Command 객체
     * @return 생성된 JWT 토큰 정보 (Access Token, Refresh Token, 만료 시간 포함)
     */
    JwtTokenResponse generateToken(GenerateJwtTokenCommand command);
}
