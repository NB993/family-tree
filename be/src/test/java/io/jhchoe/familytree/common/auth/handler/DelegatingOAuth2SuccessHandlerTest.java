package io.jhchoe.familytree.common.auth.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] DelegatingOAuth2SuccessHandler")
class DelegatingOAuth2SuccessHandlerTest {

    @Mock
    private OAuth2JwtSuccessHandler jwtSuccessHandler;

    @Mock
    private OAuth2InviteSuccessHandler inviteSuccessHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private DelegatingOAuth2SuccessHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DelegatingOAuth2SuccessHandler(jwtSuccessHandler, inviteSuccessHandler);
    }

    @Test
    @DisplayName("invite_code가 있을 때 OAuth2InviteSuccessHandler로 위임한다")
    void delegates_to_invite_handler_when_invite_code_present() throws Exception {
        // given
        String inviteCode = "test-invite-code-123";
        Cookie authCookie = createAuthCookieWithInviteCode(inviteCode);
        given(request.getCookies()).willReturn(new Cookie[]{authCookie});

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(inviteSuccessHandler).should().onAuthenticationSuccess(request, response, authentication);
        then(jwtSuccessHandler).should(never()).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    @DisplayName("invite_code가 없을 때 OAuth2JwtSuccessHandler로 위임한다")
    void delegates_to_jwt_handler_when_invite_code_absent() throws Exception {
        // given
        Cookie authCookie = createAuthCookieWithoutInviteCode();
        given(request.getCookies()).willReturn(new Cookie[]{authCookie});

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(jwtSuccessHandler).should().onAuthenticationSuccess(request, response, authentication);
        then(inviteSuccessHandler).should(never()).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    @DisplayName("invite_code가 빈 문자열일 때 OAuth2JwtSuccessHandler로 위임한다")
    void delegates_to_jwt_handler_when_invite_code_is_blank() throws Exception {
        // given
        Cookie authCookie = createAuthCookieWithInviteCode("   ");
        given(request.getCookies()).willReturn(new Cookie[]{authCookie});

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(jwtSuccessHandler).should().onAuthenticationSuccess(request, response, authentication);
        then(inviteSuccessHandler).should(never()).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    @DisplayName("쿠키가 없을 때 OAuth2JwtSuccessHandler로 위임한다")
    void delegates_to_jwt_handler_when_cookie_absent() throws Exception {
        // given
        given(request.getCookies()).willReturn(null);

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(jwtSuccessHandler).should().onAuthenticationSuccess(request, response, authentication);
        then(inviteSuccessHandler).should(never()).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    @DisplayName("oauth2_auth_request 쿠키가 없을 때 OAuth2JwtSuccessHandler로 위임한다")
    void delegates_to_jwt_handler_when_auth_request_cookie_not_found() throws Exception {
        // given
        given(request.getCookies()).willReturn(new Cookie[]{
            new Cookie("other_cookie", "value")
        });

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(jwtSuccessHandler).should().onAuthenticationSuccess(request, response, authentication);
        then(inviteSuccessHandler).should(never()).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    @DisplayName("쿠키 역직렬화 실패 시 OAuth2JwtSuccessHandler로 위임한다")
    void delegates_to_jwt_handler_when_deserialization_fails() throws Exception {
        // given
        Cookie invalidCookie = new Cookie("oauth2_auth_request", "invalid-base64-data");
        given(request.getCookies()).willReturn(new Cookie[]{invalidCookie});

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(jwtSuccessHandler).should().onAuthenticationSuccess(request, response, authentication);
        then(inviteSuccessHandler).should(never()).onAuthenticationSuccess(request, response, authentication);
    }

    /**
     * invite_code를 포함한 oauth2_auth_request 쿠키를 생성하는 헬퍼 메서드
     */
    private Cookie createAuthCookieWithInviteCode(String inviteCode) {
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest
            .authorizationCode()
            .clientId("test-client-id")
            .authorizationUri("https://example.com/oauth2/authorize")
            .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
            .state("test-state")
            .additionalParameters(params -> params.put("invite_code", inviteCode))
            .build();

        byte[] serialized = SerializationUtils.serialize(authRequest);
        String encoded = Base64.getUrlEncoder().encodeToString(serialized);
        return new Cookie("oauth2_auth_request", encoded);
    }

    /**
     * invite_code가 없는 oauth2_auth_request 쿠키를 생성하는 헬퍼 메서드
     */
    private Cookie createAuthCookieWithoutInviteCode() {
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest
            .authorizationCode()
            .clientId("test-client-id")
            .authorizationUri("https://example.com/oauth2/authorize")
            .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
            .state("test-state")
            .build();

        byte[] serialized = SerializationUtils.serialize(authRequest);
        String encoded = Base64.getUrlEncoder().encodeToString(serialized);
        return new Cookie("oauth2_auth_request", encoded);
    }
}
