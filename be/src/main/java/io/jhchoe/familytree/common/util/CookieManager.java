package io.jhchoe.familytree.common.util;

import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 환경에 따라 HttpOnly 쿠키의 보안 속성을 동적으로 관리하는 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CookieManager {

    private final Environment environment;

    /**
     * 환경(prod/local)에 따라 보안 속성이 적용된 토큰 쿠키를 응답에 추가합니다.
     *
     * @param response      HTTP 응답 객체
     * @param tokenResponse JWT 토큰 정보
     */
    public void addSecureTokenCookies(HttpServletResponse response, JwtTokenResponse tokenResponse) {
        boolean isProd = isProductionProfile();

        ResponseCookie accessTokenCookie = createTokenCookie("accessToken", tokenResponse.accessToken(), 60 * 5, isProd);
        ResponseCookie refreshTokenCookie = createTokenCookie("refreshToken", tokenResponse.refreshToken(), 7 * 24 * 60 * 60, isProd);

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        log.debug("보안 쿠키 설정 완료 [Secure: {}] [SameSite: {}]", isProd, isProd ? "None" : "Lax");
    }

    /**
     * 환경에 맞게 토큰 쿠키를 삭제합니다.
     *
     * @param response    HTTP 응답 객체
     */
    public void clearTokenCookies(HttpServletResponse response) {
        boolean isProd = isProductionProfile();

        ResponseCookie accessTokenCookie = createTokenCookie("accessToken", "", 0, isProd);
        ResponseCookie refreshTokenCookie = createTokenCookie("refreshToken", "", 0, isProd);

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        log.debug("토큰 쿠키 삭제 완료");
    }

    private ResponseCookie createTokenCookie(String name, String value, long maxAge, boolean isProd) {
        return ResponseCookie.from(name, value)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(isProd)
                .sameSite(isProd ? "None" : "Lax")
                .build();
    }

    private boolean isProductionProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }
}

