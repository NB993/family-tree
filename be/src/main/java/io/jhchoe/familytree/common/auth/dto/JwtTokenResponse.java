package io.jhchoe.familytree.common.auth.dto;

/**
 * JWT 토큰 응답을 위한 DTO입니다.
 *
 * @param accessToken Access Token
 * @param refreshToken Refresh Token
 * @param tokenType 토큰 타입 (기본값: "Bearer")
 * @param expiresIn Access Token 만료 시간(초)
 */
public record JwtTokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn
) {

    /**
     * JWT 토큰 응답을 생성합니다.
     *
     * @param accessToken Access Token
     * @param refreshToken Refresh Token
     * @param expiresIn Access Token 만료 시간(초)
     * @return 생성된 JwtTokenResponse
     */
    public static JwtTokenResponse of(
        final String accessToken,
        final String refreshToken,
        final long expiresIn
    ) {
        return new JwtTokenResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
