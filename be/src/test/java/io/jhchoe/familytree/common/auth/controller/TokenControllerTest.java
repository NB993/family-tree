package io.jhchoe.familytree.common.auth.controller;

import io.jhchoe.familytree.common.auth.adapter.out.persistence.RefreshTokenJpaEntity;
import io.jhchoe.familytree.common.auth.adapter.out.persistence.RefreshTokenJpaRepository;
import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.LocalDateTime;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
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

    @Test
    @DisplayName("유효한 Refresh Token으로 토큰 갱신 시 200 OK와 새로운 토큰을 반환합니다")
    void modify_returns_200_and_new_tokens_when_valid_refresh_token() {
        // given
        User user = User.newUser(
            "test@example.com", 
            "테스트사용자", 
            "profile.jpg",
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
            null,
            LocalDateTime.now(),
            null,
            LocalDateTime.now()
        );
        UserJpaEntity savedUser = userJpaRepository.save(UserJpaEntity.ofOAuth2User(user));
        
        RefreshToken refreshToken = RefreshToken.newRefreshToken(
            savedUser.getId(),
            "valid.refresh.token.value",
            LocalDateTime.now().plusDays(7)
        );
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.from(refreshToken));

        String requestBody = """
            {
                "refreshToken": "valid.refresh.token.value"
            }
            """;

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
        .when()
            .post("/api/auth/refresh")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("accessToken", notNullValue())
            .body("refreshToken", notNullValue())
            .body("tokenType", equalTo("Bearer"))
            .body("expiresIn", notNullValue());
    }

    @Test
    @DisplayName("빈 Refresh Token으로 토큰 갱신 시 400 BAD REQUEST를 반환합니다")
    void modify_returns_400_when_empty_refresh_token() {
        // given
        String requestBody = """
            {
                "refreshToken": ""
            }
            """;

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
        .when()
            .post("/api/auth/refresh")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("존재하지 않는 Refresh Token으로 갱신 시 401 UNAUTHORIZED를 반환합니다")
    void modify_returns_401_when_nonexistent_refresh_token() {
        // given
        String requestBody = """
            {
                "refreshToken": "nonexistent.refresh.token"
            }
            """;

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
        .when()
            .post("/api/auth/refresh")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithMockOAuth2User(id = 1L, email = "test@example.com", name = "테스트사용자")
    @DisplayName("인증된 사용자의 로그아웃 시 200 OK와 성공 메시지를 반환합니다")
    void delete_returns_200_and_success_message_when_authenticated_user() {
        // given
        User user = User.withId(
            1L,
            "test@example.com", 
            "테스트사용자", 
            "profile.jpg",
            AuthenticationType.OAUTH2,
            OAuth2Provider.GOOGLE,
            UserRole.USER,
            false,
            null,
            LocalDateTime.now(),
            null,
            LocalDateTime.now()
        );
        UserJpaEntity savedUser = userJpaRepository.save(UserJpaEntity.ofOAuth2User(user));
        
        RefreshToken refreshToken = RefreshToken.newRefreshToken(
            savedUser.getId(),
            "user.refresh.token",
            LocalDateTime.now().plusDays(7)
        );
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.from(refreshToken));

        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .post("/api/auth/logout")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("success", equalTo(true))
            .body("message", equalTo("로그아웃이 성공적으로 완료되었습니다."));
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 로그아웃 시 401 UNAUTHORIZED를 반환합니다")
    void delete_returns_401_when_unauthenticated_user() {
        // when & then
        given()
            .postProcessors(SecurityMockMvcRequestPostProcessors.csrf())
        .when()
            .post("/api/auth/logout")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
