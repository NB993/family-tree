package io.jhchoe.familytree.common.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.handler.OAuth2JwtSuccessHandler.OAuth2JwtLoginResponse;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2JwtSuccessHandler 테스트")
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

    private OAuth2JwtSuccessHandler handler;
    private ObjectMapper objectMapper;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        handler = new OAuth2JwtSuccessHandler(generateJwtTokenUseCase, objectMapper);
        
        responseWriter = new StringWriter();
        given(response.getWriter()).willReturn(new PrintWriter(responseWriter));
    }

    @Test
    @DisplayName("OAuth2 로그인 성공 시 JWT 토큰을 발급하고 JSON 응답을 반환한다")
    void on_authentication_success_generates_jwt_tokens_and_returns_json_response() throws Exception {
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
                3600L // 1시간
        );
        given(generateJwtTokenUseCase.generateToken(any(GenerateJwtTokenCommand.class)))
                .willReturn(tokenResponse);

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        // HTTP 응답 헤더 검증
        then(response).should().setContentType(MediaType.APPLICATION_JSON_VALUE);
        then(response).should().setCharacterEncoding("UTF-8");
        then(response).should().setStatus(HttpServletResponse.SC_OK);
        
        // 토큰 생성 UseCase 호출 검증
        then(generateJwtTokenUseCase).should().generateToken(commandCaptor.capture());
        final GenerateJwtTokenCommand command = commandCaptor.getValue();
        assertThat(command.user()).isEqualTo(ftUser);
        
        // JSON 응답 검증
        final String jsonResponse = responseWriter.toString();
        final OAuth2JwtLoginResponse loginResponse = objectMapper.readValue(jsonResponse, OAuth2JwtLoginResponse.class);
        
        assertThat(loginResponse.success()).isTrue();
        assertThat(loginResponse.message()).isEqualTo("로그인이 성공적으로 완료되었습니다.");
        assertThat(loginResponse.tokenInfo()).isNotNull();
        assertThat(loginResponse.tokenInfo().accessToken()).isEqualTo("access_token_123");
        assertThat(loginResponse.tokenInfo().refreshToken()).isEqualTo("refresh_token_456");
        assertThat(loginResponse.userInfo()).isNotNull();
        assertThat(loginResponse.userInfo().id()).isEqualTo(1L);
        assertThat(loginResponse.userInfo().email()).isEqualTo("test@example.com");
        assertThat(loginResponse.userInfo().name()).isEqualTo("테스트 사용자");
        assertThat(loginResponse.errorMessage()).isNull();
    }

    @Test
    @DisplayName("토큰 생성 중 오류 발생 시 에러 응답을 반환한다")
    void on_token_generation_error_returns_error_response() throws Exception {
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
        // HTTP 응답 헤더 검증 (에러 상태)
        then(response).should().setContentType(MediaType.APPLICATION_JSON_VALUE);
        then(response).should().setCharacterEncoding("UTF-8");
        then(response).should().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        
        // JSON 에러 응답 검증
        final String jsonResponse = responseWriter.toString();
        final OAuth2JwtLoginResponse loginResponse = objectMapper.readValue(jsonResponse, OAuth2JwtLoginResponse.class);
        
        assertThat(loginResponse.success()).isFalse();
        assertThat(loginResponse.message()).isEqualTo("로그인에 실패했습니다.");
        assertThat(loginResponse.tokenInfo()).isNull();
        assertThat(loginResponse.userInfo()).isNull();
        assertThat(loginResponse.errorMessage()).isEqualTo("토큰 생성 중 오류가 발생했습니다. 다시 시도해주세요.");
    }

    @Test
    @DisplayName("ADMIN 권한 사용자의 JWT 토큰을 성공적으로 발급한다")
    void generate_jwt_token_for_admin_user_successfully() throws Exception {
        // given
        final FTUser adminUser = FTUser.ofJwtUser(
                2L,
                "관리자",
                "admin@example.com",
                "ADMIN"
        );
        given(authentication.getPrincipal()).willReturn(adminUser);
        
        final JwtTokenResponse tokenResponse = JwtTokenResponse.of(
                "admin_access_token",
                "admin_refresh_token",
                3600L // 1시간
        );
        given(generateJwtTokenUseCase.generateToken(any(GenerateJwtTokenCommand.class)))
                .willReturn(tokenResponse);

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        final String jsonResponse = responseWriter.toString();
        final OAuth2JwtLoginResponse loginResponse = objectMapper.readValue(jsonResponse, OAuth2JwtLoginResponse.class);
        
        assertThat(loginResponse.success()).isTrue();
        assertThat(loginResponse.userInfo().id()).isEqualTo(2L);
        assertThat(loginResponse.userInfo().email()).isEqualTo("admin@example.com");
        assertThat(loginResponse.userInfo().name()).isEqualTo("관리자");
        assertThat(loginResponse.tokenInfo().accessToken()).isEqualTo("admin_access_token");
    }
}
