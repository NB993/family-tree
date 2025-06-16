package io.jhchoe.familytree.common.auth.handler;

import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.util.MaskingUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 로그인 성공 시 JWT 토큰을 HttpOnly 쿠키로 발급하는 핸들러
 * 
 * URL 파라미터 대신 보안이 강화된 HttpOnly 쿠키로 토큰을 전달합니다.
 * 이는 XSS 공격으로부터 토큰을 보호하고 브라우저 히스토리 노출을 방지합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2JwtSuccessHandler implements AuthenticationSuccessHandler {

    private final GenerateJwtTokenUseCase generateJwtTokenUseCase;

    /**
     * OAuth2 인증 성공 시 JWT 토큰을 HttpOnly 쿠키로 설정하고 프론트엔드로 리다이렉트합니다.
     *
     * @param request        HTTP 요청
     * @param response       HTTP 응답
     * @param authentication 인증 정보 (FTUser 포함)
     * @throws IOException 리다이렉트 실패 시
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        // 보안 감사 추적용 정보 수집
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        log.info("OAuth2 인증 성공 [Client IP: {}] [User Agent: {}]", 
                MaskingUtils.maskEmail(clientIp), // IP도 일부 마스킹
                userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 50)) + "..." : "Unknown");
        
        try {
            // 인증된 사용자 정보 추출
            final FTUser ftUser = (FTUser) authentication.getPrincipal();
            
            // 개인정보 보호를 위한 마스킹된 로깅
            log.info("JWT 토큰 발급 대상 사용자 [User ID: {}] [Email: {}] [Name: {}]", 
                    ftUser.getId(), 
                    MaskingUtils.maskEmail(ftUser.getEmail()),
                    MaskingUtils.maskName(ftUser.getName()));
            
            // JWT 토큰 생성
            final GenerateJwtTokenCommand command = new GenerateJwtTokenCommand(ftUser);
            final JwtTokenResponse tokenResponse = generateJwtTokenUseCase.generateToken(command);
            
            // HttpOnly 쿠키로 토큰 설정
            setSecureTokenCookies(response, tokenResponse);
            
            // 성공만 알리고, 사용자 정보는 /api/users/me API로 조회하도록 함
            final String frontendUrl = "http://localhost:3000/auth/callback?success=true";
            
            response.sendRedirect(frontendUrl);
            
            // 보안 이벤트 성공 로깅 (감사 추적용)
            log.info("OAuth2 JWT 토큰 발급 완료 [User ID: {}] [Token Type: HttpOnly Cookie] [Client IP: {}] [리다이렉트: {}]", 
                    ftUser.getId(), 
                    MaskingUtils.maskEmail(clientIp),
                    frontendUrl);
            
        } catch (Exception e) {
            // 보안 이벤트 실패 로깅 (감사 추적용)
            log.error("OAuth2 JWT 토큰 발급 중 오류 발생 [Client IP: {}] [Error: {}]", 
                    MaskingUtils.maskEmail(clientIp), 
                    e.getMessage(), e);
            handleTokenGenerationError(response, e);
        }
    }

    /**
     * JWT 토큰을 보안이 강화된 HttpOnly 쿠키로 설정합니다.
     * 
     * @param response HTTP 응답
     * @param tokenResponse JWT 토큰 응답
     */
    private void setSecureTokenCookies(HttpServletResponse response, JwtTokenResponse tokenResponse) {
        // Access Token 쿠키 설정
        Cookie accessTokenCookie = new Cookie("accessToken", tokenResponse.accessToken());
        accessTokenCookie.setHttpOnly(true);  // XSS 공격 방지
        accessTokenCookie.setSecure(false);   // 개발환경에서는 false, 운영환경에서는 true (HTTPS)
        accessTokenCookie.setPath("/");       // 전체 애플리케이션에서 사용
        accessTokenCookie.setMaxAge(60 * 5); // 5분 (초 단위)
        accessTokenCookie.setAttribute("SameSite", "Lax"); // CSRF 공격 방지
        response.addCookie(accessTokenCookie);
        
        // Refresh Token 쿠키 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);   // 개발환경에서는 false
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 (초 단위)
        refreshTokenCookie.setAttribute("SameSite", "Lax");
        response.addCookie(refreshTokenCookie);
        
        // 쿠키 설정 완료 (상세 정보는 DEBUG 레벨로)
        log.debug("보안 쿠키 설정 완료 [AccessToken: {}분] [RefreshToken: {}일] [HttpOnly: true] [SameSite: Lax]", 
                (60 * 5) / 60, // 5분을 분 단위로 표시
                (7 * 24 * 60 * 60) / (24 * 60 * 60)); // 7일을 일 단위로 표시
    }

    /**
     * 토큰 생성 오류 발생 시 프론트엔드로 에러와 함께 리다이렉트합니다.
     */
    private void handleTokenGenerationError(HttpServletResponse response, @SuppressWarnings("unused") Exception e) throws IOException {
        final String frontendUrl = "http://localhost:3000/auth/callback" +
                "?success=false" +
                "&error=" + URLEncoder.encode("토큰 생성 실패", StandardCharsets.UTF_8);
        
        response.sendRedirect(frontendUrl);
        
        // 에러 리다이렉트 로깅 (실제 에러 메시지는 URL에 노출하지 않음)
        log.warn("OAuth2 인증 실패로 에러 페이지 리다이렉트 [URL: {}]", frontendUrl);
    }

    /**
     * 실제 클라이언트 IP 주소를 추출합니다.
     * 프록시나 로드밸런서를 고려하여 X-Forwarded-For 헤더도 확인합니다.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For는 쉼표로 구분된 IP 목록일 수 있으므로 첫 번째 IP 사용
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
