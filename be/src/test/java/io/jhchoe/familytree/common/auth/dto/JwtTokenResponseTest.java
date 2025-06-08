package io.jhchoe.familytree.common.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Unit Test] JwtTokenResponseTest")
class JwtTokenResponseTest {

    @Test
    @DisplayName("JWT 토큰 응답 생성에 성공합니다")
    void create_jwt_token_response_success() {
        // given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        long expiresIn = 3600L;

        // when
        JwtTokenResponse response = JwtTokenResponse.of(accessToken, refreshToken, expiresIn);

        // then
        assertThat(response.accessToken()).isEqualTo(accessToken);
        assertThat(response.refreshToken()).isEqualTo(refreshToken);
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(expiresIn);
    }

    @Test
    @DisplayName("직접 생성자를 통한 JWT 토큰 응답 생성에 성공합니다")
    void create_jwt_token_response_with_constructor_success() {
        // given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        String tokenType = "Bearer";
        long expiresIn = 3600L;

        // when
        JwtTokenResponse response = new JwtTokenResponse(accessToken, refreshToken, tokenType, expiresIn);

        // then
        assertThat(response.accessToken()).isEqualTo(accessToken);
        assertThat(response.refreshToken()).isEqualTo(refreshToken);
        assertThat(response.tokenType()).isEqualTo(tokenType);
        assertThat(response.expiresIn()).isEqualTo(expiresIn);
    }
}
