package io.jhchoe.familytree.common.auth.handler;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.config.CorsProperties;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoCommand;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoUseCase;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] OAuth2InviteSuccessHandler")
class OAuth2InviteSuccessHandlerTest {

    @Mock
    private CorsProperties corsProperties;

    @Mock
    private SaveInviteResponseWithKakaoUseCase saveInviteResponseWithKakaoUseCase;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Captor
    private ArgumentCaptor<SaveInviteResponseWithKakaoCommand> commandCaptor;

    @Captor
    private ArgumentCaptor<String> redirectUrlCaptor;

    private OAuth2InviteSuccessHandler handler;

    @BeforeEach
    void setUp() {
        handler = new OAuth2InviteSuccessHandler(corsProperties, saveInviteResponseWithKakaoUseCase);
    }

    @Test
    @DisplayName("additionalParameters에서 invite_code를 추출하여 UseCase에 전달한다")
    void extracts_invite_code_from_additional_parameters_and_passes_to_use_case() throws Exception {
        // given
        String inviteCode = "test-invite-code-123";
        String kakaoId = "kakao_12345";
        String email = "test@kakao.com";
        String name = "테스트 사용자";
        String profileUrl = "https://kakao.com/profile.jpg";

        FTUser ftUser = createKakaoUser(kakaoId, email, name, profileUrl);
        given(authentication.getPrincipal()).willReturn(ftUser);

        Cookie authCookie = createAuthCookieWithInviteCode(inviteCode);
        given(request.getCookies()).willReturn(new Cookie[]{authCookie});

        given(saveInviteResponseWithKakaoUseCase.save(any(SaveInviteResponseWithKakaoCommand.class)))
            .willReturn(1L);

        given(corsProperties.getFrontendUrl()).willReturn("http://localhost:3000");

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(saveInviteResponseWithKakaoUseCase).should().save(commandCaptor.capture());
        SaveInviteResponseWithKakaoCommand command = commandCaptor.getValue();

        assertThat(command.getInviteCode()).isEqualTo(inviteCode);
        assertThat(command.getKakaoId()).isEqualTo(kakaoId);
        assertThat(command.getEmail()).isEqualTo(email);
        assertThat(command.getName()).isEqualTo(name);
        assertThat(command.getProfileUrl()).isEqualTo(profileUrl);

        then(response).should().sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();
        assertThat(redirectUrl).isEqualTo("http://localhost:3000/invite/" + inviteCode + "/callback?success=true");
    }

    @Test
    @DisplayName("invite_code가 없을 때 에러 페이지로 리다이렉트한다")
    void redirects_to_error_page_when_invite_code_absent() throws Exception {
        // given
        FTUser ftUser = createKakaoUser("kakao_123", "test@kakao.com", "테스트", "profile.jpg");
        given(authentication.getPrincipal()).willReturn(ftUser);

        Cookie authCookie = createAuthCookieWithoutInviteCode();
        given(request.getCookies()).willReturn(new Cookie[]{authCookie});

        given(corsProperties.getFrontendUrl()).willReturn("http://localhost:3000");

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(saveInviteResponseWithKakaoUseCase).should(never()).save(any());

        then(response).should().sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();
        String expectedError = URLEncoder.encode("초대 코드를 찾을 수 없습니다.", StandardCharsets.UTF_8);
        assertThat(redirectUrl).contains("/invite/callback?error=" + expectedError);
    }

    @Test
    @DisplayName("invite_code가 빈 문자열일 때 에러 페이지로 리다이렉트한다")
    void redirects_to_error_page_when_invite_code_is_blank() throws Exception {
        // given
        FTUser ftUser = createKakaoUser("kakao_123", "test@kakao.com", "테스트", "profile.jpg");
        given(authentication.getPrincipal()).willReturn(ftUser);

        Cookie authCookie = createAuthCookieWithInviteCode("   ");
        given(request.getCookies()).willReturn(new Cookie[]{authCookie});

        given(corsProperties.getFrontendUrl()).willReturn("http://localhost:3000");

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(saveInviteResponseWithKakaoUseCase).should(never()).save(any());

        then(response).should().sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();
        String expectedError = URLEncoder.encode("초대 코드를 찾을 수 없습니다.", StandardCharsets.UTF_8);
        assertThat(redirectUrl).contains("/invite/callback?error=" + expectedError);
    }

    @Test
    @DisplayName("쿠키가 없을 때 에러 페이지로 리다이렉트한다")
    void redirects_to_error_page_when_cookie_absent() throws Exception {
        // given
        FTUser ftUser = createKakaoUser("kakao_123", "test@kakao.com", "테스트", "profile.jpg");
        given(authentication.getPrincipal()).willReturn(ftUser);
        given(request.getCookies()).willReturn(null);

        given(corsProperties.getFrontendUrl()).willReturn("http://localhost:3000");

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(saveInviteResponseWithKakaoUseCase).should(never()).save(any());

        then(response).should().sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();
        String expectedError = URLEncoder.encode("초대 코드를 찾을 수 없습니다.", StandardCharsets.UTF_8);
        assertThat(redirectUrl).contains("/invite/callback?error=" + expectedError);
    }

    @Test
    @DisplayName("UseCase에서 FTException 발생 시 에러 페이지로 리다이렉트한다")
    void redirects_to_error_page_when_use_case_throws_ft_exception() throws Exception {
        // given
        String inviteCode = "test-invite-code-123";
        FTUser ftUser = createKakaoUser("kakao_123", "test@kakao.com", "테스트", "profile.jpg");
        given(authentication.getPrincipal()).willReturn(ftUser);

        Cookie authCookie = createAuthCookieWithInviteCode(inviteCode);
        given(request.getCookies()).willReturn(new Cookie[]{authCookie});

        FTException exception = new FTException(InviteExceptionCode.INVITE_EXPIRED);
        given(saveInviteResponseWithKakaoUseCase.save(any(SaveInviteResponseWithKakaoCommand.class)))
            .willThrow(exception);

        given(corsProperties.getFrontendUrl()).willReturn("http://localhost:3000");

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(response).should().sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();
        String expectedError = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
        assertThat(redirectUrl).contains("/invite/" + inviteCode + "/callback?error=" + expectedError);
    }

    @Test
    @DisplayName("UseCase에서 일반 Exception 발생 시 에러 페이지로 리다이렉트한다")
    void redirects_to_error_page_when_use_case_throws_general_exception() throws Exception {
        // given
        String inviteCode = "test-invite-code-123";
        FTUser ftUser = createKakaoUser("kakao_123", "test@kakao.com", "테스트", "profile.jpg");
        given(authentication.getPrincipal()).willReturn(ftUser);

        Cookie authCookie = createAuthCookieWithInviteCode(inviteCode);
        given(request.getCookies()).willReturn(new Cookie[]{authCookie});

        given(saveInviteResponseWithKakaoUseCase.save(any(SaveInviteResponseWithKakaoCommand.class)))
            .willThrow(new RuntimeException("시스템 오류"));

        given(corsProperties.getFrontendUrl()).willReturn("http://localhost:3000");

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(response).should().sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();
        String expectedError = URLEncoder.encode("초대 수락 처리 중 오류가 발생했습니다.", StandardCharsets.UTF_8);
        assertThat(redirectUrl).contains("/invite/" + inviteCode + "/callback?error=" + expectedError);
    }

    /**
     * 카카오 사용자를 생성하는 헬퍼 메서드
     */
    private FTUser createKakaoUser(String kakaoId, String email, String name, String profileUrl) {
        return FTUser.ofOAuth2User(
            1L,
            name,
            email,
            OAuth2Provider.KAKAO,
            Map.of(
                "id", kakaoId,
                "kakao_account", Map.of(
                    "email", email,
                    "profile", Map.of(
                        "nickname", name,
                        "profile_image_url", profileUrl
                    )
                )
            )
        );
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
