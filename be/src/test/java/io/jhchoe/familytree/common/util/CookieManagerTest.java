package io.jhchoe.familytree.common.util;

import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] CookieManager")
class CookieManagerTest {

    @Mock
    private Environment environment;

    @Mock
    private HttpServletResponse response;

    private CookieManager cookieManager;

    @BeforeEach
    void setUp() {
        cookieManager = new CookieManager(environment);
    }

    @Nested
    @DisplayName("addSecureTokenCookies 메서드는")
    class AddSecureTokenCookies {

        private JwtTokenResponse testTokenResponse;
        private ArgumentCaptor<String> setCookieHeaderCaptor;

        @BeforeEach
        void setUpNested() {
            testTokenResponse = JwtTokenResponse.of("testAccessToken", "testRefreshToken", 3600L);
            setCookieHeaderCaptor = ArgumentCaptor.forClass(String.class);
        }

        @Test
        @DisplayName("prod 프로필 활성화 시 Secure=true, SameSite=None으로 쿠키를 추가한다")
        void adds_cookies_with_secure_and_samesite_none_when_prod_profile_active() {
            // given
            given(environment.getActiveProfiles()).willReturn(new String[]{"prod"});

            // when
            cookieManager.addSecureTokenCookies(response, testTokenResponse);

            // then
            // addHeader가 2번 호출되었는지 먼저 검증
            then(response).should(times(2)).addHeader(eq("Set-Cookie"), anyString());
            // 캡처된 값을 가져와 검증
            then(response).should(times(2)).addHeader(eq("Set-Cookie"), setCookieHeaderCaptor.capture());

            String accessTokenHeader = setCookieHeaderCaptor.getAllValues().get(0);
            String refreshTokenHeader = setCookieHeaderCaptor.getAllValues().get(1);

            assertThat(accessTokenHeader).contains("accessToken=testAccessToken");
            assertThat(accessTokenHeader).contains("Path=/");
            assertThat(accessTokenHeader).contains("Max-Age=300");
            assertThat(accessTokenHeader).contains("HttpOnly");
            assertThat(accessTokenHeader).contains("Secure");
            assertThat(accessTokenHeader).contains("SameSite=None");

            assertThat(refreshTokenHeader).contains("refreshToken=testRefreshToken");
            assertThat(refreshTokenHeader).contains("Path=/");
            assertThat(refreshTokenHeader).contains("Max-Age=604800");
            assertThat(refreshTokenHeader).contains("HttpOnly");
            assertThat(refreshTokenHeader).contains("Secure");
            assertThat(refreshTokenHeader).contains("SameSite=None");
        }

        @Test
        @DisplayName("prod 프로필 비활성화 시 Secure=false, SameSite=Lax로 쿠키를 추가한다")
        void adds_cookies_with_secure_false_and_samesite_none_when_prod_profile_inactive() {
            // given
            given(environment.getActiveProfiles()).willReturn(new String[]{"local"});

            // when
            cookieManager.addSecureTokenCookies(response, testTokenResponse);

            // then
            then(response).should(times(2)).addHeader(eq("Set-Cookie"), anyString());
            then(response).should(times(2)).addHeader(eq("Set-Cookie"), setCookieHeaderCaptor.capture());

            String accessTokenHeader = setCookieHeaderCaptor.getAllValues().get(0);
            String refreshTokenHeader = setCookieHeaderCaptor.getAllValues().get(1);

            assertThat(accessTokenHeader).contains("accessToken=testAccessToken");
            assertThat(accessTokenHeader).contains("Path=/");
            assertThat(accessTokenHeader).contains("Max-Age=300");
            assertThat(accessTokenHeader).contains("HttpOnly");
            assertThat(accessTokenHeader).doesNotContain("Secure"); // Secure=false
            assertThat(accessTokenHeader).contains("SameSite=Lax");

            assertThat(refreshTokenHeader).contains("refreshToken=testRefreshToken");
            assertThat(refreshTokenHeader).contains("Path=/");
            assertThat(refreshTokenHeader).contains("Max-Age=604800");
            assertThat(refreshTokenHeader).contains("HttpOnly");
            assertThat(refreshTokenHeader).doesNotContain("Secure"); // Secure=false
            assertThat(refreshTokenHeader).contains("SameSite=Lax");
        }
    }

    @Nested
    @DisplayName("clearTokenCookies 메서드는")
    class ClearTokenCookies {

        private ArgumentCaptor<String> setCookieHeaderCaptor;

        @BeforeEach
        void setUpNested() {
            setCookieHeaderCaptor = ArgumentCaptor.forClass(String.class);
        }

        @Test
        @DisplayName("prod 프로필 활성화 시 Secure=true, SameSite=None으로 쿠키를 삭제한다")
        void clears_cookies_with_secure_and_samesite_none_when_prod_profile_active() {
            // given
            given(environment.getActiveProfiles()).willReturn(new String[]{"prod"});

            // when
            cookieManager.clearTokenCookies(response);

            // then
            then(response).should(times(2)).addHeader(eq("Set-Cookie"), anyString());
            then(response).should(times(2)).addHeader(eq("Set-Cookie"), setCookieHeaderCaptor.capture());

            String accessTokenHeader = setCookieHeaderCaptor.getAllValues().get(0);
            String refreshTokenHeader = setCookieHeaderCaptor.getAllValues().get(1);

            assertThat(accessTokenHeader).contains("accessToken=");
            assertThat(accessTokenHeader).contains("Path=/");
            assertThat(accessTokenHeader).contains("Max-Age=0");
            assertThat(accessTokenHeader).contains("HttpOnly");
            assertThat(accessTokenHeader).contains("Secure");
            assertThat(accessTokenHeader).contains("SameSite=None");

            assertThat(refreshTokenHeader).contains("refreshToken=");
            assertThat(refreshTokenHeader).contains("Path=/");
            assertThat(refreshTokenHeader).contains("Max-Age=0");
            assertThat(refreshTokenHeader).contains("HttpOnly");
            assertThat(refreshTokenHeader).contains("Secure");
            assertThat(refreshTokenHeader).contains("SameSite=None");
        }

        @Test
        @DisplayName("prod 프로필 비활성화 시 Secure=false, SameSite=Lax로 쿠키를 삭제한다")
        void clears_cookies_with_secure_false_and_samesite_none_when_prod_profile_inactive() {
            // given
            given(environment.getActiveProfiles()).willReturn(new String[]{"local"});

            // when
            cookieManager.clearTokenCookies(response);

            // then
            then(response).should(times(2)).addHeader(eq("Set-Cookie"), anyString());
            then(response).should(times(2)).addHeader(eq("Set-Cookie"), setCookieHeaderCaptor.capture());

            String accessTokenHeader = setCookieHeaderCaptor.getAllValues().get(0);
            String refreshTokenHeader = setCookieHeaderCaptor.getAllValues().get(1);

            assertThat(accessTokenHeader).contains("accessToken=");
            assertThat(accessTokenHeader).contains("Path=/");
            assertThat(accessTokenHeader).contains("Max-Age=0");
            assertThat(accessTokenHeader).contains("HttpOnly");
            assertThat(accessTokenHeader).doesNotContain("Secure"); // Secure=false
            assertThat(accessTokenHeader).contains("SameSite=Lax");

            assertThat(refreshTokenHeader).contains("refreshToken=");
            assertThat(refreshTokenHeader).contains("Path=/");
            assertThat(refreshTokenHeader).contains("Max-Age=0");
            assertThat(refreshTokenHeader).contains("HttpOnly");
            assertThat(refreshTokenHeader).doesNotContain("Secure"); // Secure=false
            assertThat(refreshTokenHeader).contains("SameSite=Lax");
        }
    }
}
