package io.jhchoe.familytree.common.auth.dto;

/**
 * Access Token 전용 응답 DTO입니다.
 * Refresh Token은 HttpOnly 쿠키로 전송되므로 응답에 포함하지 않습니다.
 */
public record AccessTokenResponse(
    String accessToken,
    String tokenType,
    Long expiresIn
) {
    
    /**
     * AccessTokenResponse 생성자입니다.
     *
     * @param accessToken Access Token 값
     * @param tokenType   토큰 타입 (보통 "Bearer")
     * @param expiresIn   토큰 만료 시간 (초 단위)
     */
    public AccessTokenResponse {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("accessToken must not be null or empty");
        }
        if (tokenType == null || tokenType.trim().isEmpty()) {
            throw new IllegalArgumentException("tokenType must not be null or empty");
        }
        if (expiresIn == null || expiresIn <= 0) {
            throw new IllegalArgumentException("expiresIn must be positive");
        }
    }
}