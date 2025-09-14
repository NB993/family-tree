package io.jhchoe.familytree.common.auth.controller;

import io.jhchoe.familytree.common.auth.adapter.out.persistence.RefreshTokenJpaEntity;
import io.jhchoe.familytree.common.auth.adapter.out.persistence.RefreshTokenJpaRepository;
import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.LocalDateTime;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * JWT 토큰 관련 API 인수 테스트
 */
@DisplayName("[Acceptance Test] TokenControllerTest")
class TokenControllerTest extends AcceptanceTestBase {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @DisplayName("쿠키의 유효한 Refresh Token으로 토큰 갱신 시 200 OK와 새로운 토큰을 반환합니다")
    void modify_returns_200_and_new_tokens_when_valid_refresh_token_in_cookie() throws InterruptedException {
        // given
        User user = User.newUser(
            "test@example.com", 
            "테스트사용자", 
            "profile.jpg",
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false
        );
        UserJpaEntity savedUser = userJpaRepository.save(UserJpaEntity.ofOAuth2User(user));
        
        // 실제 유효한 Refresh Token 생성
        String validRefreshToken = jwtTokenUtil.generateRefreshToken(savedUser.getId());
        
        RefreshToken refreshToken = RefreshToken.newRefreshToken(
            savedUser.getId(),
            validRefreshToken,
            LocalDateTime.now().plusDays(7)
        );
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.from(refreshToken));

        // 시간 차이를 강제로 만들기
        Thread.sleep(1000);  // 1000ms 대기

        // when & then - 토큰 갱신 요청 성공 검증  
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .postProcessors(request -> {
                request.setCookies(new jakarta.servlet.http.Cookie("refreshToken", validRefreshToken));
                return request;
            })
        .when()
            .post("/api/auth/refresh")
        .then()
            .statusCode(HttpStatus.OK.value())
            .cookie("accessToken", notNullValue())
            .cookie("refreshToken", notNullValue());

        RefreshTokenJpaEntity newRefreshToken = refreshTokenJpaRepository.findByUserId(savedUser.getId()).get();
        assertThat(newRefreshToken.getTokenHash()).isNotEqualTo(validRefreshToken);
    }

    @Test
    @DisplayName("Refresh Token 쿠키가 없을 때 401 UNAUTHORIZED를 반환합니다")
    void modify_returns_401_when_no_refresh_token_cookie() {
        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .post("/api/auth/refresh")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("code", equalTo(AuthExceptionCode.REFRESH_TOKEN_MISSING.getCode()))
            .body("message", equalTo(AuthExceptionCode.REFRESH_TOKEN_MISSING.getMessage()));
    }

    @Test
    @DisplayName("잘못된 형식의 Refresh Token 쿠키로 갱신 시 401 UNAUTHORIZED를 반환합니다")
    void modify_returns_401_when_invalid_refresh_token_cookie() {
        // given - JWT 서명이 잘못된 토큰
        String invalidToken = "invalid.jwt.token.format";

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .postProcessors(request -> {
                request.setCookies(new jakarta.servlet.http.Cookie("refreshToken", invalidToken));
                return request;
            })
        .when()
            .post("/api/auth/refresh")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("code", equalTo(AuthExceptionCode.INVALID_TOKEN_FORMAT.getCode()))
            .body("message", equalTo(AuthExceptionCode.INVALID_TOKEN_FORMAT.getMessage()));
    }

    @Test
    @WithMockOAuth2User(id = 1L, email = "test@example.com", name = "테스트사용자")
    @DisplayName("인증된 사용자의 로그아웃 시 200 OK와 성공 메시지를 반환하고 RT를 삭제합니다")
    void delete_returns_200_and_success_message_when_authenticated_user() {
        // given
        User user = User.newUser(
            "test@example.com", 
            "테스트사용자", 
            "profile.jpg",
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false
        );
        UserJpaEntity savedUser = userJpaRepository.save(UserJpaEntity.ofOAuth2User(user));
        
        RefreshToken refreshToken = RefreshToken.newRefreshToken(
            savedUser.getId(),
            "user.refresh.token",
            LocalDateTime.now().plusDays(7)
        );

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .post("/api/auth/logout")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("success", equalTo(true))
            .body("message", equalTo("로그아웃이 성공적으로 완료되었습니다."))
            .cookie("refreshToken", "")
            .cookie("accessToken", ""); // accessToken도 삭제되었는지 확인
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 로그아웃 시 401 UNAUTHORIZED를 반환합니다")
    void delete_returns_401_when_unauthenticated_user() {
        // given - 인증 정보 없이 로그아웃 시도
        
        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .post("/api/auth/logout")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("code", equalTo(AuthExceptionCode.UNAUTHORIZED.getCode()))
            .body("message", equalTo(AuthExceptionCode.UNAUTHORIZED.getMessage()));
    }
}
