package io.jhchoe.familytree.common.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * JWT 토큰 갱신 요청 DTO입니다.
 *
 * @param refreshToken 갱신에 사용할 Refresh Token
 */
public record TokenRefreshRequest(
    @NotBlank(message = "Refresh Token은 필수입니다.")
    String refreshToken
) {
}
