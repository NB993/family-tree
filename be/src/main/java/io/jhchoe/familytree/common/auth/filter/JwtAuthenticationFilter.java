package io.jhchoe.familytree.common.auth.filter;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.common.util.MaskingUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 토큰을 검증하고 Spring Security Context에 인증 정보를 설정하는 필터입니다.
 * HttpOnly 쿠키 또는 Authorization 헤더에서 Bearer 토큰을 추출하여 검증하고, 유효한 토큰인 경우 인증 정보를 설정합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenUtil jwtTokenUtil;

    /**
     * HTTP 요청에서 JWT 토큰을 추출하고 검증하여 인증 정보를 설정합니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException 입출력 예외
     */
    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // HttpOnly 쿠키 또는 Authorization 헤더에서 JWT 토큰 추출
            final String token = extractTokenFromRequest(request);

            // 토큰이 존재하고 현재 SecurityContext에 인증 정보가 없는 경우에만 처리
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateToken(token, request);
            }
        } catch (final FTException e) {
            log.debug("JWT 토큰 예외: {}", e.getMessage());
            String exceptionCode = e.getCode();
            request.setAttribute("JWT_EXCEPTION", AuthExceptionCode.ofCode(exceptionCode).name());
            SecurityContextHolder.clearContext();

        } catch (final Exception e) {
            log.error("JWT 토큰 처리 중 예상치 못한 오류가 발생했습니다: {}", e.getMessage(), e);
            request.setAttribute("JWT_EXCEPTION", AuthExceptionCode.UNAUTHORIZED.name());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰을 추출합니다.
     * 우선순위: 1) HttpOnly 쿠키, 2) Authorization 헤더
     *
     * @param request HTTP 요청
     * @return 추출된 JWT 토큰, 없으면 null
     */
    private String extractTokenFromRequest(final HttpServletRequest request) {
        // 1. HttpOnly 쿠키에서 토큰 추출 (보안 우선)
        String token = extractTokenFromCookie(request);
        if (token != null) {
            log.debug("HttpOnly 쿠키에서 JWT 토큰 추출됨");
            return token;
        }

        // 2. Authorization 헤더에서 토큰 추출 (백워드 호환성)
        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            log.debug("Authorization 헤더에서 JWT 토큰 추출됨");
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * HTTP 요청의 쿠키에서 accessToken을 추출합니다.
     *
     * @param request HTTP 요청
     * @return 추출된 JWT 토큰, 없으면 null
     */
    private String extractTokenFromCookie(final HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * JWT 토큰을 검증하고 인증 정보를 SecurityContext에 설정합니다.
     *
     * @param token JWT 토큰
     * @param request HTTP 요청
     */
    private void authenticateToken(final String token, final HttpServletRequest request) {
        // 토큰 유효성 검증
        if (jwtTokenUtil.validateToken(token)) {
            // 토큰에서 사용자 정보 추출
            final Long userId = jwtTokenUtil.extractUserId(token);
            final String email = jwtTokenUtil.extractEmail(token);
            final String name = jwtTokenUtil.extractName(token);
            final String role = jwtTokenUtil.extractRole(token);

            // FTUser 객체 생성 (JWT 인증용)
            final FTUser ftUser = createJwtFTUser(userId, name, email, role);

            // Spring Security 인증 토큰 생성
            final UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(
                    ftUser,
                    null,
                    ftUser.getAuthorities()
                );

            // 웹 인증 세부 정보 설정
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.debug("JWT 인증 성공: [User ID: {}] [Masked Email: {}]", 
                userId, MaskingUtils.maskEmail(email));
        }
    }

    /**
     * JWT 토큰에서 추출한 정보로 FTUser 객체를 생성합니다.
     *
     * @param userId 사용자 ID
     * @param name 사용자 이름
     * @param email 이메일
     * @param role 역할
     * @return 생성된 FTUser 객체
     */
    private FTUser createJwtFTUser(
        final Long userId,
        final String name,
        final String email,
        final String role
    ) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(role, "role must not be null");

        // JWT 전용 팩토리 메서드 사용
        return FTUser.ofJwtUser(userId, name, email, role);
    }

    /**
     * 특정 요청에 대해 이 필터를 적용하지 않아야 하는지 결정합니다.
     *
     * @param request HTTP 요청
     * @return 필터를 건너뛰어야 하면 true, 그렇지 않으면 false
     */
    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final String path = request.getRequestURI();
        
        // 정적 리소스 및 공개 엔드포인트는 JWT 필터 적용 제외
        return path.equals("/favicon.ico") ||
               path.startsWith("/docs/") ||
               path.startsWith("/h2-console/") ||
               path.equals("/signup") ||
               path.equals("/") ||
               path.equals("/login") ||
               path.equals("/logout");
    }
}
