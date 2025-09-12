package io.jhchoe.familytree.common.auth.controller;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.ModifyJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.ModifyJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.dto.LogoutResponse;
import io.jhchoe.familytree.common.config.CorsProperties;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.util.CookieManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JWT 토큰 관련 API 컨트롤러입니다.
 * 토큰 갱신 및 로그아웃 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final ModifyJwtTokenUseCase modifyJwtTokenUseCase;
    private final DeleteJwtTokenUseCase deleteJwtTokenUseCase;
    private final CookieManager cookieManager;
    private final CorsProperties corsProperties;


    /**
     * 쿠키의 Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급하고 HttpOnly 쿠키로 설정합니다.
     *
     * Refresh Token을 쿠키에서 읽어와 새로운 Access Token과 Refresh Token을 생성합니다.
     * 새로운 Refresh Token도 HttpOnly 쿠키로 다시 설정됩니다.
     *
     * @param request HTTP 요청 객체 (쿠키 읽기용)
     * @param response HTTP 응답 객체 (쿠키 설정용)
     * @return 성공 시 200 OK, 실패 시 400 Bad Request
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        log.debug("토큰 갱신 요청 (쿠키 기반)");

        // 쿠키에서 Refresh Token 추출
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            log.warn("Refresh Token 쿠키가 없습니다");
            throw new FTException(AuthExceptionCode.REFRESH_TOKEN_MISSING);
        }

        ModifyJwtTokenCommand command = new ModifyJwtTokenCommand(refreshToken);
        JwtTokenResponse tokenResponse = modifyJwtTokenUseCase.modify(command);

        // CookieManager를 사용하여 새로운 토큰들을 HttpOnly 쿠키로 설정
        cookieManager.addSecureTokenCookies(response, tokenResponse);

        log.debug("토큰 갱신 및 쿠키 설정 완료");
        return ResponseEntity.ok().build();
    }

    /**
     * 현재 인증된 사용자를 로그아웃 처리합니다.
     *
     * 해당 사용자의 모든 Refresh Token을 무효화하고 쿠키도 삭제합니다.
     * 기존 Access Token은 5분 후 자동으로 만료됩니다.
     *
     * @param user 현재 인증된 사용자
     * @param response HTTP 응답 객체 (쿠키 삭제용)
     * @return 로그아웃 처리 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
        @AuthFTUser FTUser user,
        HttpServletResponse response
    ) {
        log.debug("로그아웃 요청: [User ID: {}]", user.getId());

        // DB에서 Refresh Token 삭제
        DeleteRefreshTokenCommand command = new DeleteRefreshTokenCommand(user.getId());
        deleteJwtTokenUseCase.delete(command);

        // CookieManager를 사용하여 쿠키에서 토큰 삭제
        cookieManager.clearTokenCookies(response);

        log.debug("로그아웃 완료: [User ID: {}]", user.getId());
        return ResponseEntity.ok(LogoutResponse.createSuccess());
    }

    /**
     * HTTP 요청의 쿠키에서 Refresh Token을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return Refresh Token 값 (없으면 null)
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    log.debug("쿠키에서 Refresh Token 발견");
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
