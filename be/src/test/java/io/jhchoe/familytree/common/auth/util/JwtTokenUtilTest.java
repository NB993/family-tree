package io.jhchoe.familytree.common.auth.util;

import io.jhchoe.familytree.common.auth.config.JwtProperties;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.exception.InvalidTokenException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] JwtTokenUtilTest")
class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private FTUser testUser;

    @BeforeEach
    void setUp() {
        // JWT 설정 생성
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-for-jwt-token-signing-must-be-at-least-256-bits");
        jwtProperties.setAccessTokenExpiration(3600L); // 1시간
        jwtProperties.setRefreshTokenExpiration(604800L); // 7일
        jwtProperties.setIssuer("family-tree-test");

        // JwtTokenUtil 생성
        jwtTokenUtil = new JwtTokenUtil(jwtProperties);

        // Google OAuth2 속성 생성
        Map<String, Object> googleAttributes = Map.of(
            "sub", "123456789",
            "name", "테스트 사용자",
            "email", "test@example.com",
            "picture", "https://example.com/profile.jpg"
        );

        // 테스트용 FTUser 생성
        testUser = FTUser.ofOAuth2User(
            1L,
            "테스트 사용자",
            "test@example.com",
            OAuth2Provider.GOOGLE,
            googleAttributes
        );
    }

    @Test
    @DisplayName("유효한 FTUser로 Access Token 생성에 성공합니다")
    void generate_access_token_success_when_valid_user() {
        // when
        String accessToken = jwtTokenUtil.generateAccessToken(testUser);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();
        assertThat(accessToken.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성
    }

    @Test
    @DisplayName("null FTUser로 Access Token 생성 시 NullPointerException이 발생합니다")
    void throw_exception_when_ftuser_is_null() {
        assertThatThrownBy(() -> jwtTokenUtil.generateAccessToken(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("ftUser must not be null");
    }

    @Test
    @DisplayName("유효한 사용자 ID로 Refresh Token 생성에 성공합니다")
    void generate_refresh_token_success_when_valid_user_id() {
        // given
        Long userId = 1L;

        // when
        String refreshToken = jwtTokenUtil.generateRefreshToken(userId);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(refreshToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("null 사용자 ID로 Refresh Token 생성 시 NullPointerException이 발생합니다")
    void throw_exception_when_user_id_is_null() {
        assertThatThrownBy(() -> jwtTokenUtil.generateRefreshToken(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userId must not be null");
    }

    @Test
    @DisplayName("유효한 토큰 검증에 성공합니다")
    void validate_token_success_when_valid_token() {
        // given
        String token = jwtTokenUtil.generateAccessToken(testUser);

        // when
        boolean isValid = jwtTokenUtil.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 검증 시 InvalidTokenException이 발생합니다")
    void throw_invalid_token_exception_when_malformed_token() {
        // given
        String malformedToken = "invalid.token.format";

        // when & then
        assertThatThrownBy(() -> jwtTokenUtil.validateToken(malformedToken))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 토큰입니다");
    }

    @Test
    @DisplayName("null 토큰 검증 시 NullPointerException이 발생합니다")
    void throw_exception_when_token_is_null() {
        assertThatThrownBy(() -> jwtTokenUtil.validateToken(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("token must not be null");
    }

    @Test
    @DisplayName("유효한 토큰에서 사용자 ID 추출에 성공합니다")
    void extract_user_id_success_when_valid_token() {
        // given
        String token = jwtTokenUtil.generateAccessToken(testUser);

        // when
        Long extractedUserId = jwtTokenUtil.extractUserId(token);

        // then
        assertThat(extractedUserId).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("유효한 토큰에서 이메일 추출에 성공합니다")
    void extract_email_success_when_valid_token() {
        // given
        String token = jwtTokenUtil.generateAccessToken(testUser);

        // when
        String extractedEmail = jwtTokenUtil.extractEmail(token);

        // then
        assertThat(extractedEmail).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("유효한 토큰에서 사용자 이름 추출에 성공합니다")
    void extract_name_success_when_valid_token() {
        // given
        String token = jwtTokenUtil.generateAccessToken(testUser);

        // when
        String extractedName = jwtTokenUtil.extractName(token);

        // then
        assertThat(extractedName).isEqualTo(testUser.getName());
    }

    @Test
    @DisplayName("유효한 토큰에서 역할 추출에 성공합니다")
    void extract_role_success_when_valid_token() {
        // given
        String token = jwtTokenUtil.generateAccessToken(testUser);

        // when
        String extractedRole = jwtTokenUtil.extractRole(token);

        // then
        assertThat(extractedRole).isEqualTo("USER");
    }

    @Test
    @DisplayName("유효한 토큰의 만료 시간 확인에 성공합니다")
    void extract_expiration_success_when_valid_token() {
        // given
        String token = jwtTokenUtil.generateAccessToken(testUser);

        // when
        boolean isExpired = jwtTokenUtil.isTokenExpired(token);

        // then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("잘못된 토큰에서 사용자 ID 추출 시 InvalidTokenException이 발생합니다")
    void throw_invalid_token_exception_when_extract_user_id_from_invalid_token() {
        // given
        String invalidToken = "invalid.token.format";

        // when & then
        assertThatThrownBy(() -> jwtTokenUtil.extractUserId(invalidToken))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 토큰입니다");
    }
}
