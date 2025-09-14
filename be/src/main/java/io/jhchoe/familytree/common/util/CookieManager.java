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

        log.debug("토큰 쿠키 삭제 완료 [Secure: {}] [SameSite: {}]", isProd, isProd ? "None" : "Lax");
    }

    private ResponseCookie createTokenCookie(String name, String value, long maxAge, boolean isProd) {
        return ResponseCookie.from(name, value)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true) // XSS 방지
                .secure(isProd) // prod일 때만 Secure=true
                .sameSite(isProd ? "None" : "Lax") // 로컬일 때만 SameSite=Lax
            // 브라우저는 포트번호, 서브도메인 명이 다르더라도 Same Site로 판단한다(cors 보다 판단 기준이 느슨).
            // 최신 브라우저들은 SameSite=None이면 Secure=true일 때에만 쿠키를 브라우저에 저장한다.
            // https를 주기 까다로운 로컬환경에서는 보통 Secure=false를 주게되므로 로컬에서 SameSite=None을 주면 브라우저가 쿠키를 저장하지 않아 테스트가 번거롭다.
            // 때문에 로컬환경에서 Secure=false일 때에도 브라우저가 쿠키를 저장할 수 있도록 SameSite=Lax로 주려고 함.
                .build();
    }

    private boolean isProductionProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod")
            || Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }
}
