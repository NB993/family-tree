package io.jhchoe.familytree.common.auth.handler;

import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] OAuth2JwtSuccessHandler")
class OAuth2JwtSuccessHandlerTest {

    @Mock
    private GenerateJwtTokenUseCase generateJwtTokenUseCase;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private Authentication authentication;
    
    @Captor
    private ArgumentCaptor<GenerateJwtTokenCommand> commandCaptor;
    
    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;
    
    @Captor
    private ArgumentCaptor<String> redirectUrlCaptor;

    private OAuth2JwtSuccessHandler handler;

    @BeforeEach
    void setUp() {
        handler = new OAuth2JwtSuccessHandler(generateJwtTokenUseCase);
    }

    @Test
    @DisplayName("OAuth2 로그인 성공 시 JWT 토큰을 HttpOnly 쿠키로 설정하고 프론트엔드로 리다이렉트한다")
    void on_authentication_success_sets_httponly_cookies_and_redirects() throws Exception {
        // given
        final FTUser ftUser = FTUser.ofOAuth2User(
                1L,
                "테스트 사용자",
                "test@example.com",
                OAuth2Provider.GOOGLE,
                Map.of(
                    "sub", "google_123",
                    "name", "테스트 사용자",
                    "email", "test@example.com",
                    "picture", "https://example.com/profile.jpg"
                )
        );
        given(authentication.getPrincipal()).willReturn(ftUser);
        
        final JwtTokenResponse tokenResponse = JwtTokenResponse.of(
                "access_token_123",
                "refresh_token_456", 
                3600L
        );
        given(generateJwtTokenUseCase.generateToken(any(GenerateJwtTokenCommand.class)))
                .willReturn(tokenResponse);

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        // 토큰 생성 UseCase 호출 검증
        then(generateJwtTokenUseCase).should().generateToken(commandCaptor.capture());
        final GenerateJwtTokenCommand command = commandCaptor.getValue();
        assertThat(command.user()).isEqualTo(ftUser);
        
        // HttpOnly 쿠키 설정 검증
        then(response).should(times(2)).addCookie(cookieCaptor.capture());

        var cookies = cookieCaptor.getAllValues();
        assertThat(cookies).hasSize(2);
        
        // Access Token 쿠키 검증
        Cookie accessTokenCookie = cookies.stream()
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("accessToken 쿠키가 설정되지 않았습니다"));
        
        assertThat(accessTokenCookie.getValue()).isEqualTo("access_token_123");
        assertThat(accessTokenCookie.isHttpOnly()).isTrue();
        assertThat(accessTokenCookie.getSecure()).isFalse(); // 개발환경
        assertThat(accessTokenCookie.getPath()).isEqualTo("/");
        assertThat(accessTokenCookie.getMaxAge()).isEqualTo(300); // 5분
        
        // Refresh Token 쿠키 검증
        Cookie refreshTokenCookie = cookies.stream()
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("refreshToken 쿠키가 설정되지 않았습니다"));
        
        assertThat(refreshTokenCookie.getValue()).isEqualTo("refresh_token_456");
        assertThat(refreshTokenCookie.isHttpOnly()).isTrue();
        assertThat(refreshTokenCookie.getSecure()).isFalse(); // 개발환경
        assertThat(refreshTokenCookie.getPath()).isEqualTo("/");
        assertThat(refreshTokenCookie.getMaxAge()).isEqualTo(7 * 24 * 60 * 60); // 7일
        
        // 리다이렉트 URL 검증 (성공 여부만 포함, 사용자 정보는 API로 조회)
        then(response).should().sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();
        
        assertThat(redirectUrl).isEqualTo("http://localhost:3000/auth/callback?success=true");
        
        // 보안: 토큰과 개인정보가 URL에 포함되지 않았는지 확인
        assertThat(redirectUrl).doesNotContain("access_token_123");
        assertThat(redirectUrl).doesNotContain("refresh_token_456");
        assertThat(redirectUrl).doesNotContain("userId");
        assertThat(redirectUrl).doesNotContain("email");
        assertThat(redirectUrl).doesNotContain("name");
    }

    @Test
    @DisplayName("토큰 생성 중 오류 발생 시 에러와 함께 프론트엔드로 리다이렉트한다")
    void on_token_generation_error_redirects_with_error() throws Exception {
        // given
        final FTUser ftUser = FTUser.ofOAuth2User(
                1L,
                "테스트 사용자",
                "test@example.com",
                OAuth2Provider.GOOGLE,
                Map.of(
                    "sub", "google_123",
                    "name", "테스트 사용자",
                    "email", "test@example.com",
                    "picture", "https://example.com/profile.jpg"
                )
        );
        given(authentication.getPrincipal()).willReturn(ftUser);
        given(generateJwtTokenUseCase.generateToken(any(GenerateJwtTokenCommand.class)))
                .willThrow(new RuntimeException("토큰 생성 실패"));

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        // 에러 리다이렉트 URL 검증
        then(response).should().sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();
        
        assertThat(redirectUrl).contains("http://localhost:3000/auth/callback");
        assertThat(redirectUrl).contains("success=false");
        assertThat(redirectUrl).contains("error=%ED%86%A0%ED%81%B0+%EC%83%9D%EC%84%B1+%EC%8B%A4%ED%8C%A8"); // URL 인코딩됨
        
        // 쿠키가 설정되지 않았는지 확인 (토큰 생성 실패 시)
        then(response).should(never()).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("정상적인 사용자라면 개인정보 없이 성공 리다이렉트만 수행한다")
    void redirects_with_success_only_for_valid_user() throws Exception {
        // given
        final FTUser ftUser = FTUser.ofOAuth2User(
                2L,
                "홍길동 & 김철수",
                "user+test@example.com",
                OAuth2Provider.KAKAO,
                Map.of(
                    "id", 2L,
                    "kakao_account", Map.of(
                        "email", "user+test@example.com",
                        "profile", Map.of("nickname", "홍길동 & 김철수")
                    )
                )
        );
        given(authentication.getPrincipal()).willReturn(ftUser);
        
        final JwtTokenResponse tokenResponse = JwtTokenResponse.of(
                "access_token_special",
                "refresh_token_special",
                3600L
        );
        given(generateJwtTokenUseCase.generateToken(any(GenerateJwtTokenCommand.class)))
                .willReturn(tokenResponse);

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        // 개인정보 없이 성공만 알리는 깔끔한 리다이렉트 URL 확인
        then(response).should().sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();

        assertThat(redirectUrl).isEqualTo("http://localhost:3000/auth/callback?success=true");

        // 쿠키에는 토큰이 정상적으로 설정되었는지 확인 (AT, RT 각 한 번씩)
        then(response).should(times(2)).addCookie(cookieCaptor.capture());

        var cookies = cookieCaptor.getAllValues();
        Cookie accessToken = cookies.stream()
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst()
                .orElseThrow();

        Cookie refreshToken = cookies.stream()
            .filter(cookie -> "refreshToken".equals(cookie.getName()))
            .findFirst()
            .orElseThrow();
        
        assertThat(accessToken.getValue()).isEqualTo("access_token_special");
    }

}
