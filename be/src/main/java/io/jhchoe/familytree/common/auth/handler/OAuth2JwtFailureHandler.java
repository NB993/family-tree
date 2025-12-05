package io.jhchoe.familytree.common.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.exception.ErrorResponse;
import io.jhchoe.familytree.common.exception.FTException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 로그인 실패 시 JSON 응답을 반환하는 핸들러입니다.
 * View 리다이렉트 대신 API 응답으로 에러를 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2JwtFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    /**
     * OAuth2 인증 실패 시 JSON 형식의 에러 응답을 반환합니다.
     *
     * @param request   HTTP 요청
     * @param response  HTTP 응답
     * @param exception 인증 예외
     * @throws IOException JSON 응답 작성 실패 시
     */
    @Override
    public void onAuthenticationFailure(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final AuthenticationException exception
    ) throws IOException {

        log.info("OAuth2 인증 실패 [Exception: {}]", exception.getClass().getSimpleName());
        log.debug("OAuth2 인증 실패 상세: {}", exception.getMessage());

        // 인증 실패 예외 생성
        final FTException ftException = new FTException(AuthExceptionCode.UNAUTHORIZED);

        // 공통 응답 구조로 에러 응답 생성
        final ErrorResponse errorResponse = ErrorResponse.commonException(ftException);

        // JSON 응답 설정
        response.setStatus(ftException.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    /**
     * 실제 클라이언트 IP 주소를 추출합니다.
     * 프록시나 로드밸런서를 고려하여 X-Forwarded-For 헤더도 확인합니다.
     *
     * 참고: 현재 사용하지 않지만, 향후 보안 감사나 의심스러운 로그인 시도 추적이 필요한 경우 활용할 수 있습니다.
     */
    private String getClientIpAddress(final HttpServletRequest request) {
        final String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For는 쉼표로 구분된 IP 목록일 수 있으므로 첫 번째 IP 사용
            return xForwardedFor.split(",")[0].trim();
        }

        final String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}