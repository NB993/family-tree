package io.jhchoe.familytree.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.exception.ErrorResponse;
import io.jhchoe.familytree.common.exception.FTException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Spring Security에서 발생하는 인증/인가 예외를 공통 응답 구조로 처리하는 핸들러입니다.
 * JWT 토큰 관련 세부 예외들을 구분하여 적절한 응답을 생성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FTSpringSecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * 인증 실패 시 호출되는 메서드입니다.
     * JWT 토큰 관련 예외들을 구분하여 적절한 응답을 생성합니다.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        log.debug("Authentication failed: {} - {}", authException.getClass().getSimpleName(), authException.getMessage());

        // JWT 관련 예외 원인 분석
        FTException ftException = determineJwtException(request, authException.getMessage());
        
        // 공통 응답 구조로 에러 응답 생성
        ErrorResponse errorResponse = ErrorResponse.commonException(ftException);
        response.setStatus(ftException.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    /**
     * 인가 실패 시 호출되는 메서드입니다.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.debug("Access denied: {}", accessDeniedException.getMessage());

        ErrorResponse errorResponse = ErrorResponse.commonException(FTException.ACCESS_DENIED);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    /**
     * AuthenticationException의 메시지와 request 속성으로 적절한 FTException을 반환합니다.
     * 
     * @param request HTTP 요청 (JWT_EXCEPTION 속성 확인용)
     * @param message BadCredentialsException의 메시지 (AuthExceptionCode.name())
     * @return 해당하는 FTException
     */
    private FTException determineJwtException(HttpServletRequest request, String message) {
        // 1. JWT 필터에서 설정한 구체적인 예외 코드 확인
        String jwtExceptionCode = (String) request.getAttribute("JWT_EXCEPTION");
        if (jwtExceptionCode != null) {
            try {
                AuthExceptionCode exceptionCode = AuthExceptionCode.valueOf(jwtExceptionCode);
                return new FTException(exceptionCode);
            } catch (IllegalArgumentException e) {
                log.debug("Invalid JWT exception code in request attribute: {}", jwtExceptionCode);
            }
        }
        
        // 2. 메시지에서 AuthExceptionCode name으로 매칭 (기존 방식)
        if (message != null) {
            try {
                AuthExceptionCode exceptionCode = AuthExceptionCode.valueOf(message);
                return new FTException(exceptionCode);
            } catch (IllegalArgumentException e) {
                log.debug("Unknown exception code in message: {}", message);
            }
        }
        
        // 3. 기본값
        return FTException.UNAUTHORIZED;
    }
}
