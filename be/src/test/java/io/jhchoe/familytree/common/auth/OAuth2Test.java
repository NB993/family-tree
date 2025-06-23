package io.jhchoe.familytree.common.auth;

import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("[Acceptance Test] OAuth2 인증")
class OAuth2Test extends AcceptanceTestBase {

    @Nested
    @DisplayName("OAuth2 인증 통합 테스트")
    class AuthenticationIntegrationTest {

        @Test
        @DisplayName("인증되지 않은 사용자는 보호된 리소스에 접근할 수 없다")
        void unauthenticatedUserCannotAccessProtectedResource() throws Exception {
            // 인증이 필요한 엔드포인트에 접근 시도
            given()
                .when()
                .get("/api/user/me")
                .then()
                .statusCode(401); // Unauthorized
        }

        @Test
        @WithMockOAuth2User(
            id = 999L,
            name = "구글테스트",
            email = "google@example.com",
            provider = OAuth2Provider.GOOGLE
        )
        @DisplayName("GOOGLE: 인증된 사용자는 자신의 프로필 정보를 조회할 수 있다")
        void authenticatedUserCanAccessProtectedResource() throws Exception {
            // SecurityContext에서 인증 정보 확인 (JWT 인증 시스템과 동일한 토큰 타입)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);

            FTUser user = (FTUser) authentication.getPrincipal();
            assertThat(user.getId()).isEqualTo(999L);
            assertThat(user.getName()).isEqualTo("구글테스트");
            assertThat(user.getEmail()).isEqualTo("google@example.com");
            assertThat(user.getAuthType()).isEqualTo(AuthenticationType.OAUTH2);
            assertThat(user.getOAuth2Provider()).isEqualTo(OAuth2Provider.GOOGLE);

            // 인증이 필요한 엔드포인트에 접근 시도
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/user/me")
                .then()
                .statusCode(200)
                .body("id", is(999))
                .body("name", is("구글테스트"))
                .body("email", is("google@example.com"))
                .body("provider", is("GOOGLE"));
        }

        @Test
        @WithMockOAuth2User(
            id = 100L,
            name = "카카오테스트",
            email = "kakao@example.com",
            provider = OAuth2Provider.KAKAO
        )
        @DisplayName("KAKAO: 인증된 사용자는 자신의 프로필 정보를 조회할 수 있다")
        void customOAuth2UserAuthentication() throws Exception {
            // SecurityContext에서 인증 정보 확인 (JWT 인증 시스템과 동일한 토큰 타입)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);

            FTUser user = (FTUser) authentication.getPrincipal();
            assertThat(user.getId()).isEqualTo(100L);
            assertThat(user.getName()).isEqualTo("카카오테스트");
            assertThat(user.getEmail()).isEqualTo("kakao@example.com");
            assertThat(user.getOAuth2Provider()).isEqualTo(OAuth2Provider.KAKAO);

            // 인증이 필요한 엔드포인트에 접근 시도
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/user/me")
                .then()
                .statusCode(200)
                .body("id", is(100))
                .body("name", is("카카오테스트"))
                .body("email", is("kakao@example.com"))
                .body("provider", is("KAKAO"));
        }
    }
}
