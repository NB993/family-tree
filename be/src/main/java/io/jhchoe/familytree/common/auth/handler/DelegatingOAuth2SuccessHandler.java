package io.jhchoe.familytree.common.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Base64;

/**
 * OAuth2 인증 성공 시 초대 여부에 따라 적절한 핸들러로 위임하는 핸들러입니다.
 *
 * 쿠키에서 invite_code를 확인하여:
 * - invite_code 있음 → OAuth2InviteSuccessHandler (초대 수락 프로세스)
 * - invite_code 없음 → OAuth2JwtSuccessHandler (일반 로그인 프로세스)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DelegatingOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2JwtSuccessHandler jwtSuccessHandler;
    private final OAuth2InviteSuccessHandler inviteSuccessHandler;

    /**
     * OAuth2 인증 성공 시 호출되는 메서드입니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authentication 인증 정보
     * @throws IOException 입출력 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    public void onAuthenticationSuccess(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Authentication authentication
    ) throws IOException, ServletException {

        // 1. OAuth2AuthorizationRequest에서 invite_code 확인
        final String inviteCode = extractInviteCodeFromAuthorizationRequest(request);

        if (inviteCode != null && !inviteCode.isBlank()) {
            log.info("초대 수락 OAuth 감지: inviteCode={}", inviteCode);
            inviteSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        } else {
            log.info("일반 로그인 OAuth 감지");
            jwtSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        }
    }

    /**
     * OAuth2AuthorizationRequest의 additionalParameters에서 invite_code를 추출합니다.
     *
     * @param request HTTP 요청
     * @return 초대 코드 (없으면 null)
     */
    private String extractInviteCodeFromAuthorizationRequest(final HttpServletRequest request) {
        try {
            // 1. 쿠키에서 oauth2_auth_request 추출
            final Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return null;
            }

            Cookie authRequestCookie = null;
            for (Cookie cookie : cookies) {
                if ("oauth2_auth_request".equals(cookie.getName())) {
                    authRequestCookie = cookie;
                    break;
                }
            }

            if (authRequestCookie == null) {
                log.debug("oauth2_auth_request 쿠키를 찾을 수 없습니다");
                return null;
            }

            // 2. OAuth2AuthorizationRequest 역직렬화
            final String value = authRequestCookie.getValue();
            if (value == null || value.isEmpty()) {
                return null;
            }

            final byte[] decoded = Base64.getUrlDecoder().decode(value);
            final OAuth2AuthorizationRequest authorizationRequest =
                (OAuth2AuthorizationRequest) SerializationUtils.deserialize(decoded);

            if (authorizationRequest == null) {
                return null;
            }

            // 3. additionalParameters에서 invite_code 추출
            final Object inviteCode = authorizationRequest
                .getAdditionalParameters()
                .get("invite_code");

            if (inviteCode != null) {
                log.debug("OAuth2 인증 요청에서 초대 코드 추출 성공 [inviteCode: {}]", inviteCode);
                return inviteCode.toString();
            }

            return null;

        } catch (Exception e) {
            log.error("OAuth2 인증 요청에서 초대 코드 추출 실패", e);
            return null;
        }
    }
}
