package io.jhchoe.familytree.common.auth.repository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 인증 요청을 쿠키에 저장하는 Repository입니다.
 * 완전한 Stateless 환경에서 세션 없이 OAuth2 인증 흐름을 처리합니다.
 * 서버가 여러 대여도 쿠키 기반이므로 세션 공유 문제가 없습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private static final int COOKIE_EXPIRE_SECONDS = 180; // 3분

    private final Environment environment;

    /**
     * 요청에서 OAuth2 인증 요청을 로드합니다.
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(final HttpServletRequest request) {
        return getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            .map(this::deserialize)
            .orElse(null);
    }

    /**
     * OAuth2 인증 요청을 쿠키에 저장합니다.
     * URL 파라미터에 invite_code가 있으면 additionalParameters에 포함시킵니다.
     */
    @Override
    public void saveAuthorizationRequest(
        final OAuth2AuthorizationRequest authorizationRequest,
        final HttpServletRequest request,
        final HttpServletResponse response
    ) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            log.debug("OAuth2 인증 요청이 null이므로 쿠키를 삭제합니다");
            return;
        }

        // 1. URL에서 invite_code 파라미터 추출
        final String inviteCode = request.getParameter("invite_code");

        // 2. invite_code가 있으면 OAuth2AuthorizationRequest에 추가
        OAuth2AuthorizationRequest modifiedRequest = authorizationRequest;
        if (inviteCode != null && !inviteCode.isBlank()) {
            log.info("초대 코드 감지. OAuth2 인증 요청에 포함 [inviteCode: {}]", inviteCode);

            // OAuth2AuthorizationRequest는 불변 객체이므로 Builder 사용
            Map<String, Object> additionalParams = new HashMap<>(
                authorizationRequest.getAdditionalParameters()
            );
            additionalParams.put("invite_code", inviteCode);

            modifiedRequest = OAuth2AuthorizationRequest
                .from(authorizationRequest)
                .additionalParameters(additionalParams)
                .build();
        }

        final String serialized = serialize(modifiedRequest);
        addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, serialized, COOKIE_EXPIRE_SECONDS);
        log.debug("OAuth2 인증 요청을 쿠키에 저장했습니다 [Cookie Size: {} bytes]", serialized.length());
    }

    /**
     * OAuth2 인증 요청을 쿠키에서 제거하고 반환합니다.
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
        final HttpServletRequest request,
        final HttpServletResponse response
    ) {
        final OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        if (authorizationRequest != null) {
            removeAuthorizationRequestCookies(request, response);
            log.debug("OAuth2 인증 요청 쿠키를 제거했습니다");
        }
        return authorizationRequest;
    }

    /**
     * OAuth2 인증 요청 쿠키를 삭제합니다.
     */
    public void removeAuthorizationRequestCookies(
        final HttpServletRequest request,
        final HttpServletResponse response
    ) {
        deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

    /**
     * 요청에서 특정 이름의 쿠키를 조회합니다.
     */
    private java.util.Optional<Cookie> getCookie(final HttpServletRequest request, final String name) {
        final Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return java.util.Optional.of(cookie);
                }
            }
        }

        return java.util.Optional.empty();
    }

    /**
     * 응답에 쿠키를 추가합니다.
     * 환경에 따라 Secure 속성을 동적으로 설정합니다.
     */
    private void addCookie(
        final HttpServletResponse response,
        final String name,
        final String value,
        final int maxAge
    ) {
        final boolean isProd = isProductionProfile();

        // SameSite=Lax로 설정하여 OAuth2 리다이렉트 시에도 쿠키 전달
        // Lax: Top-level navigation(GET)에서는 쿠키 전송, POST/PUT/DELETE는 Same-Site만
        final String securePart = isProd ? "; Secure" : "";
        response.addHeader("Set-Cookie", String.format(
            "%s=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax%s",
            name, value, maxAge, securePart
        ));

        log.debug("OAuth2 쿠키 저장 [Secure: {}] [SameSite: Lax]", isProd);
    }

    /**
     * 쿠키를 삭제합니다.
     * 저장 시와 동일한 속성(HttpOnly, SameSite, Secure)을 사용하여 브라우저가 정확히 삭제하도록 합니다.
     */
    private void deleteCookie(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final String name
    ) {
        final boolean isProd = isProductionProfile();
        final String securePart = isProd ? "; Secure" : "";

        response.addHeader("Set-Cookie", String.format(
            "%s=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax%s",
            name, securePart
        ));

        log.debug("OAuth2 쿠키 삭제 요청 [Secure: {}] [SameSite: Lax]", isProd);
    }

    /**
     * OAuth2AuthorizationRequest를 Base64로 직렬화합니다.
     * Java 직렬화를 사용하여 객체를 완전히 보존합니다.
     */
    private String serialize(final OAuth2AuthorizationRequest authorizationRequest) {
        try {
            final byte[] serialized = SerializationUtils.serialize(authorizationRequest);
            return Base64.getUrlEncoder().encodeToString(serialized);
        } catch (Exception e) {
            log.error("OAuth2 인증 요청 직렬화 실패", e);
            throw new IllegalStateException("OAuth2 인증 요청을 쿠키에 저장할 수 없습니다", e);
        }
    }

    /**
     * Base64 문자열을 OAuth2AuthorizationRequest로 역직렬화합니다.
     */
    private OAuth2AuthorizationRequest deserialize(final Cookie cookie) {
        try {
            final String value = cookie.getValue();
            if (value == null || value.isEmpty()) {
                return null;
            }

            final byte[] decoded = Base64.getUrlDecoder().decode(value);
            return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(decoded);
        } catch (Exception e) {
            log.error("OAuth2 인증 요청 역직렬화 실패", e);
            return null;
        }
    }

    /**
     * 현재 환경이 프로덕션(prod 또는 dev)인지 확인합니다.
     */
    private boolean isProductionProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod")
            || Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }
}