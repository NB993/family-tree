package io.jhchoe.familytree.common.auth.repository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] HttpCookieOAuth2AuthorizationRequestRepository")
class HttpCookieOAuth2AuthorizationRequestRepositoryTest {

    @Mock
    private Environment environment;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Captor
    private ArgumentCaptor<String> headerCaptor;

    private HttpCookieOAuth2AuthorizationRequestRepository repository;

    @BeforeEach
    void setUp() {
        repository = new HttpCookieOAuth2AuthorizationRequestRepository(environment);
    }

    @Test
    @DisplayName("invite_code가 URL 파라미터에 있을 때 OAuth2AuthorizationRequest의 additionalParameters에 포함된다")
    void save_includes_invite_code_in_additional_parameters_when_present() {
        // given
        String inviteCode = "test-invite-code-123";
        given(request.getParameter("invite_code")).willReturn(inviteCode);
        given(environment.getActiveProfiles()).willReturn(new String[]{"local"});

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest
            .authorizationCode()
            .clientId("test-client-id")
            .authorizationUri("https://example.com/oauth2/authorize")
            .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
            .state("test-state")
            .build();

        // when
        repository.saveAuthorizationRequest(authorizationRequest, request, response);

        // then
        then(response).should().addHeader(anyString(), headerCaptor.capture());
        String setCookieHeader = headerCaptor.getValue();

        // Set-Cookie 헤더에서 쿠키 값 추출
        String cookieValue = extractCookieValue(setCookieHeader);
        OAuth2AuthorizationRequest savedRequest = deserializeAuthorizationRequest(cookieValue);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getAdditionalParameters()).containsKey("invite_code");
        assertThat(savedRequest.getAdditionalParameters().get("invite_code")).isEqualTo(inviteCode);
    }

    @Test
    @DisplayName("invite_code가 없을 때도 정상적으로 OAuth2AuthorizationRequest를 저장한다")
    void save_works_normally_when_invite_code_is_absent() {
        // given
        given(request.getParameter("invite_code")).willReturn(null);
        given(environment.getActiveProfiles()).willReturn(new String[]{"local"});

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest
            .authorizationCode()
            .clientId("test-client-id")
            .authorizationUri("https://example.com/oauth2/authorize")
            .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
            .state("test-state")
            .build();

        // when
        repository.saveAuthorizationRequest(authorizationRequest, request, response);

        // then
        then(response).should().addHeader(anyString(), headerCaptor.capture());
        String setCookieHeader = headerCaptor.getValue();

        String cookieValue = extractCookieValue(setCookieHeader);
        OAuth2AuthorizationRequest savedRequest = deserializeAuthorizationRequest(cookieValue);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getAdditionalParameters()).doesNotContainKey("invite_code");
    }

    @Test
    @DisplayName("invite_code가 빈 문자열일 때는 additionalParameters에 포함하지 않는다")
    void save_excludes_invite_code_when_blank() {
        // given
        given(request.getParameter("invite_code")).willReturn("   ");
        given(environment.getActiveProfiles()).willReturn(new String[]{"local"});

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest
            .authorizationCode()
            .clientId("test-client-id")
            .authorizationUri("https://example.com/oauth2/authorize")
            .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
            .state("test-state")
            .build();

        // when
        repository.saveAuthorizationRequest(authorizationRequest, request, response);

        // then
        then(response).should().addHeader(anyString(), headerCaptor.capture());
        String setCookieHeader = headerCaptor.getValue();

        String cookieValue = extractCookieValue(setCookieHeader);
        OAuth2AuthorizationRequest savedRequest = deserializeAuthorizationRequest(cookieValue);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getAdditionalParameters()).doesNotContainKey("invite_code");
    }

    @Test
    @DisplayName("authorizationRequest가 null일 때 쿠키를 삭제한다")
    void save_removes_cookies_when_authorization_request_is_null() {
        // given
        given(environment.getActiveProfiles()).willReturn(new String[]{"local"});

        // when
        repository.saveAuthorizationRequest(null, request, response);

        // then
        then(response).should().addHeader(anyString(), headerCaptor.capture());
        String setCookieHeader = headerCaptor.getValue();

        // Max-Age=0으로 쿠키 삭제 확인
        assertThat(setCookieHeader).contains("Max-Age=0");
    }

    @Test
    @DisplayName("쿠키에서 OAuth2AuthorizationRequest를 로드할 때 invite_code가 포함되어 있다")
    void load_returns_authorization_request_with_invite_code() {
        // given
        String inviteCode = "test-invite-code-456";
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest
            .authorizationCode()
            .clientId("test-client-id")
            .authorizationUri("https://example.com/oauth2/authorize")
            .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
            .state("test-state")
            .additionalParameters(params -> params.put("invite_code", inviteCode))
            .build();

        // 직렬화
        byte[] serialized = SerializationUtils.serialize(authorizationRequest);
        String encoded = Base64.getUrlEncoder().encodeToString(serialized);

        Cookie authCookie = new Cookie("oauth2_auth_request", encoded);
        given(request.getCookies()).willReturn(new Cookie[]{authCookie});

        // when
        OAuth2AuthorizationRequest loadedRequest = repository.loadAuthorizationRequest(request);

        // then
        assertThat(loadedRequest).isNotNull();
        assertThat(loadedRequest.getAdditionalParameters()).containsKey("invite_code");
        assertThat(loadedRequest.getAdditionalParameters().get("invite_code")).isEqualTo(inviteCode);
    }

    @Test
    @DisplayName("쿠키가 없을 때 loadAuthorizationRequest는 null을 반환한다")
    void load_returns_null_when_cookie_is_absent() {
        // given
        given(request.getCookies()).willReturn(null);

        // when
        OAuth2AuthorizationRequest loadedRequest = repository.loadAuthorizationRequest(request);

        // then
        assertThat(loadedRequest).isNull();
    }

    /**
     * Set-Cookie 헤더에서 쿠키 값을 추출하는 헬퍼 메서드
     */
    private String extractCookieValue(String setCookieHeader) {
        String[] parts = setCookieHeader.split(";");
        String nameValue = parts[0];
        return nameValue.substring(nameValue.indexOf("=") + 1);
    }

    /**
     * Base64 인코딩된 문자열을 OAuth2AuthorizationRequest로 역직렬화하는 헬퍼 메서드
     */
    private OAuth2AuthorizationRequest deserializeAuthorizationRequest(String encoded) {
        byte[] decoded = Base64.getUrlDecoder().decode(encoded);
        return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(decoded);
    }
}
