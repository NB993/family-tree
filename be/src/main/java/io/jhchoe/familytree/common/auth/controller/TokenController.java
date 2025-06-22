package io.jhchoe.familytree.common.auth.controller;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.ModifyJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.ModifyJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.AccessTokenResponse;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.dto.LogoutResponse;
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


    /**
     * 쿠키의 Refresh Token을 사용하여 새로운 Access Token을 발급합니다.
     * 
     * Refresh Token을 쿠키에서 읽어와 새로운 Access Token과 Refresh Token을 생성합니다.
     * 새로운 Refresh Token도 HttpOnly 쿠키로 다시 설정됩니다.
     *
     * @param request HTTP 요청 객체 (쿠키 읽기용)
     * @param response HTTP 응답 객체 (쿠키 설정용)
     * @return 새로 발급된 Access Token 정보
     */
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        log.debug("토큰 갱신 요청 (쿠키 기반)");

        // 쿠키에서 Refresh Token 추출
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            log.warn("Refresh Token 쿠키가 없습니다");
            return ResponseEntity.badRequest().build();
        }

        ModifyJwtTokenCommand command = new ModifyJwtTokenCommand(refreshToken);
        JwtTokenResponse tokenResponse = modifyJwtTokenUseCase.modify(command);

        // 새로운 Refresh Token을 HttpOnly 쿠키로 설정
        setSecureRefreshTokenCookie(response, tokenResponse.refreshToken());

        // Access Token만 응답 Body에 포함
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(
            tokenResponse.accessToken(),
            tokenResponse.tokenType(),
            tokenResponse.expiresIn()
        );

        log.debug("토큰 갱신 완료");
        return ResponseEntity.ok(accessTokenResponse);
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

        // 쿠키에서 Refresh Token 삭제
        clearRefreshTokenCookie(response);

        log.debug("로그아웃 완료: [User ID: {}]", user.getId());
        return ResponseEntity.ok(LogoutResponse.createSuccess());
    }

    /**
     * Refresh Token을 HttpOnly 보안 쿠키로 설정합니다.
     *
     * @param response HTTP 응답 객체
     * @param refreshToken 설정할 Refresh Token 값
     */
    private void setSecureRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        
        // 보안 설정
        refreshTokenCookie.setHttpOnly(true);    // JavaScript 접근 차단 (XSS 방지)
        refreshTokenCookie.setSecure(true);      // HTTPS에서만 전송
        refreshTokenCookie.setPath("/");         // 전체 도메인에서 사용
        refreshTokenCookie.setMaxAge(7 * 24 * 3600); // 7일 (초 단위)
        
        // SameSite 설정 (CSRF 방지)
        response.addHeader("Set-Cookie", 
            String.format("%s=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=Strict",
                refreshTokenCookie.getName(),
                refreshTokenCookie.getValue(),
                refreshTokenCookie.getMaxAge()
            )
        );
        
        log.debug("Refresh Token 쿠키 설정 완료");
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

    /**
     * Refresh Token 쿠키를 삭제합니다.
     *
     * @param response HTTP 응답 객체
     */
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 만료
        
        response.addHeader("Set-Cookie", 
            "refreshToken=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=Strict"
        );
        
        log.debug("Refresh Token 쿠키 삭제 완료");
    }
}
