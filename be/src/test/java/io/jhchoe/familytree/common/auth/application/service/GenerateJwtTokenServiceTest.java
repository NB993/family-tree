package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.config.JwtProperties;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] GenerateJwtTokenService")
class GenerateJwtTokenServiceTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;
    
    @Mock
    private SaveRefreshTokenUseCase saveRefreshTokenUseCase;
    
    @Mock
    private JwtProperties jwtProperties;
    
    @Captor
    private ArgumentCaptor<SaveRefreshTokenCommand> saveCommandCaptor;

    private GenerateJwtTokenService generateJwtTokenService;

    @BeforeEach
    void setUp() {
        generateJwtTokenService = new GenerateJwtTokenService(jwtTokenUtil, saveRefreshTokenUseCase, jwtProperties);
    }

    @Test
    @DisplayName("OAuth2 사용자의 JWT 토큰을 성공적으로 생성한다")
    void generate_token_for_oauth2_user_successfully() {
        // given
        final FTUser user = FTUser.ofOAuth2User(
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
        final GenerateJwtTokenCommand command = new GenerateJwtTokenCommand(user);
        
        final String expectedAccessToken = "access_token_123";
        final String expectedRefreshToken = "refresh_token_456";
        
        given(jwtTokenUtil.generateAccessToken(user))
                .willReturn(expectedAccessToken);
        given(jwtTokenUtil.generateRefreshToken(1L))
                .willReturn(expectedRefreshToken);
        given(jwtProperties.getAccessTokenExpiration())
                .willReturn(3600L); // 1시간

        // when
        final JwtTokenResponse response = generateJwtTokenService.generateToken(command);

        // then
        assertThat(response.accessToken()).isEqualTo(expectedAccessToken);
        assertThat(response.refreshToken()).isEqualTo(expectedRefreshToken);
        assertThat(response.expiresIn()).isEqualTo(3600L);
        
        // Refresh Token 저장 검증
        then(saveRefreshTokenUseCase).should().save(saveCommandCaptor.capture());
        final SaveRefreshTokenCommand saveCommand = saveCommandCaptor.getValue();
        assertThat(saveCommand.getUserId()).isEqualTo(1L);
        assertThat(saveCommand.getTokenHash()).isEqualTo(expectedRefreshToken);
        assertThat(saveCommand.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("JWT 사용자의 토큰을 성공적으로 생성한다")
    void generate_token_for_jwt_user_successfully() {
        // given
        final FTUser user = FTUser.ofJwtUser(
                2L,
                "JWT 사용자",
                "jwt@example.com",
                "ADMIN"
        );
        final GenerateJwtTokenCommand command = new GenerateJwtTokenCommand(user);
        
        final String expectedAccessToken = "admin_access_token";
        final String expectedRefreshToken = "admin_refresh_token";
        
        given(jwtTokenUtil.generateAccessToken(user))
                .willReturn(expectedAccessToken);
        given(jwtTokenUtil.generateRefreshToken(2L))
                .willReturn(expectedRefreshToken);
        given(jwtProperties.getAccessTokenExpiration())
                .willReturn(3600L);

        // when
        final JwtTokenResponse response = generateJwtTokenService.generateToken(command);

        // then
        assertThat(response.accessToken()).isEqualTo(expectedAccessToken);
        assertThat(response.refreshToken()).isEqualTo(expectedRefreshToken);
        
        // ADMIN 권한으로 토큰 생성 확인
        then(jwtTokenUtil).should().generateAccessToken(user);
    }

    @Test
    @DisplayName("토큰 생성 시 만료 시간이 올바르게 설정된다")
    void generate_token_sets_correct_expiration_times() {
        // given
        final FTUser user = FTUser.ofOAuth2User(
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
        final GenerateJwtTokenCommand command = new GenerateJwtTokenCommand(user);
        
        given(jwtTokenUtil.generateAccessToken(user))
                .willReturn("access_token");
        given(jwtTokenUtil.generateRefreshToken(1L))
                .willReturn("refresh_token");
        given(jwtProperties.getAccessTokenExpiration())
                .willReturn(3600L); // 1시간

        // when
        final JwtTokenResponse response = generateJwtTokenService.generateToken(command);

        // then
        assertThat(response.expiresIn()).isEqualTo(3600L); // 1시간 = 3600초
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }
}
